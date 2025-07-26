package dev.ezekielmartin.availableflightsfinder.service;

import dev.ezekielmartin.availableflightsfinder.model.FlightRoute;
import dev.ezekielmartin.availableflightsfinder.service.cache.FlightRoutesCacheImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.LinkedHashSet;
import java.util.List;

import static dev.ezekielmartin.availableflightsfinder.config.Defaults.CONNECTING_AIRPORT_NULL;
import static dev.ezekielmartin.availableflightsfinder.config.Defaults.OPERATOR_NAME_VALID;

@Service
public class FlightRoutesServiceImpl implements FlightRoutesService {
    @Autowired
    FlightRoutesCacheImpl flightRoutesCacheImpl;

    @Override
    public Flux<LinkedHashSet<String>> findConnectingFlightRoutes(String airportFrom, String airportTo) {
        return flightRoutesCacheImpl.get(airportFrom.concat(airportTo)).map(Flux::just).orElseGet(Flux::empty);

    }

    public static List<FlightRoute> filterValidFlights(List<FlightRoute> flightRoutes) {
        return flightRoutes.stream().filter(CONNECTING_AIRPORT_NULL).filter(OPERATOR_NAME_VALID).toList();
    }
}
