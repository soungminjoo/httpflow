package com.github.httpflowlabs.httpflow.resource.impl;

import com.github.httpflowlabs.httpflow.resource.AbstractHttpFlowResource;
import com.github.httpflowlabs.httpflow.support.HttpFlowUtils;

import java.io.InputStream;

public class InputStreamHttpFlowResource extends AbstractHttpFlowResource {

    public InputStreamHttpFlowResource(InputStream httpFlowStream) {
        super(HttpFlowUtils.readInputStream(httpFlowStream).trim());
    }

    public static InputStreamHttpFlowResource of(InputStream httpFlowStream) {
        return new InputStreamHttpFlowResource(httpFlowStream);
    }

}
