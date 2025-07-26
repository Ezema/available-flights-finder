package dev.ezekielmartin.availableflightsfinder.controller;

import dev.ezekielmartin.availableflightsfinder.model.FlightFinderResult;
import dev.ezekielmartin.availableflightsfinder.model.FlightLegData;
import dev.ezekielmartin.availableflightsfinder.model.NumberOfStops;
import dev.ezekielmartin.availableflightsfinder.service.FlightsFinderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class FlightsFinderControllerIT {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FlightsFinderService flightsFinderService;

    private final String BASE = "/interconnections";
    private static final String DEPARTURE_QUERY_PARAM = "departure";
    private static final String ARRIVAL_QUERY_PARAM = "arrival";
    private static final String DEPARTURE_DATE_TIME_QUERY_PARAM = "departureDateTime";
    private static final String ARRIVAL_DATE_TIME_QUERY_PARAM = "arrivalDateTime";

    @Test
    void whenIsInvalidIataCode_returnsBadRequest() {
        webTestClient.get()
                .uri(uri ->
                        uri.path(BASE)
                        .queryParam(DEPARTURE_QUERY_PARAM, "invalidIATACode")
                        .queryParam(ARRIVAL_QUERY_PARAM, "MAD")
                        .queryParam(DEPARTURE_DATE_TIME_QUERY_PARAM, LocalDateTime.now())
                        .queryParam(ARRIVAL_DATE_TIME_QUERY_PARAM, LocalDateTime.now())
                        .build()
                )
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void whenArrivalBeforeDeparture_returnsBadRequest() {
        LocalDateTime now = LocalDateTime.now();
        webTestClient.get()
                .uri(uri ->
                        uri.path(BASE)
                        .queryParam(DEPARTURE_QUERY_PARAM, "DUB")
                        .queryParam(ARRIVAL_QUERY_PARAM, "MAD")
                        .queryParam(DEPARTURE_DATE_TIME_QUERY_PARAM, now.plusHours(1))
                        .queryParam(ARRIVAL_DATE_TIME_QUERY_PARAM, now)
                        .build()
                )
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void whenValidInput_returnsFlightFinderResults() {
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        FlightFinderResult result = new FlightFinderResult(
                NumberOfStops.DIRECT,
                List.of(new FlightLegData("DUB", "MAD", now, now))
        );

        Mockito.when(flightsFinderService.find("DUB", "MAD", now, now))
                .thenReturn(Flux.just(result));

        webTestClient.get()
                .uri(uri ->
                        uri.path(BASE)
                        .queryParam(DEPARTURE_QUERY_PARAM, "DUB")
                        .queryParam(ARRIVAL_QUERY_PARAM, "MAD")
                        .queryParam(DEPARTURE_DATE_TIME_QUERY_PARAM, now)
                        .queryParam(ARRIVAL_DATE_TIME_QUERY_PARAM, now)
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FlightFinderResult.class)
                .hasSize(1)
                .contains(result);
    }
}
