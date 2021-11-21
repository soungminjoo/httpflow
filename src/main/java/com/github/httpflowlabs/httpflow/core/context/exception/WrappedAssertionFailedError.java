package com.github.httpflowlabs.httpflow.core.context.exception;

import lombok.Getter;
import org.opentest4j.AssertionFailedError;

@Getter
public class WrappedAssertionFailedError extends AssertionFailedError {

    private String assertionRule;

    public WrappedAssertionFailedError(Throwable cause, String assertionRule) {
        super(cause.getMessage() + " : Assertion rule was [" + assertionRule + "]", cause);
        this.assertionRule = assertionRule;
    }

}
