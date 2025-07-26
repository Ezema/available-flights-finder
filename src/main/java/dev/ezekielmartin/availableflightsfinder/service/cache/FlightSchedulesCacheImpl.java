package dev.ezekielmartin.availableflightsfinder.service.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.ezekielmartin.availableflightsfinder.model.DayOfMonthSchedules;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class FlightSchedulesCacheImpl implements FlightSchedulesCache {
    private final Cache<String, List<DayOfMonthSchedules>> cache;
    FlightSchedulesCacheImpl(@Value("${app.config.web-clients-config.schedules-api.cache-ttl-minutes}") int cacheTtlMinutes) {
        this.cache = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(cacheTtlMinutes)).build();
    }
    @Override
    @CachePut(value = "schedulesCache", key = "#depAirportArrAirportYearMonth")
    public void put(String depAirportArrAirportYearMonth, List<DayOfMonthSchedules> dayOfMonthSchedulesList) {
        cache.put(depAirportArrAirportYearMonth, dayOfMonthSchedulesList);
    }

    @Override
    @Cacheable(value = "schedulesCache", key = "#depAirportArrAirportYearMonth")
    public Optional<List<DayOfMonthSchedules>> get(String depAirportArrAirportYearMonth) {
        return Optional.ofNullable(cache.getIfPresent(depAirportArrAirportYearMonth));
    }
}
