package dev.ezekielmartin.availableflightsfinder.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MonthSchedules {
    private String month;
    private List<DayOfMonthSchedules> days;
}
