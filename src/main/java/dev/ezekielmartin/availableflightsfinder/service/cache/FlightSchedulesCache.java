package dev.ezekielmartin.availableflightsfinder.service.cache;

import dev.ezekielmartin.availableflightsfinder.model.DayOfMonthSchedules;

import java.util.List;
import java.util.Optional;

public interface FlightSchedulesCache {
    void put(String depAirportArrAirportYearMonth, List<DayOfMonthSchedules> dayOfMonthSchedulesList);
    Optional<List<DayOfMonthSchedules>> get(String depAirportArrAirportYearMonth);
}
