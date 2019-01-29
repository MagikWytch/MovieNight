package io.magikwytch.movienights.repository;

import io.magikwytch.movienights.domain.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
}
