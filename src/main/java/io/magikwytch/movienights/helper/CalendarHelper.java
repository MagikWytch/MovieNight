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
import java.util.ArrayList;
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
        int millisInADay = 86400000;

        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime oneWeekInTheFutureFromNow = new DateTime(System.currentTimeMillis() + days * millisInADay);
        Events events = null;
        try {
            events = calendar.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setTimeMax(oneWeekInTheFutureFromNow)
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
}
