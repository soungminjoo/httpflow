package com.github.httpflowlabs.httpflow.resource.parser.model;

import com.github.httpflowlabs.httpflow.resource.parser.enums.HttpFlowProtocol;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import java.util.Map;

@Getter
@Setter
public class HttpFlowElement {

    private String method;
    private String url;
    private HttpFlowProtocol protocol;
    private Map<String, HttpFlowHeaderValues> headers = new CaseInsensitiveMap<>();
    private HttpFlowBody body;

    public void setProtocol(String protocolStr) {
        if (protocolStr == null) {
            this.protocol = HttpFlowProtocol.HTTP;
        } else {
            this.protocol = HttpFlowProtocol.valueOf(protocolStr.toUpperCase());
        }
    }

    public void addHeader(String name, String value) {
        HttpFlowHeaderValues values = headers.get(name);
        if (values == null) {
            values = new HttpFlowHeaderValues();
            headers.put(name, values);
        }

        values.add(value.trim());
    }

    public HttpFlowHeaderValues getHeader(String name) {
        HttpFlowHeaderValues values = headers.get(name);
        if (values == null) {
            values = new HttpFlowHeaderValues();
            headers.put(name, values);
        }
        return values;
    }

}
