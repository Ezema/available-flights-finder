package dev.ezekielmartin.availableflightsfinder.controller;

import dev.ezekielmartin.availableflightsfinder.model.FlightFinderResult;
import dev.ezekielmartin.availableflightsfinder.service.FlightsFinderService;
import dev.ezekielmartin.availableflightsfinder.utils.Utils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
public class FlightsFinderController {
    @Autowired
    FlightsFinderService flightsFinderService;

    @GetMapping("/interconnections")
    public Flux<FlightFinderResult> findAllPossibleFlights(@RequestParam @NotBlank String departure, @RequestParam @NotBlank String arrival, @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime, @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalDateTime) {
        if (!Utils.isValidIataCode(departure) || !Utils.isValidIataCode(arrival)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid IATA airport code detected");
        }
        if (arrivalDateTime.isBefore(departureDateTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arrival time must be before departure time");
        }
        if (departureDateTime.isBefore(LocalDateTime.of(LocalDate.now(), LocalTime.now()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departure date/time can't be in the past");
        }
        return flightsFinderService.find(departure, arrival, departureDateTime, arrivalDateTime);
    }
}
