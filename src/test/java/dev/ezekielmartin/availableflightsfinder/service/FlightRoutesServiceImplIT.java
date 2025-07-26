package dev.ezekielmartin.availableflightsfinder.service;

import dev.ezekielmartin.availableflightsfinder.service.cache.FlightRoutesCacheImpl;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.LinkedHashSet;

import static dev.ezekielmartin.availableflightsfinder.config.Defaults.DIRECT_FLIGHT_EXPRESSION;

@SpringBootTest
class FlightRoutesServiceImplIT {
    @Autowired
    FlightRoutesCacheImpl flightRoutesCache;
    @Autowired
    private FlightRoutesServiceImpl flightRoutesService;

    @Test
    @Order(2)
    void whenCacheHasRoutes_thenReturnsRoutesFlux() {
        flightRoutesCache.put("MADDUB",new LinkedHashSet<>(Arrays.asList(DIRECT_FLIGHT_EXPRESSION, "STN")));

        StepVerifier.create(flightRoutesService.findConnectingFlightRoutes("MAD", "DUB"))
                .expectNextMatches(set -> set.size() == 2
                        && set.contains(DIRECT_FLIGHT_EXPRESSION)
                        && set.contains("STN"))
                .verifyComplete();
    }

    @Test
    @Order(1)
    void whenCacheHasNotRoutes_thenReturnsEmptyFlux() {
        StepVerifier.create(flightRoutesService.findConnectingFlightRoutes("MAD", "DUB"))
                .verifyComplete();
    }
}

