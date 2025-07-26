package dev.ezekielmartin.availableflightsfinder.service.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.ezekielmartin.availableflightsfinder.model.FlightRoute;
import dev.ezekielmartin.availableflightsfinder.service.FlightRoutesServiceImpl;
import dev.ezekielmartin.availableflightsfinder.webclient.RoutesAPIWebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static dev.ezekielmartin.availableflightsfinder.config.Defaults.DIRECT_FLIGHT_EXPRESSION;

@Service
@Slf4j
public class FlightRoutesCacheImpl implements FlightRoutesCache {
    private List<FlightRoute> currentFlightRoutes = List.of();
    private final Cache<String, LinkedHashSet<String>> flightRoutesCache;

    @Autowired
    RoutesAPIWebClient routesAPIWebClient;

    public FlightRoutesCacheImpl() {
        this.flightRoutesCache = Caffeine.newBuilder().build();
    }

    @Override
    @CachePut(value = "flightRoutes", key = "#originAndDestinationAiport")
    public void put(String originAndDestinationAirport, LinkedHashSet<String> connectingAirports) {
        flightRoutesCache.put(originAndDestinationAirport, connectingAirports);
    }

    @Override
    @Cacheable(value = "flightRoutes", key = "#originAndDestinationAirport")
    public Optional<LinkedHashSet<String>> get(String originAndDestinationAirport) {
        return Optional.ofNullable(flightRoutesCache.getIfPresent(originAndDestinationAirport));
    }

    @Override
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS, initialDelay = 30)
    public void updateCache(){
        log.info("Calling flight routes API to detect flight routes changes");
        routesAPIWebClient.getAllFlightRoutes()
            .map(FlightRoutesServiceImpl::filterValidFlights)
            .filter(this::shouldInvalidateCache)
            .doOnNext(flightRoutes -> {
                log.info("Flight routes changes detected, proceeding to update flight routes cache");
                currentFlightRoutes = flightRoutes;
                flightRoutesCache.invalidateAll();
            })
            .map(this::cacheFormatMapper)
            .doOnNext(stringListMap -> {
                stringListMap.forEach(this::put);
                log.info("Updated flight routes cache");
            })
            .subscribe();
    }

    private boolean shouldInvalidateCache(List<FlightRoute> lastFetchedFlightRoutes) {
        return !currentFlightRoutes.equals(lastFetchedFlightRoutes);
    }

    public Map<String, LinkedHashSet<String>> cacheFormatMapper(List<FlightRoute> flightRoutes){
        var directFlightsMap = new HashMap<String, LinkedHashSet<String>>();
        var formattedMap = new HashMap<String, LinkedHashSet<String>>();
        flightRoutes
            .forEach(flightRoute -> {
                flightRoutes.stream()
                    .filter(flightRoute1 -> flightRoute1.getAirportFrom().equalsIgnoreCase(flightRoute.getAirportFrom()))
                    .forEach(flightRouteSameOrigin -> {
                        directFlightsMap
                                .computeIfAbsent(flightRoute.getAirportFrom(), originAirport -> new LinkedHashSet<>())
                                .add(flightRouteSameOrigin.getAirportTo());
                    });
                }
            );
        directFlightsMap.forEach(
            (originAirport, destinationAirports) -> {
                destinationAirports.forEach(currentDestinationAirport -> {
                    formattedMap.computeIfAbsent(originAirport.concat(currentDestinationAirport), key -> new LinkedHashSet<String>(List.of(DIRECT_FLIGHT_EXPRESSION))).add(DIRECT_FLIGHT_EXPRESSION);
                    if(directFlightsMap.containsKey(currentDestinationAirport)){
                        LinkedHashSet<String> destinationsOfDestination = directFlightsMap.get(currentDestinationAirport);
                        destinationsOfDestination.forEach(indirectDestination -> {
                            formattedMap.computeIfAbsent(originAirport.concat(indirectDestination), key-> new LinkedHashSet<String>(List.of(currentDestinationAirport))).add(currentDestinationAirport);
                        });
                    }
                });
            }
        );

        return formattedMap;
    }
}
