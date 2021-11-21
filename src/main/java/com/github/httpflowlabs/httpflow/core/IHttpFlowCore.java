package com.github.httpflowlabs.httpflow.core;

import com.github.httpflowlabs.httpflow.resource.HttpFlowResource;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowDocument;

public interface IHttpFlowCore extends HttpFlowConfigurable {

    void execute(HttpFlowResource resource);

    void execute(HttpFlowDocument document);

}
