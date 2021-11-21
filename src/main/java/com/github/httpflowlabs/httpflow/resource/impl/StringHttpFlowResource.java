package com.github.httpflowlabs.httpflow.resource.impl;

import com.github.httpflowlabs.httpflow.resource.AbstractHttpFlowResource;

public class StringHttpFlowResource extends AbstractHttpFlowResource {

    public StringHttpFlowResource(String httpFlowStr) {
        super(httpFlowStr);
    }

    public static StringHttpFlowResource of(String httpFlowStr) {
        return new StringHttpFlowResource(httpFlowStr);
    }

}
