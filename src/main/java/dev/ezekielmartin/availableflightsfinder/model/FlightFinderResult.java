package dev.ezekielmartin.availableflightsfinder.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FlightFinderResult {
    private NumberOfStops stops;
    private List<FlightLegData> flightLegs;
}
