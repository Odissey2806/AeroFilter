package com.gridnine.testing;

import java.util.List;
import java.util.stream.Collectors;


// Главный класс. Показывает исходные данные и результаты каждой фильтрации.

public class Main {

    public static void main(String[] args) {
        // Получаем тестовые данные
        List<Flight> flights = FlightBuilder.createFlights();

        // Создаем фильтр
        FlightFilter filter = new FlightFilter();

        System.out.println("=== ИСХОДНЫЙ СПИСОК ПЕРЕЛЁТОВ ===");
        System.out.println(FlightPrinter.formatFlights(flights));
        System.out.println();

        // Фильтр 1: Исключить перелёты с вылетом до текущего момента.
        System.out.println("=== ФИЛЬТР 1: Исключить вылет до текущего момента ===");
        List<Flight> filtered1 = filter.filter(flights, new DepartureBeforeCurrentTimeFilter());
        System.out.println("Осталось перелётов: " + filtered1.size());
        System.out.println(FlightPrinter.formatFlights(filtered1));
        System.out.println();

        // Фильтр 2: Исключить сегменты с датой прилёта раньше даты вылета.
        System.out.println("=== ФИЛЬТР 2: Исключить прилёт раньше вылета ===");
        List<Flight> filtered2 = filter.filter(flights, new ArrivalBeforeDepartureFilter());
        System.out.println("Осталось перелётов: " + filtered2.size());
        System.out.println(FlightPrinter.formatFlights(filtered2));
        System.out.println();

        // Фильтр 3: Исключить перелёты с временем на земле > 2 часов
        System.out.println("=== ФИЛЬТР 3: Исключить время на земле > 2 часов ===");
        List<Flight> filtered3 = filter.filter(flights, new ExcessiveGroundTimeFilter());
        System.out.println("Осталось перелётов: " + filtered3.size());
        System.out.println(FlightPrinter.formatFlights(filtered3));
        System.out.println();

        // Показать какие перелёты исключаются каждым правилом
        System.out.println("=== ПЕРЕЛЁТЫ, ИСКЛЮЧАЕМЫЕ КАЖДЫМ ПРАВИЛОМ ===");

        System.out.println("Исключаемые правилом 1 (вылет в прошлом):");
        List<Flight> excluded1 = flights.stream()
                .filter(flight -> !new DepartureBeforeCurrentTimeFilter().test(flight))
                .collect(Collectors.toList());
        System.out.println(FlightPrinter.formatFlights(excluded1));

        System.out.println("Исключаемые правилом 2 (прилёт раньше вылета):");
        List<Flight> excluded2 = flights.stream()
                .filter(flight -> !new ArrivalBeforeDepartureFilter().test(flight))
                .collect(Collectors.toList());
        System.out.println(FlightPrinter.formatFlights(excluded2));

        System.out.println("Исключаемые правилом 3 (время на земле больше > 2ч):");
        List<Flight> excluded3 = flights.stream()
                .filter(flight -> !new ExcessiveGroundTimeFilter().test(flight))
                .collect(Collectors.toList());
        System.out.println(FlightPrinter.formatFlights(excluded3));
        System.out.println();

        // Комбинированный фильтр: все три правила сразу!
        System.out.println("=== КОМБИНИРОВАННЫЙ ФИЛЬТР: Все три правила ===");
        List<Flight> filteredAll = filter.filter(flights,
                new DepartureBeforeCurrentTimeFilter(),
                new ArrivalBeforeDepartureFilter(),
                new ExcessiveGroundTimeFilter()
        );

        System.out.println("Осталось перелётов после всех фильтров: " + filteredAll.size());
        System.out.println(FlightPrinter.formatFlights(filteredAll));

        // Демонстрация использования фабрики правил
        System.out.println("=== ДЕМОНСТРАЦИЯ ФАБРИКИ ПРАВИЛ ===");
        FilterRule businessFilter = FilterRules.createBusinessFilter();
        List<Flight> businessFlights = filter.filter(flights, businessFilter);
        System.out.println("Бизнес-рейсы (макс. 1 час на земле): " + businessFlights.size());
        System.out.println(FlightPrinter.formatFlights(businessFlights));
    }
}