package dev.ezekielmartin.availableflightsfinder.service.cache;

import dev.ezekielmartin.availableflightsfinder.model.DayOfMonthSchedules;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class FlightSchedulesCacheTest {
    @Mock
    FlightSchedulesCache flightSchedulesCache;

    @Test
    void whenCacheIsEmpty_shouldReturnEmpty() {
        Mockito.when(flightSchedulesCache.get(any())).thenReturn(Optional.empty());
        Assertions.assertThat(flightSchedulesCache.get(any())).isEqualTo(Optional.empty());
    }
    @Test
    void whenCacheHits_shouldReturnList() {
        Mockito.when(flightSchedulesCache.get(any())).thenReturn(Optional.of(List.of(new DayOfMonthSchedules("", List.of()))));
        Assertions.assertThat(flightSchedulesCache.get(any())).isEqualTo(Optional.of(List.of(new DayOfMonthSchedules("", List.of()))));
    }
}
