package dev.ezekielmartin.availableflightsfinder.service;

import dev.ezekielmartin.availableflightsfinder.model.FlightRoute;
import dev.ezekielmartin.availableflightsfinder.service.cache.FlightRoutesCacheImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static dev.ezekielmartin.availableflightsfinder.config.Defaults.DIRECT_FLIGHT_EXPRESSION;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightRoutesServiceImplTest {

    @Mock
    private FlightRoutesCacheImpl flightRoutesCache;

    @InjectMocks
    private FlightRoutesServiceImpl flightRoutesService;

    @Test
    void whenCacheHasRoutes_thenReturnsRoutesFlux() {
        var routes = new LinkedHashSet<>(Arrays.asList(DIRECT_FLIGHT_EXPRESSION, "STN"));

        when(flightRoutesCache.get("MADDUB")).thenReturn(Optional.of(routes));

        StepVerifier.create(flightRoutesService.findConnectingFlightRoutes("MAD", "DUB"))
                .expectNextMatches(set -> set.size() == 2
                        && set.contains(DIRECT_FLIGHT_EXPRESSION)
                        && set.contains("STN"))
                .verifyComplete();
    }

    @Test
    void whenCacheHasNotRoutes_thenReturnsEmptyFlux() {

        when(flightRoutesCache.get("MADDUB")).thenReturn(Optional.empty());

        StepVerifier.create(flightRoutesService.findConnectingFlightRoutes("MAD", "DUB"))
                .verifyComplete();
    }

    @Test
    void shouldFilterValidRoutes() {
        var input = List.of(
                new FlightRoute("","","invalid", "ryanair"),
                new FlightRoute("","",null, "invalid"),
                new FlightRoute("","",null, "ryanair")
        );
        Assertions.assertEquals(1,FlightRoutesServiceImpl.filterValidFlights(input).size());
        Assertions.assertEquals(List.of(new FlightRoute("","",null, "ryanair")),FlightRoutesServiceImpl.filterValidFlights(input));

        var allAreInvalidInput = List.of(
                new FlightRoute("","","invalid", "ryanair"),
                new FlightRoute("","",null, "invalid"),
                new FlightRoute("","",null, "iberia")
        );
        Assertions.assertEquals(0,FlightRoutesServiceImpl.filterValidFlights(allAreInvalidInput).size());
        Assertions.assertEquals(List.of(),FlightRoutesServiceImpl.filterValidFlights(allAreInvalidInput));
    }
}

