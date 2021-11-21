package com.github.httpflowlabs.httpflow.core;

import com.github.httpflowlabs.httpflow.core.context.DefaultHttpFlowContext;
import com.github.httpflowlabs.httpflow.core.context.HttpFlowContext;
import com.github.httpflowlabs.httpflow.core.request.cookie.DefaultRequestCookieResolver;
import com.github.httpflowlabs.httpflow.core.request.cookie.RequestCookieResolver;
import com.github.httpflowlabs.httpflow.core.response.handler.HttpFlowResponseHandler;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractHttpFlowConfig implements HttpFlowConfigurable {

    protected HttpFlowContext context = new DefaultHttpFlowContext();
    protected RequestCookieResolver requestCookieResolver = new DefaultRequestCookieResolver();
    protected HttpFlowResponseHandler responseHandler = new HttpFlowResponseHandler();
    protected int loopCount = 1;

}
