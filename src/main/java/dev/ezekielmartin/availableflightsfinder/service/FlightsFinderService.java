package dev.ezekielmartin.availableflightsfinder.service;

import dev.ezekielmartin.availableflightsfinder.model.FlightFinderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;

import static dev.ezekielmartin.availableflightsfinder.config.Defaults.DIRECT_FLIGHT_EXPRESSION;
import static dev.ezekielmartin.availableflightsfinder.utils.Utils.*;

@Service
public class FlightsFinderService {
    @Autowired
    FlightRoutesService flightRoutesService;
    @Autowired
    FlightSchedulesService flightSchedulesService;

    public Flux<FlightFinderResult> find(String departure, String destination, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        return flightRoutesService
                .findConnectingFlightRoutes(departure, destination)
                .flatMap(Flux::fromIterable)
                .flatMap(connection -> {
                    if (connection.equalsIgnoreCase(DIRECT_FLIGHT_EXPRESSION)) {
                        return Flux
                                .range(0, (int) (Duration.between(departureDateTime, arrivalDateTime).toDays() + 1))
                                .map(departureDateTime::plusDays)
                                .flatMap(
                                        currentDate -> flightSchedulesService.getFlightSchedules(departure, destination, currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth())
                                                .filter(flightSchedule -> isValidDirectFlightSchedule(currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth(), flightSchedule, departureDateTime, arrivalDateTime))
                                                .map(flightSchedule -> getDirectFlightFinderResult(currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth(), departure, destination, flightSchedule))
                                );
                    } else {
                        return Flux
                                .range(0, (int) (Duration.between(departureDateTime, arrivalDateTime).toDays() + 1))
                                .map(departureDateTime::plusDays)
                                .flatMap(
                                        currentDate -> flightSchedulesService.getFlightSchedules(departure, connection, currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth())
                                                .filter(flightSchedule -> isValidDirectFlightSchedule(currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth(), flightSchedule, departureDateTime, arrivalDateTime))
                                                .flatMap(firstLegFlightSchedule -> flightSchedulesService.getFlightSchedules(connection, destination, currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth())
                                                        .filter(secondLegFlightSchedule -> isValidOneStopFlightSchedule(currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth(), firstLegFlightSchedule, secondLegFlightSchedule, arrivalDateTime))
                                                        .map(validSecondLegFlightSchedule -> getOneStopFlightFinderResult(currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth(), departure, connection, destination, firstLegFlightSchedule, validSecondLegFlightSchedule))
                                                )
                                );
                    }
                })
                .sort(Comparator.comparing(FlightFinderResult::getStops));
    }
}
