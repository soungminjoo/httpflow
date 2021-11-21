package com.github.httpflowlabs.httpflow.core.controller;

public class HttpFlowXHeaderException extends RuntimeException {

    public HttpFlowXHeaderException(Throwable cause) {
        super(cause);
    }

    public HttpFlowXHeaderException(String message) {
        super(message);
    }

}
