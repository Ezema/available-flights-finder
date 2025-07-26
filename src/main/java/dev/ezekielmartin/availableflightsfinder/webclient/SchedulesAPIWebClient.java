package dev.ezekielmartin.availableflightsfinder.webclient;

import dev.ezekielmartin.availableflightsfinder.model.MonthSchedules;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Optional;

import static dev.ezekielmartin.availableflightsfinder.config.Defaults.*;

@Service
public class SchedulesAPIWebClient {
    private final WebClient webClient;
    private Optional<Long> timeoutMillis;
    private Optional<Long> cacheTtlMinutes;
    SchedulesAPIWebClient (@Value("${app.config.web-clients-config.schedules-api.base-url}") String baseUrl,
                           @Value("${app.config.web-clients-config.default.timeout}") Long timeoutMillis,
                           @Value("${app.config.web-clients-config.schedules-api.cache-ttl-minutes}") Long cacheTtlMinutes) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.timeoutMillis = Optional.ofNullable(timeoutMillis);
        this.cacheTtlMinutes = Optional.ofNullable(cacheTtlMinutes);
    }
    public Flux<MonthSchedules> findScheduledFlights(String departureAirport, String arrivalAirport, int year, @Range(min = 1, max = 12) int month) {
        return webClient
            .get()
            .uri(String.format("/%s/%s/years/%d/months/%d", departureAirport, arrivalAirport, year, month))
            .retrieve()
            .bodyToFlux(MonthSchedules.class)
            .cache(Duration.ofMinutes(cacheTtlMinutes.orElse(DEFAULT_CACHE_TTL_MINUTES)))
            .timeout(Duration.ofMillis(timeoutMillis.orElse(DEFAULT_TIMEOUT_MILLIS)))
            .retryWhen(DEFAULT_RETRY_SPEC);
    }
}