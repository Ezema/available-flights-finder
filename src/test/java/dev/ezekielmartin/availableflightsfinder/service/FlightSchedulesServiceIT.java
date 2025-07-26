package dev.ezekielmartin.availableflightsfinder.service;

import dev.ezekielmartin.availableflightsfinder.model.DayOfMonthSchedules;
import dev.ezekielmartin.availableflightsfinder.model.FlightSchedule;
import dev.ezekielmartin.availableflightsfinder.model.MonthSchedules;
import dev.ezekielmartin.availableflightsfinder.service.cache.FlightSchedulesCacheImpl;
import dev.ezekielmartin.availableflightsfinder.webclient.SchedulesAPIWebClient;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@SpringBootTest
public class FlightSchedulesServiceIT {
    @Autowired
    FlightSchedulesServiceImpl flightSchedulesService;
    @MockitoSpyBean
    FlightSchedulesCacheImpl flightSchedulesCache;
    @MockitoSpyBean
    SchedulesAPIWebClient schedulesAPIWebClient;
    @Test
    @Order(1)
    void shouldFindAllFlightSchedulesRetrievedFromAPI_whenCacheIsEmpty() {
        var list = List.of(
                new DayOfMonthSchedules("1", List.of(new FlightSchedule("",0,"12:00", "14:00"))),
                new DayOfMonthSchedules("2", List.of(new FlightSchedule("",0,"12:00", "14:00")))
        );
        Mockito.when(schedulesAPIWebClient.findScheduledFlights(any(), any(), anyInt(), anyInt())).thenReturn(Flux.just(new MonthSchedules("7", list)));
        StepVerifier
                .create(flightSchedulesService.getFlightSchedules("MAD", "DUB", 2025, 7))
                .expectNext(new DayOfMonthSchedules("1", List.of(new FlightSchedule("",0,"12:00", "14:00"))),
                        new DayOfMonthSchedules("2", List.of(new FlightSchedule("",0,"12:00", "14:00"))))
                .verifyComplete();

        Mockito.verify(flightSchedulesCache, Mockito.times(1)).put(any(), any());
        Mockito.verify(schedulesAPIWebClient, Mockito.times(1)).findScheduledFlights(any(), any(), anyInt(), anyInt());

    }

    @Test
    @Order(2)
    void shouldFindAllFlightSchedulesFromCache_whenCacheHits() {
        var list = List.of(
                new DayOfMonthSchedules("1", List.of(new FlightSchedule("",0,"12:00", "14:00"))),
                new DayOfMonthSchedules("2", List.of(new FlightSchedule("",0,"12:00", "14:00")))
        );
        flightSchedulesCache.put(flightSchedulesService.generateCacheKey("MAD", "DUB", 2025, 7), list);
        StepVerifier
                .create(flightSchedulesService.getFlightSchedules("MAD", "DUB", 2025, 7))
                .expectNext(new DayOfMonthSchedules("1", List.of(new FlightSchedule("",0,"12:00", "14:00"))),
                        new DayOfMonthSchedules("2", List.of(new FlightSchedule("",0,"12:00", "14:00"))))
                .verifyComplete();

        Mockito.verify(flightSchedulesCache, Mockito.times(1)).get(any());
        Mockito.verify(schedulesAPIWebClient, Mockito.times(0)).findScheduledFlights(any(), any(), anyInt(), anyInt());
    }

}
