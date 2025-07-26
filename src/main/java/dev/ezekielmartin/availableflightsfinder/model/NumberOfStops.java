package dev.ezekielmartin.availableflightsfinder.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NumberOfStops {
    DIRECT,ONE_STOP;
    @JsonValue
    public int toOrdinal() {
        return this.ordinal();
    }
}
