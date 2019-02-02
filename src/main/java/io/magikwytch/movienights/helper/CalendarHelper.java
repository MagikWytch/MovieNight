package io.magikwytch.movienights.helper;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import io.magikwytch.movienights.domain.entity.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarHelper {


    public GoogleCredential getRefreshedCredentials(String refreshCode, String CLIENT_ID, String CLIENT_SECRET) {
        try {
            GoogleTokenResponse response = new GoogleRefreshTokenRequest(
                    new NetHttpTransport(), JacksonFactory.getDefaultInstance(), refreshCode, CLIENT_ID, CLIENT_SECRET)
                    .execute();

            return new GoogleCredential().setAccessToken(response.getAccessToken());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public Calendar getUserCalendar(User user) {

        // Use an accessToken previously gotten to call Google's API
        GoogleCredential credential = new GoogleCredential().setAccessToken(user.getAccessToken());
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

    public boolean timeIsFree(LocalDateTime startParamater, LocalDateTime endParamater, List<Event> events) {
        if (events.size() == 0) {
            return true;
        }

        for (Event event : events) {
            LocalDateTime start = getDateTime(event.getStart().getDateTime());
            LocalDateTime end = getDateTime(event.getEnd().getDateTime());

            if (start == null || end == null) {
                return false;
            } else if (start.isAfter(startParamater) && start.isBefore(endParamater)) {
                return false;
            } else if (end.isAfter(startParamater) && end.isBefore(endParamater)) {
                return false;
            }
        }
        return true;
    }

    private static LocalDateTime getDateTime(DateTime dateTime) {
        try {
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            return LocalDateTime.parse(dateTime.toStringRfc3339(), format);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
