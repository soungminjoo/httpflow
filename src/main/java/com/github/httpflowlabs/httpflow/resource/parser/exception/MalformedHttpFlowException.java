package com.github.httpflowlabs.httpflow.resource.parser.exception;

public class MalformedHttpFlowException extends RuntimeException {

    public MalformedHttpFlowException(String msg) {
        super(msg);
    }

    public MalformedHttpFlowException(String msg, int lineNumber) {
        super(msg + "(Line:" + lineNumber + ")");
    }

}
