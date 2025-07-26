package dev.ezekielmartin.availableflightsfinder.service;

import reactor.core.publisher.Flux;

import java.util.LinkedHashSet;

public interface FlightRoutesService {
    Flux<LinkedHashSet<String>> findConnectingFlightRoutes(String airportFrom, String airportTo);
}
