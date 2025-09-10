package com.gridnine.testing;

import java.time.LocalDateTime;

// Правило фильтрации: исключает перелёты с вылетом до текущего момента времени.

public class DepartureBeforeCurrentTimeFilter implements FilterRule {

    private final LocalDateTime currentTime;

    // Создает фильтр с текущим временем по умолчанию (now())

    public DepartureBeforeCurrentTimeFilter() {
        this.currentTime = LocalDateTime.now();
    }

    // Создает фильтр с указанным временем (для тестирования)

    public DepartureBeforeCurrentTimeFilter(LocalDateTime currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public boolean test(Flight flight) {
        // Проверяем, что все сегменты вылетают не раньше текущего времени
        return flight.getSegments().stream()
                .allMatch(segment ->
                        !segment.getDepartureDate().isBefore(currentTime));
    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    @Override
    public String toString() {
        return "DepartureBeforeCurrentTimeFilter{currentTime=" + currentTime + "}";
    }
}