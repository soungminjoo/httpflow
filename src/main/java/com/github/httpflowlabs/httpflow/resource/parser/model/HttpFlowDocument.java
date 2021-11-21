package com.github.httpflowlabs.httpflow.resource.parser.model;

import lombok.Getter;

import java.util.List;

@Getter
public class HttpFlowDocument {

    private List<HttpFlowElement> elements;

    public HttpFlowDocument(List<HttpFlowElement> elements) {
        this.elements = elements;
    }

}
