package dev.ezekielmartin.availableflightsfinder.utils;

import dev.ezekielmartin.availableflightsfinder.model.FlightFinderResult;
import dev.ezekielmartin.availableflightsfinder.model.FlightLegData;
import dev.ezekielmartin.availableflightsfinder.model.FlightSchedule;
import dev.ezekielmartin.availableflightsfinder.model.NumberOfStops;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class UtilsTest {
    @ParameterizedTest
    @MethodSource("hourMinutes")
    void toLocalTimeTest(String hourMinutes, LocalTime expected) {
        LocalTime actual = Utils.toLocalTime(hourMinutes);
        assertEquals(expected, actual);
    }
    @Test
    void isValidDirectFlightScheduleTest(){
        FlightSchedule flightSchedule = new FlightSchedule("",1, "11:00", "23:59");
        LocalDate date = LocalDate.of(2025, 7, 24);
        assertEquals(false, Utils.isValidDirectFlightSchedule(2025, 7, 24, flightSchedule,
                LocalDateTime.of(date, LocalTime.of(12, 0)),
                LocalDateTime.of(date, LocalTime.of(18, 0))));
        assertEquals(true, Utils.isValidDirectFlightSchedule(2025, 7, 24, flightSchedule,
                LocalDateTime.of(date, LocalTime.of(11, 0)),
                LocalDateTime.of(date, LocalTime.of(23, 59))));
        assertEquals(false, Utils.isValidDirectFlightSchedule(2025, 7, 24, flightSchedule,
                LocalDateTime.of(date, LocalTime.of(10, 0)),
                LocalDateTime.of(date, LocalTime.of(0, 0))));
    }

    @Test
    void isValidOneStopFlightScheduleTest(){
        FlightSchedule leg1 = new FlightSchedule("",0,"09:00", "10:00");
        FlightSchedule leg2 = new FlightSchedule("",0,"12:30", "14:00");
        LocalDateTime arrival = LocalDateTime.of(2025, 7, 25, 15, 0);

        assertEquals(true,Utils.isValidOneStopFlightSchedule(
                2025, 7, 25, leg1, leg2, arrival
        ));
    }

    @Test
    void getDirectFlightFinderResultTest(){
        var leg1 = new FlightLegData("ABC", "DEF",  LocalDateTime.of(2025, 7, 24, 8, 0), LocalDateTime.of(2025, 7, 24, 10, 0));
        FlightFinderResult expected = new FlightFinderResult(NumberOfStops.DIRECT, List.of(leg1));
        FlightFinderResult expectedFalse = new FlightFinderResult(NumberOfStops.ONE_STOP, List.of(leg1));
        assertEquals(expected,Utils.getDirectFlightFinderResult(2025, 7, 24, "ABC", "DEF",new FlightSchedule("", 0, "08:00", "10:00")));
        assertNotEquals(expectedFalse,Utils.getDirectFlightFinderResult(2025, 7, 24, "ABC", "DEF",new FlightSchedule("", 0, "08:00", "10:00")));
    }

    @Test
    void getOneStopFlightFinderResult(){
        var leg1 = new FlightLegData("ABC", "DEF",  LocalDateTime.of(2025, 7, 24, 8, 0), LocalDateTime.of(2025, 7, 24, 10, 0));
        var leg2 = new FlightLegData("DEF", "XYZ",  LocalDateTime.of(2025, 7, 24, 12, 0),  LocalDateTime.of(2025, 7, 24, 14, 0));
        FlightFinderResult expected = new FlightFinderResult(NumberOfStops.ONE_STOP, List.of(leg1, leg2));
        FlightFinderResult expectedFalse = new FlightFinderResult(NumberOfStops.DIRECT, List.of(leg1, leg2));
        assertEquals(expected,
        Utils.getOneStopFlightFinderResult(2025, 7, 24, "ABC", "DEF", "XYZ"
                , new FlightSchedule("",0, "08:00", "10:00")
                , new FlightSchedule("",0, "12:00", "14:00")));
        assertNotEquals(expectedFalse, Utils.getOneStopFlightFinderResult(2025, 7, 24, "ABC", "DEF", "XYZ"
                , new FlightSchedule("", 0, "08:00", "10:00")
                , new FlightSchedule("", 0, "12:00", "14:00")));
    }

    @ParameterizedTest
    @MethodSource("iataCodes")
    void isValidIataCodeTest(String iataCode,Boolean isValid) {
        assertEquals(Utils.isValidIataCode(iataCode), isValid);
    }

    static Stream<Arguments> iataCodes(){
        return Stream.of(
                arguments("x", false),
                arguments("", false),
                arguments("AB", false),
                arguments("AAAA", false),
                arguments("AAA", true),
                arguments("MAD", true),
                arguments("DUB", true)
        );
    }
    static Stream<Arguments> hourMinutes() {
        return Stream.of(
                arguments("00:00", LocalTime.of(0, 0)),
                arguments("07:05", LocalTime.of(7, 5)),
                arguments("12:30", LocalTime.of(12, 30)),
                arguments("23:59", LocalTime.of(23, 59))
        );
    }
}
