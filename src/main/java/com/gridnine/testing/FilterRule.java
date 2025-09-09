package com.gridnine.testing;

import java.util.function.Predicate;

// Интерфейс для правил фильтрации перелётов.
// Расширяет стандартный Predicate для совместимости с Java API.

@FunctionalInterface
public interface FilterRule extends Predicate<Flight> {

    // Проверяет, удовлетворяет ли перелёт условиям правила.
    // @param flight перелёт для проверки
    // @return true если перелёт удовлетворяет правилу (должен быть включен в результат),
    //         false если не удовлетворяет (должен быть исключен)

    boolean test(Flight flight);

    // Композиция правил через AND (логическое И)

    default FilterRule and(FilterRule other) {
        return flight -> this.test(flight) && other.test(flight);
    }

    // Композиция правил через OR (логическое ИЛИ)

    default FilterRule or(FilterRule other) {
        return flight -> this.test(flight) || other.test(flight);
    }

    // Отрицание правила (логическое НЕ)

    default FilterRule negate() {
        return flight -> !this.test(flight);
    }

    // Статический метод для создания правила из Predicate

    static FilterRule fromPredicate(Predicate<Flight> predicate) {
        return predicate::test;
    }
}