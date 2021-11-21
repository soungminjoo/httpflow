package com.github.httpflowlabs.httpflow.core.response.handler;

import com.github.httpflowlabs.httpflow.core.AbstractHttpFlowConfig;
import com.github.httpflowlabs.httpflow.core.context.HttpFlowContext;
import com.github.httpflowlabs.httpflow.core.response.handler.jsoup.JsoupHtmlHandler;
import com.github.httpflowlabs.httpflow.core.response.handler.jsoup.JsoupHtmlHandlerFactory;
import com.github.httpflowlabs.httpflow.core.response.listener.HttpFlowResponseListener;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowConstants;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowHeaderValues;
import com.github.httpflowlabs.httpflow.support.HttpFlowUtils;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRequest;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateResponse;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpFlowResponseHandler extends ResponseLineParserSupport {

    @Setter
    private HttpFlowResponseListener responseListener;

    private JsoupHtmlHandlerFactory jsoupHtmlHandlerFactory = new JsoupHtmlHandlerFactory();
    private List<JsoupHtmlHandler> globalJsoupHtmlHandlerList = new ArrayList<>();

    public void handleResponse(AbstractHttpFlowConfig httpFlow, HttpFlowElement element, HttpFlowRequest request, HttpTemplateResponse<String> response) {
        HttpFlowContext context = httpFlow.getContext();

        if (responseListener != null) {
            responseListener.beforeResponseProcess(context, element, request, response);
        }

        if (!context.isStopped() && response.getBody() != null) {
            if (HttpFlowUtils.indexOfNullSafe(response.getHeader("Content-Type"), "json") != -1) {
                Map bodyMap = HttpFlowUtils.readValueAsMap(response.getBody());
                context.getVelocityContext().putAll(bodyMap);

            } else {
                Document document = null;
                if (element.getHeader(HttpFlowConstants.HTTP_FLOW_BODY_MATCH_JSOUP) != null) {
                    HttpFlowHeaderValues matchJsoupHeaders = element.getHeader(HttpFlowConstants.HTTP_FLOW_BODY_MATCH_JSOUP);
                    document = Jsoup.parse(response.getBody());

                    for (String jsoupMatch : matchJsoupHeaders.asList()) {
                        JsoupHtmlHandler jsoupHtmlHandler = jsoupHtmlHandlerFactory.getJsoupHtmlHandler(jsoupMatch);
                        if (jsoupHtmlHandler != null) {
                            jsoupHtmlHandler.handle(context, element, request, document, jsoupMatch);
                        }
                    }

                }
                if (globalJsoupHtmlHandlerList.size() > 0) {
                    if (document == null) {
                        document = Jsoup.parse(response.getBody());
                    }
                    for (JsoupHtmlHandler jsoupHtmlHandler : globalJsoupHtmlHandlerList) {
                        jsoupHtmlHandler.handle(context, element, request, document, "");
                    }
                }

            }
            super.handleResponse(httpFlow, element, request, response);
        }

        if (responseListener != null) {
            responseListener.afterResponseProcess(context, element, request, response);
        }
    }

    public void addJsoupHtmlHandler(JsoupHtmlHandler jsoupHtmlHandler) {
        if (jsoupHtmlHandler.isXHttpFlowHeaderRequired()) {
            jsoupHtmlHandlerFactory.registerHandler(jsoupHtmlHandler);
        } else {
            globalJsoupHtmlHandlerList.add(jsoupHtmlHandler);
        }
    }
}
