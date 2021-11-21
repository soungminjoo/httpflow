package com.github.httpflowlabs.httpflow.support.velocityex;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;

public class ReferenceHandlerForOgnl implements ReferenceInsertionEventHandler {

    @Override
    public Object referenceInsert(Context context, String reference, Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        return value;
    }

    private void test() {

    }
}
