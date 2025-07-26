package dev.ezekielmartin.availableflightsfinder.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class CachePreloaderService implements ApplicationRunner {
    @Autowired
    FlightRoutesCacheImpl flightRoutesCacheImpl;

    @Override
    public void run(ApplicationArguments args) {
        flightRoutesCacheImpl.updateCache();
    }
}
