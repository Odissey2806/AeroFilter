package com.gridnine.testing;

import java.time.Duration;

// Правило фильтрации: исключает перелёты, где общее время на земле превышает указанное значение.

public class ExcessiveGroundTimeFilter implements FilterRule {

    private final long maxGroundTimeMinutes;

    // Создает фильтр с максимальным временем на земле 2 часа.

    public ExcessiveGroundTimeFilter() {
        this(120); // 2 часа по умолчанию
    }

    // Создает фильтр с указанным максимальным временем на земле в минутах.

    public ExcessiveGroundTimeFilter(long maxGroundTimeMinutes) {
        if (maxGroundTimeMinutes < 0) {
            throw new IllegalArgumentException("Максимальное время на земле не может быть отрицательным");
        }
        this.maxGroundTimeMinutes = maxGroundTimeMinutes;
    }

    // Создает фильтр с указанным максимальным временем на земле.

    public ExcessiveGroundTimeFilter(Duration maxGroundTime) {
        this(maxGroundTime.toMinutes());
    }

    @Override
    public boolean test(Flight flight) {
        // Для перелётов с одним сегментом время на земле = 0
        if (flight.getSegments().size() <= 1) {
            return true;
        }

        return flight.getTotalGroundTimeMinutes() <= maxGroundTimeMinutes;
    }

    public long getMaxGroundTimeMinutes() {
        return maxGroundTimeMinutes;
    }

    @Override
    public String toString() {
        return "ExcessiveGroundTimeFilter{maxGroundTimeMinutes=" + maxGroundTimeMinutes + "}";
    }
}