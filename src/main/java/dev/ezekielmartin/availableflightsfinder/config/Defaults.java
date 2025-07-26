package dev.ezekielmartin.availableflightsfinder.config;

import dev.ezekielmartin.availableflightsfinder.model.FlightRoute;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Predicate;

public class Defaults {
    private static final String VALID_OPERATOR_NAME = "ryanair";
    public static final Long DEFAULT_CACHE_TTL_MINUTES = 5L;
    public static final Long DEFAULT_TIMEOUT_MILLIS = 5000L;
    public static final String DIRECT_FLIGHT_EXPRESSION = "DIRECT";
    public static final Retry DEFAULT_RETRY_SPEC = Retry.backoff(3, Duration.ofMillis(500L));
    public static final Predicate<FlightRoute> CONNECTING_AIRPORT_NULL = flightRoute -> flightRoute.getConnectingAirport() == null;
    public static final Predicate<FlightRoute> OPERATOR_NAME_VALID = flightRoute -> flightRoute.getOperator().equalsIgnoreCase(VALID_OPERATOR_NAME);
}
