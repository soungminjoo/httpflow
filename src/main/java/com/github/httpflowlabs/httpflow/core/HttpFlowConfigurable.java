package com.github.httpflowlabs.httpflow.core;

import com.github.httpflowlabs.httpflow.core.context.HttpFlowContext;
import com.github.httpflowlabs.httpflow.core.request.cookie.RequestCookieResolver;
import com.github.httpflowlabs.httpflow.core.response.handler.HttpFlowResponseHandler;

public interface HttpFlowConfigurable {

    HttpFlowContext getContext();
    HttpFlowResponseHandler getResponseHandler();
    RequestCookieResolver getRequestCookieResolver();
    int getLoopCount();

    void setContext(HttpFlowContext context);
    void setRequestCookieResolver(RequestCookieResolver requestCookieResolver);
    void setResponseHandler(HttpFlowResponseHandler responseHandler);
    void setLoopCount(int loopCount);

}
