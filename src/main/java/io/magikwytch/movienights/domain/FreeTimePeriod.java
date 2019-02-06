package io.magikwytch.movienights.domain;

import java.time.LocalDateTime;

public class FreeTimePeriod {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public FreeTimePeriod() {
    }

    public FreeTimePeriod(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTIme(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "FreeTimePeriod{" +
                "start=" + startTime +
                ", end=" + endTime +
                '}';
    }
}
