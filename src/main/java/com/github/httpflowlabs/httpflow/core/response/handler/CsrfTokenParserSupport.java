package com.github.httpflowlabs.httpflow.core.response.handler;

import com.github.httpflowlabs.httpflow.core.AbstractHttpFlowConfig;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRequest;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateResponse;

public abstract class CsrfTokenParserSupport {

    public static final String X_CSRF_TOKEN = "X-CSRF-TOKEN";

    protected void handleResponse(AbstractHttpFlowConfig httpFlow, HttpFlowElement element, HttpFlowRequest request, HttpTemplateResponse<String> response) {
        String[] bodyLines = response.getBody().split("\n");

        httpFlow.getContext().setCsrfToken(null);
        for (int i = 0; i < bodyLines.length; i++) {
            String line = bodyLines[i].trim();

            if (line.matches(".*<meta name=\"_csrf\".*") && line.indexOf("content=\"") != -1) {
                line = line.substring(line.indexOf("content="));
                line = line.substring(line.indexOf("\"") + 1);
                line = line.substring(0, line.indexOf("\""));
                httpFlow.getContext().setCsrfToken(line);
                break;
            }
        }
    }

}
