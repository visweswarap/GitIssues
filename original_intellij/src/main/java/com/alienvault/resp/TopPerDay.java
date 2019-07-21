package com.alienvault.resp;

import java.util.Map;

public class TopPerDay {

    private String day;

    private Map<String, String> occurrences;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Map<String, String> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(Map<String, String> occurrences) {
        this.occurrences = occurrences;
    }
}
