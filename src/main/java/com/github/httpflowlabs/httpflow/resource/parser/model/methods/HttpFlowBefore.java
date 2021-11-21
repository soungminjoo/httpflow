package com.github.httpflowlabs.httpflow.resource.parser.model.methods;

import com.github.httpflowlabs.httpflow.resource.parser.enums.HttpFlowProtocol;
import com.github.httpflowlabs.httpflow.resource.parser.exception.HttpFlowException;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowHeaderValues;
import lombok.Getter;

import java.util.Map;

@Getter
public class HttpFlowBefore {

    public static final String METHOD_BEFORE = "BEFORE";

    private Map<String, HttpFlowHeaderValues> headers;

    public HttpFlowBefore(HttpFlowElement element) {
        if (element.getProtocol() == HttpFlowProtocol.HTTPFLOW && element.getMethod().equals(METHOD_BEFORE)) {
            this.headers = element.getHeaders();
        } else {
            throw new HttpFlowException("Invalid element - Protocol must be " + HttpFlowProtocol.HTTPFLOW.name() + " and method must be " + METHOD_BEFORE + ".");
        }
    }

    public HttpFlowHeaderValues getHeader(String name) {
        HttpFlowHeaderValues values = headers.get(name);
        if (values == null) {
            return new HttpFlowHeaderValues();
        }
        return values;
    }

}
