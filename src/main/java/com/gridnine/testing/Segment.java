package com.gridnine.testing;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

// Этот класс, представляющий сегмент перелёта.
// Подготовлен для будущей поддержки временных зон.

public class Segment {
    private final LocalDateTime departureDate;
    private final LocalDateTime arrivalDate;
    private ZoneId timeZone; // Для будущего расширения

    public Segment(final LocalDateTime departureDate, final LocalDateTime arrivalDate) {
        this.departureDate = Objects.requireNonNull(departureDate, "departureDate не может быть null");
        this.arrivalDate = Objects.requireNonNull(arrivalDate, "arrivalDate не может быть null");
    }

    // Конструктор для будущего расширения с временными зонами.

    public Segment(final LocalDateTime departureDate, final LocalDateTime arrivalDate, ZoneId timeZone) {
        this(departureDate, arrivalDate);
        this.timeZone = timeZone;
    }

    // Конвертирует в ZonedDateTime (для будущего использования).

    public ZonedDateTime getDepartureZoned() {
        return timeZone != null ?
                ZonedDateTime.of(departureDate, timeZone) :
                ZonedDateTime.of(departureDate, ZoneId.systemDefault());
    }

    // Конвертирует в ZonedDateTime (для будущего использования).

    public ZonedDateTime getArrivalZoned() {
        return timeZone != null ?
                ZonedDateTime.of(arrivalDate, timeZone) :
                ZonedDateTime.of(arrivalDate, ZoneId.systemDefault());
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }

    // Проверяет, корректен ли сегмент (прилёт не раньше вылета)

    public boolean isValid() {
        return !arrivalDate.isBefore(departureDate);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return "[" + departureDate.format(formatter) +
                "|" + arrivalDate.format(formatter) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Segment segment = (Segment) o;
        return departureDate.equals(segment.departureDate) &&
                arrivalDate.equals(segment.arrivalDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departureDate, arrivalDate);
    }
}