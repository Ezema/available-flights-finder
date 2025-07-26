package dev.ezekielmartin.availableflightsfinder.service.cache;

import dev.ezekielmartin.availableflightsfinder.model.FlightRoute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.ezekielmartin.availableflightsfinder.config.Defaults.DIRECT_FLIGHT_EXPRESSION;

@ExtendWith(MockitoExtension.class)
public class FlightRoutesCacheTest {
    @InjectMocks
    FlightRoutesCacheImpl flightRoutesCacheImpl;

    @Test
    void shouldFormatCache() {
        var input = List.of(
                new FlightRoute("MAD", "DUB", null, "ryanair"),
                new FlightRoute("MAD", "STN", null, "ryanair"),
                new FlightRoute("STN", "BER", null, "ryanair"),
                new FlightRoute("STN", "DUB", null, "ryanair"),
                new FlightRoute("BER", "MAD", null, "ryanair"));
        var expectedOutput = Map.of(
                "MADDUB", Set.of(DIRECT_FLIGHT_EXPRESSION, "STN"),
                "MADSTN", Set.of(DIRECT_FLIGHT_EXPRESSION),
                "STNBER", Set.of(DIRECT_FLIGHT_EXPRESSION),
                "BERMAD", Set.of(DIRECT_FLIGHT_EXPRESSION),
                "STNMAD", Set.of("BER"),
                "BERDUB", Set.of("MAD"),
                "STNDUB", Set.of(DIRECT_FLIGHT_EXPRESSION),
                "MADBER", Set.of("STN"),
                "BERSTN", Set.of("MAD")
                );
        expectedOutput = sortMap(expectedOutput);
        var output = flightRoutesCacheImpl.cacheFormatMapper(input);
        output = sortMap(output);
        Assertions.assertEquals(expectedOutput, output);
    }

    private static <T extends Comparable<? super T>, R> Map<T,R> sortMap(Map<T,R> map){
        return map
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (before,after)->before,LinkedHashMap::new));
    }

}
