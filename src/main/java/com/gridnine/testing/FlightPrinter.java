package com.gridnine.testing;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

// Показывает время на земле между сегментами.

public final class FlightPrinter {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private FlightPrinter() {
        // Utility class
    }

    //Форматирует один перелёт с подробной информацией.

    public static String formatFlight(Flight flight, int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("Перелёт #").append(index).append(":\n");

        List<Segment> segments = flight.getSegments();
        for (int i = 0; i < segments.size(); i++) {
            Segment segment = segments.get(i);
            sb.append("  Сегмент ").append(i + 1).append(": ")
                    .append(segment.getDepartureDate().format(FORMATTER))
                    .append(" → ")
                    .append(segment.getArrivalDate().format(FORMATTER))
                    .append("\n");

            // Показываем время на земле между сегментами
            if (i < segments.size() - 1) {
                Segment nextSegment = segments.get(i + 1);
                Duration groundTime = Duration.between(
                        segment.getArrivalDate(),
                        nextSegment.getDepartureDate()
                );

                if (!groundTime.isNegative()) {
                    sb.append("  Время на земле: ")
                            .append(formatDuration(groundTime))
                            .append("\n");
                }
            }
        }

        // Общее время на земле
        if (segments.size() > 1) {
            sb.append("  Общее время на земле: ")
                    .append(formatDuration(
                            Duration.ofMinutes(flight.getTotalGroundTimeMinutes())
                    ))
                    .append("\n");
        }

        return sb.toString();
    }

    // Форматирует список перелётов.

    public static String formatFlights(List<Flight> flights) {
        if (flights.isEmpty()) {
            return "Перелёты не найдены.\n";
        }

        StringJoiner sj = new StringJoiner("\n---\n");
        for (int i = 0; i < flights.size(); i++) {
            sj.add(formatFlight(flights.get(i), i + 1));
        }
        return sj.toString();
    }

    // Форматирует Duration в читаемый вид для удобства!.

    private static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        if (hours > 0 && minutes > 0) {
            return hours + "ч " + minutes + "м";
        } else if (hours > 0) {
            return hours + "ч";
        } else {
            return minutes + "м";
        }
    }
}