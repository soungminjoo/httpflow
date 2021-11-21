package com.github.httpflowlabs.httpflow.support.easyread.regex.support;

import java.util.ArrayList;
import java.util.List;

public class DelayedStringBuilder {

    private StringBuilder builder = null;
    private List<Object> appendedObjects = new ArrayList<>();

    public DelayedStringBuilder append(Object obj) {
        appendedObjects.add(obj);
        return this;
    }

    @Override
    public String toString() {
        builder = new StringBuilder();
        for (Object obj : appendedObjects) {
            builder.append(obj);
        }

        return builder.toString();
    }
}
