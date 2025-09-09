package com.gridnine.testing;

import java.util.List;
import java.util.stream.Collectors;

// Класс для фильтрации перелётов по различным правилам.
// Для поддержания эффективного комбинирование правил.

public class FlightFilter {

    // Фильтрует список перелётов согласно заданному правилу.
    // @param flights список перелётов для фильтрации
    // @param rule правило фильтрации
    // @return отфильтрованный список перелётов

    public List<Flight> filter(List<Flight> flights, FilterRule rule) {
        return flights.stream()
                .filter(rule::test)
                .collect(Collectors.toList());
    }

    // Фильтрует список перелётов по нескольким правилам одновременно.
    // Эффективно комбинирует правила перед фильтрацией.
    // @param flights список перелётов для фильтрации
    // @param rules массив правил фильтрации
    // @return отфильтрованный список перелётов

    public List<Flight> filter(List<Flight> flights, FilterRule... rules) {
        if (rules.length == 0) {
            return flights;
        }

        // Комбинируем все правила в одно
        FilterRule combinedRule = rules[0];
        for (int i = 1; i < rules.length; i++) {
            combinedRule = combinedRule.and(rules[i]);
        }

        // Однократная фильтрация
        return filter(flights, combinedRule);
    }

    // Фильтрует список перелётов с комбинированным правилом.
    // @param flights список перелётов для фильтрации
    // @param combinedRule комбинированное правило
    // @return отфильтрованный список перелётов

    public List<Flight> filterWithCombinedRule(List<Flight> flights, FilterRule combinedRule) {
        return filter(flights, combinedRule);
    }
}