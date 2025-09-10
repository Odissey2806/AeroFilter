package com.gridnine.testing;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// Класс, представляющий перелёт.
// Перелёт состоит из одного или нескольких сегментов.

public class Flight {
    private final List<Segment> segments;

    public Flight(final List<Segment> segments) {
        this.segments = Objects.requireNonNull(segments, "segments не может быть null");
    }

    public List<Segment> getSegments() {
        return segments;
    }

    // Вычисляет общее время на земле между сегментами (в минутах)

    public long getTotalGroundTimeMinutes() {
        long totalGroundTime = 0;
        for (int i = 0; i < segments.size() - 1; i++) {
            Segment current = segments.get(i);
            Segment next = segments.get(i + 1);

            // Учитываем только положительное время на земле
            if (next.getDepartureDate().isAfter(current.getArrivalDate())) {
                totalGroundTime += Duration.between(
                        current.getArrivalDate(),
                        next.getDepartureDate()
                ).toMinutes();
            }
        }
        return totalGroundTime;
    }

    // Проверяет, есть ли в перелёте сегменты с прилётом раньше вылета.

    public boolean hasInvalidSegments() {
        return segments.stream().anyMatch(segment ->
                segment.getArrivalDate().isBefore(segment.getDepartureDate()));
    }

    // Проверяет, есть ли сегменты с вылетом в прошлом относительно заданного времени.

    public boolean hasDepartureInPast(LocalDateTime referenceTime) {
        return segments.stream().anyMatch(segment ->
                segment.getDepartureDate().isBefore(referenceTime));
    }

    @Override
    public String toString() {
        return segments.stream()
                .map(Segment::toString)
                .collect(Collectors.joining(" "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return segments.equals(flight.segments);
    }

    @Override
    public int hashCode() {
        return segments.hashCode();
    }
}