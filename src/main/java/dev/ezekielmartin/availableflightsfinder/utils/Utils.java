package dev.ezekielmartin.availableflightsfinder.utils;

import dev.ezekielmartin.availableflightsfinder.model.FlightFinderResult;
import dev.ezekielmartin.availableflightsfinder.model.FlightLegData;
import dev.ezekielmartin.availableflightsfinder.model.FlightSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

import static dev.ezekielmartin.availableflightsfinder.model.NumberOfStops.DIRECT;
import static dev.ezekielmartin.availableflightsfinder.model.NumberOfStops.ONE_STOP;

public class Utils {
    static final DateTimeFormatter flightHourMinutesFormatter = DateTimeFormatter.ofPattern("HH:mm");
    public static LocalTime toLocalTime(String hourMinutes){
        return LocalTime.parse(hourMinutes, flightHourMinutesFormatter);
    }
    public static boolean isValidDirectFlightSchedule(@NotNull int year, @NotNull @Range(min = 1, max = 12) int month, @NotNull @Range(min = 1, max = 31) int dayOfMonth, FlightSchedule flightSchedule, LocalDateTime desiredDeparture, LocalDateTime desiredArrival){
        var departureTime = toLocalTime(flightSchedule.getDepartureTime());
        var arrivalTime = toLocalTime(flightSchedule.getArrivalTime());
        var arrivalDate = LocalDate.of(year, month, dayOfMonth);
        arrivalDate = arrivalTime.isBefore(departureTime) ? arrivalDate.plusDays(1) : arrivalDate;
        LocalDateTime actualDeparture = LocalDateTime.of(LocalDate.of(year, month, dayOfMonth),departureTime);
        LocalDateTime actualArrival = LocalDateTime.of(arrivalDate,arrivalTime);
        var departureIsValid = !actualDeparture.isBefore(desiredDeparture);
        var arrivalIsValid = !actualArrival.isAfter(desiredArrival);
        return departureIsValid && arrivalIsValid;
    }

    public static boolean isValidOneStopFlightSchedule(@NotNull int year, @NotNull @Range(min = 1, max = 12) int month, @NotNull @Range(min = 1, max = 31) int dayOfMonth, FlightSchedule firstLegFlightSchedule, FlightSchedule secondLegFlightSchedule, LocalDateTime desiredArrival){
        LocalDateTime firstLegArrival = LocalDateTime.of(LocalDate.of(year, month, dayOfMonth),toLocalTime(firstLegFlightSchedule.getArrivalTime()));
        LocalDateTime secondLegDeparture = LocalDateTime.of(LocalDate.of(year, month, dayOfMonth),toLocalTime(secondLegFlightSchedule.getDepartureTime()));
        LocalDateTime secondLegArrival = LocalDateTime.of(LocalDate.of(year, month, dayOfMonth),toLocalTime(secondLegFlightSchedule.getArrivalTime()));
        secondLegArrival = secondLegArrival.isBefore(secondLegDeparture)? secondLegArrival.plusDays(1) : secondLegArrival;
        var atLeastTwoHoursBetweenEachLeg = !secondLegDeparture.isBefore(firstLegArrival.plusHours(2L));
        var arrivalIsValid = !secondLegArrival.isAfter(desiredArrival);
        return atLeastTwoHoursBetweenEachLeg && arrivalIsValid;
    }

    public static FlightFinderResult getDirectFlightFinderResult(@NotNull int year, @NotNull @Range(min = 1, max = 12) int month, @NotNull @Range(min = 1, max = 31) int dayOfMonth, String departureAirport, String arrivalAirport, FlightSchedule flightSchedule){
        return new FlightFinderResult(DIRECT,
            List.of(
                    new FlightLegData(departureAirport, arrivalAirport, LocalDateTime.of(LocalDate.of(year, month, dayOfMonth),toLocalTime(flightSchedule.getDepartureTime())) , LocalDateTime.of(LocalDate.of(year, month, dayOfMonth),toLocalTime(flightSchedule.getArrivalTime())))
        ));
    }

    public static FlightFinderResult getOneStopFlightFinderResult(@NotNull int year, @NotNull @Range(min = 1, max = 12) int month, @NotNull @Range(min = 1, max = 31) int dayOfMonth, @NotBlank String departureAirport, @NotBlank String connectionAirport, @NotBlank String arrivalAirport, @NotNull FlightSchedule firstLegFlightSchedule, @NotNull FlightSchedule secondLegFlightSchedule){
        return new FlightFinderResult(ONE_STOP,
                List.of(
                        new FlightLegData(departureAirport, connectionAirport, LocalDateTime.of(LocalDate.of(year, month, dayOfMonth),toLocalTime(firstLegFlightSchedule.getDepartureTime())) , LocalDateTime.of(LocalDate.of(year, month, dayOfMonth),toLocalTime(firstLegFlightSchedule.getArrivalTime()))),
                        new FlightLegData(connectionAirport, arrivalAirport, LocalDateTime.of(LocalDate.of(year, month, dayOfMonth),toLocalTime(secondLegFlightSchedule.getDepartureTime())) , LocalDateTime.of(LocalDate.of(year, month, dayOfMonth),toLocalTime(secondLegFlightSchedule.getArrivalTime())))
                ));

    }

    public static boolean isValidIataCode(@NotBlank String iataCode){
        Pattern pattern = Pattern.compile("^[A-Z]{3}$");
        return pattern.matcher(iataCode).find();
    }
}