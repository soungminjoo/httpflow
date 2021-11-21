package com.github.httpflowlabs.httpflow.resource.parser.model;

import java.util.ArrayList;
import java.util.List;

public class HttpFlowHeaderValues {

    private List<String> values = new ArrayList<>();

    public int size() {
        return values.size();
    }

    public boolean exists() {
        return values.size() > 0;
    }

    public void add(String value) {
        this.values.add(value);
    }

    public String value(int index) {
        return this.values.get(index);
    }

    public String lastValue() {
        if (this.values.size() > 0) {
            return this.values.get(this.values.size() - 1);
        }
        return null;
    }

    public List<String> asList() {
        return this.values;
    }

    public boolean lastValueAsBoolean(boolean defaultValue) {
        String value = this.lastValue();
        if (value == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value);
    }

    public int lastValueAsInt(int defaultValue) {
        String value = this.lastValue();
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }

}
