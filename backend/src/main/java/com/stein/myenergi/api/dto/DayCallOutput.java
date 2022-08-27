package com.stein.myenergi.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;

@JsonIgnoreProperties
public class DayCallOutput extends HashMap<String, HistoryDay[]> implements MyenergiCallOutput {

    public HistoryDay[] getHistoryDay() {
        return this.keySet().stream().findFirst().map((key) -> this.get(key)).orElse(null);
    }
}
