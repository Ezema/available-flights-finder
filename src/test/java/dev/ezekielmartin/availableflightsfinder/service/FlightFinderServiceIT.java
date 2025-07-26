package dev.ezekielmartin.availableflightsfinder.service;

import dev.ezekielmartin.availableflightsfinder.model.*;
import dev.ezekielmartin.availableflightsfinder.service.cache.FlightRoutesCacheImpl;
import dev.ezekielmartin.availableflightsfinder.service.cache.FlightSchedulesCacheImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static dev.ezekielmartin.availableflightsfinder.config.Defaults.DIRECT_FLIGHT_EXPRESSION;

@SpringBootTest
public class FlightFinderServiceIT {
    @Autowired
    FlightRoutesCacheImpl flightRoutesCache;
    @Autowired
    FlightSchedulesCacheImpl flightSchedulesCache;
    @Autowired
    FlightsFinderService flightsFinderService;

    @Test
    void whenRouteIsPossibleAndDatesValid_thenRouteIsFound() {
        var routes = new LinkedHashSet<>(Arrays.asList(DIRECT_FLIGHT_EXPRESSION));

        flightRoutesCache.put("MADDUB", routes);

        var departure = LocalDateTime.of(2025, 7, 26, 6, 0);
        var arrival = LocalDateTime.of(2025, 7, 26, 22, 0);

        flightSchedulesCache.put("MADDUB20257", List.of(new DayOfMonthSchedules("26", List.of(
                new FlightSchedule("",0,"12:00", "14:00"),
                new FlightSchedule("",0,"16:00", "18:00")
        ))));

        StepVerifier.create(flightsFinderService.find("MAD", "DUB"
                        , departure
                        , arrival))
                .expectNext(new FlightFinderResult(NumberOfStops.DIRECT
                        , List.of(
                        new FlightLegData("MAD", "DUB", LocalDateTime.of(2025, 7, 26, 12, 0),LocalDateTime.of(2025, 7, 26, 14, 0))
                )))
                .expectNext(new FlightFinderResult(NumberOfStops.DIRECT
                        , List.of(
                        new FlightLegData("MAD", "DUB", LocalDateTime.of(2025, 7, 26, 16, 0),LocalDateTime.of(2025, 7, 26, 18, 0))
                )))
                .verifyComplete();
    }
}
