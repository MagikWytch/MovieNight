package io.magikwytch.movienights.controller;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import io.magikwytch.movienights.entity.FreeTimePeriod;
import io.magikwytch.movienights.entity.User;
import io.magikwytch.movienights.helper.CalendarHelper;
import io.magikwytch.movienights.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CalendarController {
    @Autowired
    private UserRepository userRepository;

    CalendarHelper calendarHelper = new CalendarHelper();

    private String CLIENT_ID = "456752195903-qnk1em5fckgkobmj4lf5ionvb8rc7te4.apps.googleusercontent.com";
    private String CLIENT_SECRET = "5gKlEdTE6Q9i4AKv5t2LhI3F";

    @RequestMapping(value = "/storeauthcode", method = RequestMethod.POST)
    public String storeauthcode(@RequestBody String code, @RequestHeader("X-Requested-With") String encoding) {
        if (encoding == null || encoding.isEmpty()) {
            // Without the `X-Requested-With` header, this request could be forged. Aborts.
            return "Error, wrong headers";
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


        // Debugging purposes, should probably be stored in the database instead (At least "givenName").
        System.out.println("userId: " + userId);
        System.out.println("email: " + email);
        System.out.println("givenName: " + givenName);

        User user = new User(userId, givenName, email, refreshToken, accessToken, expiresAt);
        userRepository.save(user);

        return "OK";

    }

    @RequestMapping(value = "/getFreeTime", method = RequestMethod.GET)
    private List<FreeTimePeriod> getFreeTime() {

        List<FreeTimePeriod> freeTimes = new ArrayList<>();
        List<Event> events = new ArrayList<>();

        List<User> users = userRepository.findAll();
        for (User user : users) {

            Calendar calendar = calendarHelper.getUserCalendar(user);
            List<Event> userEvent = calendarHelper.getEvents(calendar);

            if (userEvent.size() > 0) {
                events.addAll(userEvent);
            }
        }

        LocalDateTime localStartTime = LocalDate.now().atTime(18, 0);
        LocalDateTime localEndTime = LocalDate.now().atTime(23, 59);


        if (events.size() == 0) {

        }

        for (Event event : events) {


            DateTime start = event.getStart().getDateTime();
            if (start == null) {
                start = event.getStart().getDate();
            }

            DateTime end = event.getEnd().getDateTime();
            if (end == null) {
                end = event.getStart().getDate();
            }


        }


        return freeTimes;
    }

    /*@RequestMapping(value = "/createEvent", method = RequestMethod.POST)
    private EventAttendee[] bookMovieNight() {
        //movie?=batman+begins
        List<User> users = userRepository.findAll();
        int amountOfUsers = users.size();

        EventAttendee[] attendees = new EventAttendee[amountOfUsers];
        for (User user : users) {
            new EventAttendee().setEmail(user.getUserEmail());
        }

        return attendees;
    }*/


}
