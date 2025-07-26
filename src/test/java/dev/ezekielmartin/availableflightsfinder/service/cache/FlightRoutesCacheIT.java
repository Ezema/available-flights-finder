package dev.ezekielmartin.availableflightsfinder.service.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;

import static dev.ezekielmartin.availableflightsfinder.config.Defaults.DIRECT_FLIGHT_EXPRESSION;

@SpringBootTest
public class FlightRoutesCacheIT {
    @Autowired
    FlightRoutesCache flightRoutesCache;

    @Test
    public void whenCacheIsEmpty_thenReturnEmpty() {
        Assertions.assertEquals(Optional.empty(),flightRoutesCache.get("MADDUB"));
    }

    @Test
    public void whenCacheHit_thenReturnList() {
        var expected = new LinkedHashSet<>(Arrays.asList(DIRECT_FLIGHT_EXPRESSION, "STN"));
        flightRoutesCache.put("MADDUB", expected);
        Assertions.assertEquals(expected,flightRoutesCache.get("MADDUB").get());
    }

}
