package com.github.httpflowlabs.httpflow.core.response.handler.jsoup;

import com.github.httpflowlabs.httpflow.core.context.HttpFlowContext;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRequest;
import org.jsoup.nodes.Document;

public interface JsoupHtmlHandler {

    String getHandlerKey();

    void handle(HttpFlowContext context, HttpFlowElement element, HttpFlowRequest request, Document document, String jsoupMatch);

    boolean isXHttpFlowHeaderRequired();

}
