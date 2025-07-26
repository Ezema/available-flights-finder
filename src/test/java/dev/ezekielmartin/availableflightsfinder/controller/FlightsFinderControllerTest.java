package dev.ezekielmartin.availableflightsfinder.controller;

import dev.ezekielmartin.availableflightsfinder.model.FlightFinderResult;
import dev.ezekielmartin.availableflightsfinder.model.FlightLegData;
import dev.ezekielmartin.availableflightsfinder.model.NumberOfStops;
import dev.ezekielmartin.availableflightsfinder.service.FlightsFinderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class FlightsFinderControllerTest {
    @Mock
    private FlightsFinderService flightsFinderService;
    @InjectMocks
    private FlightsFinderController flightsFinderController;

    @Test
    void whenIataCodeInvalid_thenThrowBadRequest() {
        Assertions.assertThrows(ResponseStatusException.class, () -> flightsFinderController.findAllPossibleFlights("InvalidIATACode", "MAD", LocalDateTime.now(), LocalDateTime.now()).blockFirst());
        Assertions.assertThrows(ResponseStatusException.class, () -> flightsFinderController.findAllPossibleFlights("MAD", "InvalidIATACode", LocalDateTime.now(), LocalDateTime.now()).blockFirst());
    }

    @Test
    void whenArrivalIsBeforeDeparture_thenThrowBadRequest() {
        Assertions.assertThrows(ResponseStatusException.class, () -> flightsFinderController.findAllPossibleFlights("DUB", "MAD", LocalDateTime.now().plusDays(1), LocalDateTime.now()).blockFirst());
    }

    @Test
    void whenDepartureIsBeforeToday_thenThrowBadRequest() {
        Assertions.assertThrows(ResponseStatusException.class, () -> flightsFinderController.findAllPossibleFlights("DUB", "MAD", LocalDateTime.now().minusDays(1), LocalDateTime.now()).blockFirst());
    }

    @Test
    void whenInputIsValid_thenReturnFlightFinderResult() {
        var now = LocalDateTime.now().plusHours(1);
        var response = new FlightFinderResult(NumberOfStops.DIRECT, List.of(new FlightLegData("DUB","MAD", now, now)));
        Mockito.when(flightsFinderController.findAllPossibleFlights("DUB", "MAD", now, now))
                .thenReturn(Flux.just(response));
        StepVerifier
                .create(flightsFinderController.findAllPossibleFlights("DUB", "MAD", now, now))
                .expectNext(response)
                .verifyComplete();
    }
}
