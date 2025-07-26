package dev.ezekielmartin.availableflightsfinder.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DayOfMonthSchedules {
    private String day;
    private List<FlightSchedule> flights;
}
