package com.github.httpflowlabs.httpflow.resource.impl;

import com.github.httpflowlabs.httpflow.resource.AbstractHttpFlowResource;
import com.github.httpflowlabs.httpflow.support.HttpFlowUtils;

import java.io.File;

public class FileHttpFlowResource extends AbstractHttpFlowResource {

    public FileHttpFlowResource(File httpFlowFile) {
        super(HttpFlowUtils.readFile((httpFlowFile)));
    }

    public static FileHttpFlowResource of(File httpFlowFile) {
        return new FileHttpFlowResource(httpFlowFile);
    }

}
