package dev.ezekielmartin.availableflightsfinder.service;

import dev.ezekielmartin.availableflightsfinder.model.FlightFinderResult;
import dev.ezekielmartin.availableflightsfinder.model.FlightLegData;
import dev.ezekielmartin.availableflightsfinder.model.FlightSchedule;
import dev.ezekielmartin.availableflightsfinder.model.NumberOfStops;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static dev.ezekielmartin.availableflightsfinder.config.Defaults.DIRECT_FLIGHT_EXPRESSION;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FlightFinderServiceTest {
    @Mock
    FlightRoutesService flightRoutesService;
    @Mock
    FlightSchedulesService flightSchedulesService;

    @InjectMocks
    FlightsFinderService flightsFinderService;

    @Test
    void whenRouteIsPossibleAndDatesValid_thenAllDirectAndOneStopFlightsAreFound() {
        var routes = new LinkedHashSet<>(Arrays.asList(DIRECT_FLIGHT_EXPRESSION, "STN"));

        when(flightRoutesService.findConnectingFlightRoutes("MAD", "DUB"))
                .thenReturn(Flux.just(routes));

        //Valid direct flight
        when(flightSchedulesService.getFlightSchedules("MAD", "DUB", 2025, 7, 26)).thenReturn(Flux.just(
                new FlightSchedule("",0,"12:00", "14:00")
        ));

        //Valid one-stop flight pair
        when(flightSchedulesService.getFlightSchedules("MAD", "STN", 2025, 7, 26)).thenReturn(Flux.just(
                new FlightSchedule("",0,"12:00", "14:00")
        ));
        when(flightSchedulesService.getFlightSchedules("STN", "DUB", 2025, 7, 26)).thenReturn(Flux.just(
                new FlightSchedule("",0,"16:00", "18:00")
        ));

        StepVerifier.create(flightsFinderService.find("MAD", "DUB"
                ,LocalDateTime.of(2025, 7, 26, 10, 0)
                ,LocalDateTime.of(2025, 7, 26, 22, 0)))
        .expectNext(new FlightFinderResult(NumberOfStops.DIRECT
                , List.of(
                        new FlightLegData("MAD", "DUB", LocalDateTime.of(2025, 7, 26, 12, 0),LocalDateTime.of(2025, 7, 26, 14, 0))
        ))).expectNext(new FlightFinderResult(NumberOfStops.ONE_STOP
                        , List.of(
                        new FlightLegData("MAD", "STN", LocalDateTime.of(2025, 7, 26, 12, 0),LocalDateTime.of(2025, 7, 26, 14, 0)),
                        new FlightLegData("STN", "DUB", LocalDateTime.of(2025, 7, 26, 16, 0),LocalDateTime.of(2025, 7, 26, 18, 0))
                        )))
        .verifyComplete();
    }

    @Test
    void whenRouteIsNotPossible_thenEmptyResult(){
        var routes = new LinkedHashSet<>(Arrays.asList(DIRECT_FLIGHT_EXPRESSION, "STN"));

        when(flightRoutesService.findConnectingFlightRoutes("MAD", "DUB"))
                .thenReturn(Flux.just(routes));

        //Direct flight is out of time range desired by the user
        when(flightSchedulesService.getFlightSchedules("MAD", "DUB", 2025, 7, 26)).thenReturn(Flux.just(
                new FlightSchedule("",0,"05:00", "07:00")
        ));

        //One Stop flight requires the traveler to transfer between each flight in less than 2 hours which is not valid
        when(flightSchedulesService.getFlightSchedules("MAD", "STN", 2025, 7, 26)).thenReturn(Flux.just(
                new FlightSchedule("",0,"12:00", "14:00")
        ));

        when(flightSchedulesService.getFlightSchedules("STN", "DUB", 2025, 7, 26)).thenReturn(Flux.just(
                new FlightSchedule("",0,"15:00", "17:00")
        ));

        StepVerifier.create(flightsFinderService.find("MAD", "DUB"
                        ,LocalDateTime.of(2025, 7, 26, 10, 0)
                        ,LocalDateTime.of(2025, 7, 26, 22, 0)))
                .verifyComplete();
    }
}
