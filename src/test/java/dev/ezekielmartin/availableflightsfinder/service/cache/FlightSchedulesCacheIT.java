package dev.ezekielmartin.availableflightsfinder.service.cache;

import dev.ezekielmartin.availableflightsfinder.model.DayOfMonthSchedules;
import dev.ezekielmartin.availableflightsfinder.model.FlightSchedule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class FlightSchedulesCacheIT {
    @Autowired
    FlightSchedulesCache flightSchedulesCache;

    @Test
    void shouldReturnEmpty_whenCacheIsEmpty() {
        Assertions.assertThat(flightSchedulesCache.get("")).isEmpty();
    }

    @Test
    void shouldReturnList_whenCacheHits() {
        var expected = List.of(new DayOfMonthSchedules("1", List.of(new FlightSchedule("",1,"",""))));
        flightSchedulesCache.put("202507", expected);
        Assertions.assertThat(flightSchedulesCache.get("202507")).isEqualTo(Optional.of(expected));
    }

}
