package com.github.httpflowlabs.httpflow.resource.parser.model.methods;

import com.github.httpflowlabs.httpflow.resource.parser.enums.HttpFlowProtocol;
import com.github.httpflowlabs.httpflow.resource.parser.exception.HttpFlowException;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowConstants;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.resource.parser.preprocess.HttpFlowLine;
import com.github.httpflowlabs.httpflow.resource.parser.preprocess.HttpFlowProtocolValidator;
import lombok.Getter;

@Getter
public class HttpFlowSetting {

    public static final String METHOD_SETTING = "SETTING";

    private boolean cookieStoreEnabled = true;
    private boolean cookieStoreFirst = false;
    private boolean csrfMetaEnabled = true;

    public HttpFlowSetting(HttpFlowElement element) {
        if (element.getProtocol() == HttpFlowProtocol.HTTPFLOW && element.getMethod().equals(METHOD_SETTING)) {
            this.cookieStoreEnabled = element.getHeader(HttpFlowConstants.COOKIE_STORE_ENABLED).lastValueAsBoolean(this.cookieStoreEnabled);
            this.cookieStoreFirst = element.getHeader(HttpFlowConstants.COOKIE_STORE_FIRST).lastValueAsBoolean(this.cookieStoreFirst);
            this.csrfMetaEnabled = element.getHeader(HttpFlowConstants.CSRF_META_ENABLED).lastValueAsBoolean(this.csrfMetaEnabled);

        } else {
            throw new HttpFlowException("Invalid element - Protocol must be " + HttpFlowProtocol.HTTPFLOW.name() + " and method must be " + METHOD_SETTING + ".");
        }
    }

    public static void validate(HttpFlowLine currLine) {
        if (currLine.getHeaderName().equalsIgnoreCase(HttpFlowConstants.COOKIE_STORE_ENABLED) || currLine.getHeaderName().equalsIgnoreCase(HttpFlowConstants.CSRF_META_ENABLED)) {
            HttpFlowProtocolValidator.assertBooleanValue(currLine.getHeaderName(), currLine.getHeaderValue(), currLine.getNumber());
        }
    }
}
