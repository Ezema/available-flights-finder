package dev.ezekielmartin.availableflightsfinder.service.cache;

import java.util.LinkedHashSet;
import java.util.Optional;

public interface FlightRoutesCache {
    void put (String originAndDestinationAirport, LinkedHashSet<String> connectingAirports);
    Optional<LinkedHashSet<String>> get(String originAndDestinationAirport);
    void updateCache();
}
