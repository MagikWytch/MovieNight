package io.magikwytch.movienights.domain;

import java.time.LocalDateTime;

public class FreeTimePeriod {

    LocalDateTime start;
    LocalDateTime end;

    public FreeTimePeriod() {
    }

    public FreeTimePeriod(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "FreeTimePeriod{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
