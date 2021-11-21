package com.github.httpflowlabs.httpflow.core.response.listener;

import com.github.httpflowlabs.httpflow.core.context.HttpFlowContext;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRequest;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateResponse;

public interface HttpFlowResponseListener {

    void beforeResponseProcess(HttpFlowContext context, HttpFlowElement element, HttpFlowRequest request, HttpTemplateResponse<String> response);
    void afterResponseProcess(HttpFlowContext context, HttpFlowElement element, HttpFlowRequest request, HttpTemplateResponse<String> response);

}
