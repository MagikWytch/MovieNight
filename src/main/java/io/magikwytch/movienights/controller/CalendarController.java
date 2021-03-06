package io.magikwytch.movienights.controller;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import io.magikwytch.movienights.domain.FreeTimePeriod;
import io.magikwytch.movienights.domain.entity.User;
import io.magikwytch.movienights.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    @Autowired
    private UserRepository userRepository;

    //private CalendarHelper calendarHelper = new CalendarHelper();

    private String CLIENT_ID = "456752195903-qnk1em5fckgkobmj4lf5ionvb8rc7te4.apps.googleusercontent.com";
    private String CLIENT_SECRET = "5gKlEdTE6Q9i4AKv5t2LhI3F";

    @RequestMapping(value = "/storeauthcode", method = RequestMethod.POST)
    public ResponseEntity<String> storeauthcode(@RequestBody String code, @RequestHeader("X-Requested-With") String encoding) {
        if (encoding == null || encoding.isEmpty()) {
            // Without the `X-Requested-With` header, this request could be forged. Aborts.
            return new ResponseEntity<>("Error, wrong headers", HttpStatus.BAD_REQUEST);
        }

        GoogleTokenResponse tokenResponse = null;
        try {
            tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    "https://www.googleapis.com/oauth2/v4/token",
                    CLIENT_ID,
                    CLIENT_SECRET,
                    code,
                    "http://localhost:8080")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Store these 3in your DB
        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();
        Long expiresAt = System.currentTimeMillis() + (tokenResponse.getExpiresInSeconds() * 1000);

        // Debug purpose only
        System.out.println("accessToken: " + accessToken);
        System.out.println("refreshToken: " + refreshToken);
        System.out.println("expiresAt: " + expiresAt);

        // Get profile info from ID token (Obtained at the last step of OAuth2)
        GoogleIdToken idToken = null;
        try {
            idToken = tokenResponse.parseIdToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        GoogleIdToken.Payload payload = idToken.getPayload();

        // Use THIS ID as a key to identify a google user-account.
        String userId = payload.getSubject();

        String email = payload.getEmail();
        String givenName = (String) payload.get("given_name");

        User user = new User(userId, givenName, email, refreshToken, accessToken, expiresAt);
        userRepository.save(user);


        return new ResponseEntity<>("OK", HttpStatus.OK);

    }


    @RequestMapping(value = "/bookMovieNight/{movieTitle}/{movieRuntime}/{sTime}/{eTime}", method = RequestMethod.POST)
    public ResponseEntity<String> bookMovieNight(
            @PathVariable("movieTitle") String movieTitle,
            @PathVariable("movieRuntime") String movieRuntime,
            @PathVariable("sTime") String sTime,
            @PathVariable("eTime") String eTime) throws IOException {


        List<Calendar> calendars = new ArrayList<>();
        List<User> users = userRepository.getAllByUserIDNotNull();
        for (User user : users) {
            calendars.add(getUserCalendar(user));
        }

        DateTime start = DateTime.parseRfc3339(sTime);

        DateTime end = DateTime.parseRfc3339(eTime);

        Event event = setEventDetails(movieTitle, movieRuntime, start, end);

        event.setAttendees(setEventAttendees(users));

        String calendarId = "primary";
        for (Calendar calendar : calendars) {
            calendar.events()
                    .insert(calendarId, event)
                    .execute();
        }

        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    private Event setEventDetails(String movieTitle, String movieRuntime, DateTime startTime, DateTime endTime) {
        Event event = new Event();

        event.setSummary("Movie : " + movieTitle + ", " + movieRuntime)
                .setDescription("You are invited to a movie night with your friends.");

        EventDateTime start = new EventDateTime()
                .setDateTime(startTime);
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(endTime);
        event.setEnd(end);

        return event;
    }

    private List<EventAttendee> setEventAttendees(List<User> users) {

        List<EventAttendee> attendees = new ArrayList<>();
        for (User user : users) {
            attendees.add(new EventAttendee().setEmail(user.getUserEmail()));
        }
        return attendees;
    }

    @RequestMapping(value = "/getFreeTime", method = RequestMethod.GET)
    public List<FreeTimePeriod> getFreeTime() {
        List<FreeTimePeriod> freeTimes = new ArrayList<>();
        List<Event> events = new ArrayList<>();

        List<User> users = userRepository.getAllByUserIDNotNull();
        for (User user : users) {
            Calendar calendar = getUserCalendar(user);
            List<Event> userEvent = getEvents(calendar);

            if (userEvent != null || userEvent.size() != 0) {
                events.addAll(userEvent);
            }
        }

        LocalDateTime movieNightStartParameter = LocalDate.now().atTime(18, 0);
        LocalDateTime movieNightEndParameter = LocalDate.now().atTime(23, 0);

        for (int i = 0; i < 7; i++) {

            movieNightStartParameter = movieNightStartParameter.plusDays(1);
            movieNightEndParameter = movieNightEndParameter.plusDays(1);

            if (timeIsFree(movieNightStartParameter, movieNightEndParameter, events)) {
                freeTimes.add(new FreeTimePeriod(movieNightStartParameter, movieNightEndParameter));
            }

        }
        return freeTimes;
    }

    public Calendar getUserCalendar(User user) {

        User googleUser = refreshToken(user);

        // Use an accessToken previously gotten to call Google's API
        GoogleCredential credential = new GoogleCredential().setAccessToken(googleUser.getAccessToken());
        Calendar calendar =
                new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                        .setApplicationName("Movie Nights")
                        .build();

        return calendar;
    }

    public List<Event> getEvents(Calendar calendar) {

        int days = 7;
        int millisPerDay = 86400000;
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime oneWeekFromNow = new DateTime(System.currentTimeMillis() + days * millisPerDay);

        Events events = null;
        try {
            events = calendar.events().list("primary")
                    .setTimeMin(now)
                    .setTimeMax(oneWeekFromNow)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (events == null) {
            return new ArrayList<>();
        }

        return events.getItems();
    }

    public Boolean timeIsFree(LocalDateTime startParameter, LocalDateTime endParameter, List<Event> events) {
        if (events.size() == 0) {
            return true;
        }


        for (Event event : events) {

            LocalDateTime start;
            LocalDateTime end;

            DateTime nullIfDateOnly = event.getStart().getDateTime();
            if (nullIfDateOnly == null) {
                String addTimeStartToDate = String.format("%sT00:00:00.000+01:00", event.getStart().getDate());
                String addTimeEndToDate = String.format("%sT23:59:59.000+01:00", event.getStart().getDate());
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS+01:00");
                start = LocalDateTime.parse(addTimeStartToDate, format);
                end = LocalDateTime.parse(addTimeEndToDate, format);
            } else {
                start = convertToLocalDate(event.getStart().getDateTime());
                end = convertToLocalDate(event.getEnd().getDateTime());
            }

            if (start.isAfter(startParameter) && start.isBefore(endParameter)) {
                return false;
            } else if (end.isAfter(startParameter) && end.isBefore(endParameter)) {
                return false;
            } else if (start.isBefore(startParameter) && end.isAfter(endParameter)) {
                return false;
            }
        }
        return true;

    }

    private static LocalDateTime convertToLocalDate(DateTime dateTime) {
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS+01:00");
            return LocalDateTime.parse(dateTime.toStringRfc3339(), format);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User refreshToken(User user) {
        Long expiresAt = user.getAccessTokenExpiration();
        Long currentTime = System.currentTimeMillis();

        if (expiresAt < currentTime) {
            GoogleCredential credential = getRefreshedCredentials(user.getRefreshToken());
            String accessToken = credential.getAccessToken();

            user.setAccessToken(accessToken);
            user.setAccessTokenExpiration(currentTime + 3600000);

            userRepository.save(user);
        }

        return user;
    }

    public GoogleCredential getRefreshedCredentials(String refreshCode) {
        try {
            GoogleTokenResponse response = new GoogleRefreshTokenRequest(
                    new NetHttpTransport(), JacksonFactory.getDefaultInstance(), refreshCode,
                    CLIENT_ID,
                    CLIENT_SECRET)
                    .execute();

            return new GoogleCredential().setAccessToken(response.getAccessToken());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}


