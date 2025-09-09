package com.gridnine.testing;

// Правило фильтрации: исключает перелёты с сегментами, где дата прилёта раньше даты вылета.

public class ArrivalBeforeDepartureFilter implements FilterRule {

    @Override
    public boolean test(Flight flight) {
        // Проверяем, что все сегменты корректны (прилёт не раньше вылета)
        return !flight.hasInvalidSegments();
    }

    @Override
    public String toString() {
        return "ArrivalBeforeDepartureFilter{}";
    }
}