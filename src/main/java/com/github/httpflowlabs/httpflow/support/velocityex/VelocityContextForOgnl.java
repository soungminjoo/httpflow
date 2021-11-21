package com.github.httpflowlabs.httpflow.support.velocityex;

import org.apache.velocity.VelocityContext;

public class VelocityContextForOgnl extends VelocityContext  {

    public VelocityContextForOgnl(VelocityContext velocityContext) {
        for (String key : velocityContext.getKeys()) {
            super.put(key, velocityContext.get(key));
        }
    }

    @Override
    public Object get(String key) {
        Object value = super.get(key);
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        return value;
    }

}
