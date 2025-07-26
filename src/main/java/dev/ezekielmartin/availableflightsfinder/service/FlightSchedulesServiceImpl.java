package dev.ezekielmartin.availableflightsfinder.service;

import dev.ezekielmartin.availableflightsfinder.model.DayOfMonthSchedules;
import dev.ezekielmartin.availableflightsfinder.model.FlightSchedule;
import dev.ezekielmartin.availableflightsfinder.model.MonthSchedules;
import dev.ezekielmartin.availableflightsfinder.service.cache.FlightSchedulesCache;
import dev.ezekielmartin.availableflightsfinder.webclient.SchedulesAPIWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class FlightSchedulesServiceImpl implements FlightSchedulesService {
    @Autowired
    SchedulesAPIWebClient schedulesAPIWebClient;
    @Autowired
    FlightSchedulesCache flightSchedulesCache;

    @Override
    public Flux<FlightSchedule> getFlightSchedules(String departureAirportCode, String arrivalAirportCode, int year, int month, int dayOfMonth) {
        var cacheKey = generateCacheKey(departureAirportCode, arrivalAirportCode, year, month);
        return Flux.fromIterable(flightSchedulesCache.get(cacheKey).orElse(List.of()))
                .switchIfEmpty(
                        Flux.defer(() -> schedulesAPIWebClient.findScheduledFlights(departureAirportCode, arrivalAirportCode, year, month)
                        .doOnNext(monthSchedules -> flightSchedulesCache.put(cacheKey, monthSchedules.getDays()))
                        .flatMapIterable(MonthSchedules::getDays)))
                .filter(dayOfMonthSchedules -> dayOfMonthSchedules.getDay().equalsIgnoreCase(String.valueOf(dayOfMonth)))
                .flatMapIterable(DayOfMonthSchedules::getFlights);
    }

    @Override
    public Flux<DayOfMonthSchedules> getFlightSchedules(String departureAirportCode, String arrivalAirportCode, int year, int month) {
        var cacheKey = generateCacheKey(departureAirportCode, arrivalAirportCode, year, month);
        return Flux.fromIterable(flightSchedulesCache.get(cacheKey).orElse(List.of()))
                .switchIfEmpty(
                    Flux.defer(()->schedulesAPIWebClient.findScheduledFlights(departureAirportCode, arrivalAirportCode, year, month)
                        .doOnNext(monthSchedules -> flightSchedulesCache.put(cacheKey, monthSchedules.getDays()))
                        .flatMapIterable(MonthSchedules::getDays))
                );
    }

    @Override
    public String generateCacheKey(String departureAirportCode, String arrivalAirportCode, int year, int month) {
        return departureAirportCode + arrivalAirportCode + year + month;
    }
}
