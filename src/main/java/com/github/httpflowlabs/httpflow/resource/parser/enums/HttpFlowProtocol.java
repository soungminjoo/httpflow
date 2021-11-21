package com.github.httpflowlabs.httpflow.resource.parser.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum HttpFlowProtocol {

    HTTP, HTTPFLOW;

    public static List<String> names() {
        return Arrays.stream(values()).map(p -> p.name()).collect(Collectors.toList());
    }

}
