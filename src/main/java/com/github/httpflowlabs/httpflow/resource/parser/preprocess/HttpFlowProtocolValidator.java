package com.github.httpflowlabs.httpflow.resource.parser.preprocess;

import com.github.httpflowlabs.httpflow.resource.parser.exception.MalformedHttpFlowException;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.resource.parser.model.methods.HttpFlowSetting;

public class HttpFlowProtocolValidator {

    public void validate(HttpFlowElement element, HttpFlowLine currLine) {
        if (element.getMethod().equalsIgnoreCase("SETTING")) {
            HttpFlowSetting.validate(currLine);
        }
    }

    public static void assertBooleanValue(String name, String value, int lineNumber) {
        if ("true|false".indexOf(value.toLowerCase()) == -1) {
            throw new MalformedHttpFlowException(name + " must be 'true' or 'false'. ", lineNumber);
        }
    }

}
