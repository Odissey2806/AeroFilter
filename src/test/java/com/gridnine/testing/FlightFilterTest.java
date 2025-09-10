package com.gridnine.testing;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

// Тесты для модуля фильтрации перелётов, для всех правил фильтрации и их комбинации.

public class FlightFilterTest {

    @Test
    void testDepartureBeforeCurrentTimeFilter() {
        // Подготовка тестовых данных
        LocalDateTime now = LocalDateTime.now();
        Segment pastSegment = new Segment(now.minusHours(1), now.plusHours(2));
        Segment futureSegment = new Segment(now.plusHours(1), now.plusHours(3));

        Flight pastFlight = new Flight(List.of(pastSegment));
        Flight futureFlight = new Flight(List.of(futureSegment));

        List<Flight> flights = List.of(pastFlight, futureFlight);

        // Тестирование фильтра
        DepartureBeforeCurrentTimeFilter filter = new DepartureBeforeCurrentTimeFilter(now);
        FlightFilter flightFilter = new FlightFilter();

        List<Flight> result = flightFilter.filter(flights, filter);

        // Проверки
        assertEquals(1, result.size());
        assertTrue(result.contains(futureFlight));
        assertFalse(result.contains(pastFlight));
    }

    @Test
    void testDepartureBeforeCurrentTimeFilterWithFixedTime() {
        // Тест с фиксированным временем для детерминированности (то есть для многократного выполнения)
        LocalDateTime referenceTime = LocalDateTime.of(2024, 1, 1, 12, 0);

        Segment beforeSegment = new Segment(referenceTime.minusHours(1), referenceTime.plusHours(1));
        Segment afterSegment = new Segment(referenceTime.plusHours(1), referenceTime.plusHours(3));

        Flight beforeFlight = new Flight(List.of(beforeSegment));
        Flight afterFlight = new Flight(List.of(afterSegment));

        DepartureBeforeCurrentTimeFilter filter = new DepartureBeforeCurrentTimeFilter(referenceTime);

        assertFalse(filter.test(beforeFlight)); // Должен быть исключен
        assertTrue(filter.test(afterFlight));   // Должен остаться
    }

    @Test
    void testArrivalBeforeDepartureFilter() {
        // Подготовка тестовых данных
        LocalDateTime now = LocalDateTime.now();
        Segment validSegment = new Segment(now, now.plusHours(2));
        Segment invalidSegment = new Segment(now.plusHours(1), now); // прилёт раньше вылета

        Flight validFlight = new Flight(List.of(validSegment));
        Flight invalidFlight = new Flight(List.of(invalidSegment));

        List<Flight> flights = List.of(validFlight, invalidFlight);

        // Тестирование фильтра
        ArrivalBeforeDepartureFilter filter = new ArrivalBeforeDepartureFilter();
        FlightFilter flightFilter = new FlightFilter();

        List<Flight> result = flightFilter.filter(flights, filter);

        // Проверки
        assertEquals(1, result.size());
        assertTrue(result.contains(validFlight));
        assertFalse(result.contains(invalidFlight));
    }

    @Test
    void testArrivalBeforeDepartureFilterWithMultipleSegments() {
        LocalDateTime now = LocalDateTime.now();

        // Перелёт с одним некорректным сегментом
        Segment seg1 = new Segment(now, now.plusHours(1)); // valid
        Segment seg2 = new Segment(now.plusHours(2), now.plusHours(1)); // invalid

        Flight flightWithInvalidSegment = new Flight(List.of(seg1, seg2));

        ArrivalBeforeDepartureFilter filter = new ArrivalBeforeDepartureFilter();

        assertFalse(filter.test(flightWithInvalidSegment));
    }

    @Test
    void testExcessiveGroundTimeFilter() {
        LocalDateTime now = LocalDateTime.now();

        // Перелёт с малым временем на земле (1 час)
        Segment seg1 = new Segment(now, now.plusHours(1));
        Segment seg2 = new Segment(now.plusHours(2), now.plusHours(3)); // 1 час на земле
        Flight shortGroundTimeFlight = new Flight(List.of(seg1, seg2));

        // Перелёт с большим временем на земле (3 часа)
        Segment seg3 = new Segment(now, now.plusHours(1));
        Segment seg4 = new Segment(now.plusHours(4), now.plusHours(5)); // 3 часа на земле
        Flight longGroundTimeFlight = new Flight(List.of(seg3, seg4));

        // Тестирование стандартного фильтра (2 часа)
        ExcessiveGroundTimeFilter filter2h = new ExcessiveGroundTimeFilter();

        assertTrue(filter2h.test(shortGroundTimeFlight));  // Должен пройти
        assertFalse(filter2h.test(longGroundTimeFlight)); // Должен быть исключен

        // Тестирование кастомного фильтра (4 часа)
        ExcessiveGroundTimeFilter filter4h = new ExcessiveGroundTimeFilter(240);

        assertTrue(filter4h.test(shortGroundTimeFlight)); // Должен пройти
        assertTrue(filter4h.test(longGroundTimeFlight));  // Должен пройти (лимит 4 часа)
    }

    @Test
    void testExcessiveGroundTimeFilterWithDuration() {
        LocalDateTime now = LocalDateTime.now();

        Segment seg1 = new Segment(now, now.plusHours(1));
        Segment seg2 = new Segment(now.plusHours(3), now.plusHours(4)); // 2 часа на земле
        Flight flight = new Flight(List.of(seg1, seg2));

        ExcessiveGroundTimeFilter filter1h = new ExcessiveGroundTimeFilter(Duration.ofHours(1));
        ExcessiveGroundTimeFilter filter3h = new ExcessiveGroundTimeFilter(Duration.ofHours(3));

        assertFalse(filter1h.test(flight)); // 2h > 1h - должен быть исключен
        assertTrue(filter3h.test(flight));  // 2h < 3h - должен пройти
    }

    @Test
    void testExcessiveGroundTimeFilterWithSingleSegment() {
        LocalDateTime now = LocalDateTime.now();
        Flight singleSegmentFlight = new Flight(List.of(
                new Segment(now, now.plusHours(2))
        ));

        ExcessiveGroundTimeFilter filter = new ExcessiveGroundTimeFilter();

        assertTrue(filter.test(singleSegmentFlight)); // Должен всегда проходить
    }

    @Test
    void testExcessiveGroundTimeFilterWithOverlappingSegments() {
        LocalDateTime now = LocalDateTime.now();

        // Сегменты с отрицательным временем на земле (прилёт позже вылета следующего)
        Segment seg1 = new Segment(now, now.plusHours(3)); // Прилёт в 15:00
        Segment seg2 = new Segment(now.plusHours(2), now.plusHours(4)); // Вылет в 14:00

        Flight flight = new Flight(List.of(seg1, seg2));

        ExcessiveGroundTimeFilter filter = new ExcessiveGroundTimeFilter();

        // Должен пройти, так как отрицательное время не учитывается
        assertTrue(filter.test(flight));
        assertEquals(0, flight.getTotalGroundTimeMinutes()); // Время на земле = 0
    }

    @Test
    void testFilterRuleComposition() {
        LocalDateTime now = LocalDateTime.now();

        // Хороший перелёт (должен пройти все фильтры)
        Segment goodSeg1 = new Segment(now.plusHours(1), now.plusHours(2));
        Segment goodSeg2 = new Segment(now.plusHours(3), now.plusHours(4)); // 1 час на земле
        Flight goodFlight = new Flight(List.of(goodSeg1, goodSeg2));

        // Плохой перелёт (не пройдет ни один фильтр)
        Segment badSeg1 = new Segment(now.minusHours(1), now.minusHours(2)); // вылет в прошлом + прилёт раньше вылета
        Flight badFlight = new Flight(List.of(badSeg1));

        // Комбинированное правило
        FilterRule combinedRule = new DepartureBeforeCurrentTimeFilter(now)
                .and(new ArrivalBeforeDepartureFilter())
                .and(new ExcessiveGroundTimeFilter());

        assertTrue(combinedRule.test(goodFlight));  // Должен пройти
        assertFalse(combinedRule.test(badFlight));  // Должен быть исключен
    }

    @Test
    void testFilterRuleNegation() {
        LocalDateTime now = LocalDateTime.now();

        Flight pastFlight = new Flight(List.of(
                new Segment(now.minusHours(1), now.plusHours(1))
        ));

        DepartureBeforeCurrentTimeFilter filter = new DepartureBeforeCurrentTimeFilter(now);
        FilterRule negation = filter.negate();

        assertFalse(filter.test(pastFlight));    // Должен быть исключен
        assertTrue(negation.test(pastFlight));   // Отрицание - должен остаться
    }

    @Test
    void testFilterRuleOrComposition() {
        LocalDateTime now = LocalDateTime.now();

        Flight flight1 = new Flight(List.of( // Только вылет в прошлом
                new Segment(now.minusHours(1), now.plusHours(1))
        ));

        Flight flight2 = new Flight(List.of( // Только прилёт раньше вылета
                new Segment(now.plusHours(1), now)
        ));

        Flight flight3 = new Flight(List.of( // Оба нарушения
                new Segment(now.minusHours(1), now.minusHours(2))
        ));

        Flight goodFlight = new Flight(List.of( // Без нарушений
                new Segment(now.plusHours(1), now.plusHours(2))
        ));

        FilterRule rule1 = new DepartureBeforeCurrentTimeFilter(now);
        FilterRule rule2 = new ArrivalBeforeDepartureFilter();
        FilterRule orRule = rule1.or(rule2);

        // Правило OR: true если ХОТЯ БЫ ОДНО условие true
        // rule1.test(flight1) = false (вылет в прошлом - нарушение)
        // rule2.test(flight1) = true (прилёт после вылета - норма)
        // false OR true = true
        assertTrue(orRule.test(flight1));   // Нарушение rule2 -> true

        // rule1.test(flight2) = true (вылет в будущем - норма)
        // rule2.test(flight2) = false (прилёт раньше вылета - нарушение)
        // true OR false = true
        assertTrue(orRule.test(flight2));   // Нарушение rule1 -> true

        // rule1.test(flight3) = false (вылет в прошлом - нарушение)
        // rule2.test(flight3) = false (прилёт раньше вылета - нарушение)
        // false OR false = false
        assertFalse(orRule.test(flight3));  // Оба нарушения -> false

        // rule1.test(goodFlight) = true (вылет в будущем - норма)
        // rule2.test(goodFlight) = true (прилёт после вылета - норма)
        // true OR true = true
        assertTrue(orRule.test(goodFlight)); // Без нарушений -> true
    }

    @Test
    void testFlightFilterWithMultipleRules() {
        LocalDateTime now = LocalDateTime.now();

        List<Flight> flights = List.of(
                // Хороший перелёт
                new Flight(List.of(
                        new Segment(now.plusHours(1), now.plusHours(2)),
                        new Segment(now.plusHours(3), now.plusHours(4))
                )),
                // Вылет в прошлом
                new Flight(List.of(
                        new Segment(now.minusHours(1), now.plusHours(1))
                )),
                // Прилёт раньше вылета
                new Flight(List.of(
                        new Segment(now.plusHours(1), now)
                ))
        );

        FlightFilter filter = new FlightFilter();
        List<Flight> result = filter.filter(flights,
                new DepartureBeforeCurrentTimeFilter(now),
                new ArrivalBeforeDepartureFilter(),
                new ExcessiveGroundTimeFilter()
        );

        assertEquals(1, result.size()); // Должен остаться только хороший перелёт
    }

    @Test
    void testFilterRulesFactory() {
        LocalDateTime now = LocalDateTime.now();

        Flight businessFlight = new Flight(List.of( // 30 мин на земле
                new Segment(now.plusHours(1), now.plusHours(2)),
                new Segment(now.plusHours(2).plusMinutes(30), now.plusHours(3))
        ));

        Flight regularFlight = new Flight(List.of( // 3 часа на земле
                new Segment(now.plusHours(1), now.plusHours(2)),
                new Segment(now.plusHours(5), now.plusHours(6))
        ));

        FilterRule businessFilter = FilterRules.createBusinessFilter(); // Макс 1 час
        FilterRule standardFilter = FilterRules.createStandardFilter(); // Макс 2 часа

        assertTrue(businessFilter.test(businessFlight));  // 30m < 1h - пройдет
        assertFalse(businessFilter.test(regularFlight));  // 3h > 1h - исключен

        assertTrue(standardFilter.test(businessFlight));  // 30m < 2h - пройдет
        assertFalse(standardFilter.test(regularFlight));  // 3h > 2h - исключен
    }

    @Test
    void testFlightTotalGroundTimeCalculation() {
        LocalDateTime now = LocalDateTime.now();

        Flight flight = new Flight(List.of(
                new Segment(now, now.plusHours(1)),           // 10:00-11:00
                new Segment(now.plusHours(2), now.plusHours(3)), // 12:00-13:00 (1h ground)
                new Segment(now.plusHours(4), now.plusHours(5))  // 14:00-15:00 (1h ground)
        ));

        assertEquals(120, flight.getTotalGroundTimeMinutes()); // 2 часа всего
    }

    @Test
    void testFlightValidationMethods() {
        LocalDateTime now = LocalDateTime.now();

        Flight validFlight = new Flight(List.of(
                new Segment(now.plusHours(1), now.plusHours(2))
        ));

        Flight invalidDepartureFlight = new Flight(List.of(
                new Segment(now.minusHours(1), now.plusHours(1))
        ));

        Flight invalidArrivalFlight = new Flight(List.of(
                new Segment(now.plusHours(1), now)
        ));

        assertFalse(validFlight.hasInvalidSegments());
        assertFalse(validFlight.hasDepartureInPast(now));

        assertFalse(invalidDepartureFlight.hasInvalidSegments());
        assertTrue(invalidDepartureFlight.hasDepartureInPast(now));

        assertTrue(invalidArrivalFlight.hasInvalidSegments());
        assertFalse(invalidArrivalFlight.hasDepartureInPast(now));
    }
}