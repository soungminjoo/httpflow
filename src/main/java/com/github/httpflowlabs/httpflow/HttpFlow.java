package com.github.httpflowlabs.httpflow;

import com.github.httpflowlabs.httpflow.core.DefaultHttpFlowCore;
import com.github.httpflowlabs.httpflow.core.IHttpFlowCore;
import com.github.httpflowlabs.httpflow.core.context.HttpFlowContext;
import com.github.httpflowlabs.httpflow.core.response.handler.jsoup.JsoupHtmlHandler;
import com.github.httpflowlabs.httpflow.core.response.listener.HttpFlowResponseListener;
import com.github.httpflowlabs.httpflow.resource.HttpFlowResource;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowDocument;

public class HttpFlow {

    private IHttpFlowCore httpFlowCore = new DefaultHttpFlowCore();

    public HttpFlowContext getContext() {
        return httpFlowCore.getContext();
    }

    public void execute(HttpFlowResource resource) {
        httpFlowCore.execute(resource);
    }

    public void execute(HttpFlowDocument document) {
        httpFlowCore.execute(document);
    }

    public void setResponseListener(HttpFlowResponseListener httpFlowResponseListener) {
        httpFlowCore.getResponseHandler().setResponseListener(httpFlowResponseListener);
    }

    public void addJsoupHtmlHandler(JsoupHtmlHandler jsoupHtmlHandler) {
        httpFlowCore.getResponseHandler().addJsoupHtmlHandler(jsoupHtmlHandler);
    }

}
