package com.gridnine.testing;

import java.time.Duration;

// Фабрика для создания комбинированных правил фильтрации.

public final class FilterRules {

    private FilterRules() {
        // Utility class
    }

    // Создает комбинированное правило для всех стандартных фильтров.

    public static FilterRule createStandardFilter() {
        FilterRule rule1 = new DepartureBeforeCurrentTimeFilter();
        FilterRule rule2 = new ArrivalBeforeDepartureFilter();
        FilterRule rule3 = new ExcessiveGroundTimeFilter();

        return flight -> rule1.test(flight) && rule2.test(flight) && rule3.test(flight);
    }

    // Создает правило для бизнес-рейсов (без долгих ожиданий).

    public static FilterRule createBusinessFilter() {
        FilterRule rule1 = new DepartureBeforeCurrentTimeFilter();
        FilterRule rule2 = new ArrivalBeforeDepartureFilter();
        FilterRule rule3 = new ExcessiveGroundTimeFilter(60); // Максимум 1 час на земле

        return flight -> rule1.test(flight) && rule2.test(flight) && rule3.test(flight);
    }

    // Создает правило только для базовых проверок.

    public static FilterRule createBasicFilter() {
        FilterRule rule1 = new DepartureBeforeCurrentTimeFilter();
        FilterRule rule2 = new ArrivalBeforeDepartureFilter();

        return flight -> rule1.test(flight) && rule2.test(flight);
    }

    // Создает правило с кастомным временем на земле.

    public static FilterRule createCustomFilter(long maxGroundTimeMinutes) {
        FilterRule rule1 = new DepartureBeforeCurrentTimeFilter();
        FilterRule rule2 = new ArrivalBeforeDepartureFilter();
        FilterRule rule3 = new ExcessiveGroundTimeFilter(maxGroundTimeMinutes);

        return flight -> rule1.test(flight) && rule2.test(flight) && rule3.test(flight);
    }

    // Создает правило с кастомным временем на земле.

    public static FilterRule createCustomFilter(Duration maxGroundTime) {
        return createCustomFilter(maxGroundTime.toMinutes());
    }

    // Комбинирует несколько правил в одно через AND.

    public static FilterRule combineRules(FilterRule... rules) {
        return flight -> {
            for (FilterRule rule : rules) {
                if (!rule.test(flight)) {
                    return false;
                }
            }
            return true;
        };
    }

    // Комбинирует несколько правил в одно через OR.

    public static FilterRule combineRulesOr(FilterRule... rules) {
        return flight -> {
            for (FilterRule rule : rules) {
                if (rule.test(flight)) {
                    return true;
                }
            }
            return false;
        };
    }
}