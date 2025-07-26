package dev.ezekielmartin.availableflightsfinder.service;

import dev.ezekielmartin.availableflightsfinder.model.DayOfMonthSchedules;
import dev.ezekielmartin.availableflightsfinder.model.FlightSchedule;
import reactor.core.publisher.Flux;

public interface FlightSchedulesService {
    Flux<FlightSchedule> getFlightSchedules(String departureAirportCode, String arrivalAirportCode, int year, int month, int dayOfMonth);

    Flux<DayOfMonthSchedules> getFlightSchedules(String departureAirportCode, String arrivalAirportCode, int year, int month);

    String generateCacheKey(String departureAirportCode, String arrivalAirportCode, int year, int month);
}
