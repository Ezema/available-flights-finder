package dev.ezekielmartin.availableflightsfinder.webclient;

import dev.ezekielmartin.availableflightsfinder.model.FlightRoute;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static dev.ezekielmartin.availableflightsfinder.config.Defaults.DEFAULT_TIMEOUT_MILLIS;
import static dev.ezekielmartin.availableflightsfinder.config.Defaults.DEFAULT_RETRY_SPEC;

@Component
@Slf4j
@Getter
public class RoutesAPIWebClient {
    private String baseUrl;
    private WebClient webClient;
    private Optional<Long> timeoutMillis;
    RoutesAPIWebClient(@Value("${app.config.web-clients-config.routes-api.base-url}") String baseUrl,@Value("${app.config.web-clients-config.default.timeout}") Long timeoutMillis) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalArgumentException("Route API URL is null or empty");
        }
        this.baseUrl = baseUrl;
        this.timeoutMillis = Optional.ofNullable(timeoutMillis);
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<List<FlightRoute>> getAllFlightRoutes() {
        return webClient
                .get()
                .retrieve()
                .bodyToFlux(FlightRoute.class)
                .collectList()
                .timeout(Duration.ofMillis(timeoutMillis.orElse(DEFAULT_TIMEOUT_MILLIS)))
                .retryWhen(DEFAULT_RETRY_SPEC)
                .doOnError(throwable -> log.error(throwable.getMessage(), throwable));
    }
}
