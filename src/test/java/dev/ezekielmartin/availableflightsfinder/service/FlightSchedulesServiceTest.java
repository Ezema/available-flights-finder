package dev.ezekielmartin.availableflightsfinder.service;

import dev.ezekielmartin.availableflightsfinder.model.DayOfMonthSchedules;
import dev.ezekielmartin.availableflightsfinder.model.FlightSchedule;
import dev.ezekielmartin.availableflightsfinder.model.MonthSchedules;
import dev.ezekielmartin.availableflightsfinder.service.cache.FlightSchedulesCache;
import dev.ezekielmartin.availableflightsfinder.webclient.SchedulesAPIWebClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class FlightSchedulesServiceTest {
    @Mock
    SchedulesAPIWebClient schedulesAPIWebClient;
    @Mock
    FlightSchedulesCache flightSchedulesCache;
    @InjectMocks
    FlightSchedulesServiceImpl flightSchedulesService;

    @Test
    void shouldReturnFlightSchedules() {
        var list = List.of(
                new DayOfMonthSchedules("1", List.of(new FlightSchedule("",0,"12:00", "14:00"))),
                new DayOfMonthSchedules("2", List.of(new FlightSchedule("",0,"12:00", "14:00")))
        );
        Mockito.when(flightSchedulesCache.get(flightSchedulesService.generateCacheKey("MAD", "DUB", 2025, 7)))
                .thenReturn(Optional.of(list));
        Mockito.when(schedulesAPIWebClient.findScheduledFlights(any(), any(), anyInt(), anyInt())).thenReturn(Flux.just(new MonthSchedules("7", list)));

        var expected = new FlightSchedule("",0,"12:00","14:00");
        StepVerifier.create(
                flightSchedulesService.getFlightSchedules("MAD","DUB",2025, 7, 1))
                .expectNext(expected)
                .verifyComplete();

    }
}
