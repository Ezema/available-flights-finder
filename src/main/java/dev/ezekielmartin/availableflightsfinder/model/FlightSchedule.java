package dev.ezekielmartin.availableflightsfinder.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FlightSchedule {
    private String carrierCode;
    private Integer number;
    private String departureTime;
    private String arrivalTime;
}
