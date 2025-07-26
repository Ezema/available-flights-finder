package dev.ezekielmartin.availableflightsfinder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class FlightRoute {
    String airportFrom;
    String airportTo;
    String connectingAirport;
    String operator;
}
