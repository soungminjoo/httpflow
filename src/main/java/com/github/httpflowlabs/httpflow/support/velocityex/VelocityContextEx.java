package com.github.httpflowlabs.httpflow.support.velocityex;

import org.apache.velocity.VelocityContext;

import java.lang.reflect.Field;
import java.util.Map;

public class VelocityContextEx extends VelocityContext {

    public VelocityContextEx() {

    }

    public VelocityContextEx(VelocityContext velocityContext) {
        super(velocityContext);
    }

    public void putAll(Map<String, ?> map) {
        for (String key : map.keySet()) {
            super.put(key, map.get(key));
        }
    }

    public void putAll(VelocityContext velocityContext) {
        for (String key : velocityContext.getKeys()) {
            super.put(key, velocityContext.get(key));
        }
    }

    public Map<String, Object> toMap() {
        for (Field f : VelocityContext.class.getDeclaredFields()) {
            if (f.getType().equals(Map.class)) {
                f.setAccessible(true);
                try {
                    return (Map<String, Object>) f.get(this);
                } catch (IllegalAccessException e) {
                }
            }
        }
        throw new IllegalStateException("Cannot access to velocity context map");
    }

}
