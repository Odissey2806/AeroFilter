package com.gridnine.testing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Фабрика для создания тестовых перелётов.

public class FlightBuilder {

    // Создает список тестовых перелётов для демонстрации работы фильтров.

    public static List<Flight> createFlights() {
        LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3);

        return Arrays.asList(
                // Нормальный перелёт длительностью 2 часа
                createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2)),

                // Нормальный перелёт с несколькими сегментами
                createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                        threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(5)),

                // Перелёт с вылетом в прошлом
                createFlight(threeDaysFromNow.minusDays(6), threeDaysFromNow),

                // Перелёт с прибытием раньше вылета (некорректный сегмент)
                createFlight(threeDaysFromNow, threeDaysFromNow.minusHours(6)),

                // Перелёт с большим временем на земле (более 2 часов)
                createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                        threeDaysFromNow.plusHours(5), threeDaysFromNow.plusHours(6)),

                // Еще один перелёт с большим временем на земле
                createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
                        threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(4),
                        threeDaysFromNow.plusHours(6), threeDaysFromNow.plusHours(7))
        );
    }

    // Создает перелёт из переданных дат (четное количество).

    private static Flight createFlight(final LocalDateTime... dates) {
        if (dates.length % 2 != 0) {
            throw new IllegalArgumentException("Необходимо передать четное количество дат");
        }

        List<Segment> segments = new ArrayList<>(dates.length / 2);
        for (int i = 0; i < dates.length - 1; i += 2) {
            segments.add(new Segment(dates[i], dates[i + 1]));
        }

        return new Flight(segments);
    }
}