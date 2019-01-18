package io.magikwytch.movienights.entity;

import com.google.api.client.util.DateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CalendarEvent {

    @Id
    private Long id;
    private String summary;
    private DateTime startTime;
    private DateTime endTime;
    private DateTime startDate;
    private DateTime endDate;

    private CalendarEvent() {
    }

    public CalendarEvent(Long id, String summary, DateTime startTime, DateTime endTime, DateTime startDate, DateTime endDate) {
        this.id = id;
        this.summary = summary;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }


}
