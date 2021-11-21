package com.github.httpflowlabs.httpflow.resource;

import com.github.httpflowlabs.httpflow.resource.parser.HttpFlowParser;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowDocument;
import lombok.Getter;

@Getter
public abstract class AbstractHttpFlowResource implements HttpFlowResource {

    private HttpFlowDocument document;

    public AbstractHttpFlowResource(String httpFlowStr) {
        this.document = new HttpFlowParser().parse(httpFlowStr);
    }

}
