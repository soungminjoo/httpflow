package com.github.httpflowlabs.httpflow.resource.parser.postprocess;

import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.resource.parser.preprocess.HttpFlowLine;

import java.util.Arrays;
import java.util.List;

public interface PostProcessor {

    static List<PostProcessor> getProcessors() {
        return Arrays.asList(new HostHeaderPostProcessor());
    }

    void process(HttpFlowElement element, HttpFlowLine currLine);

}
