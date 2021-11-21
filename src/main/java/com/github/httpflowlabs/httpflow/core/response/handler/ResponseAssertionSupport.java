package com.github.httpflowlabs.httpflow.core.response.handler;

import com.github.httpflowlabs.httpflow.core.AbstractHttpFlowConfig;
import com.github.httpflowlabs.httpflow.core.context.exception.WrappedAssertionFailedError;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowConstants;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRequest;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateResponse;
import org.apache.hc.core5.http.Header;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public abstract class ResponseAssertionSupport extends CsrfTokenParserSupport {

    protected void handleResponse(AbstractHttpFlowConfig httpFlow, HttpFlowElement element, HttpFlowRequest request, HttpTemplateResponse<String> response) {
        super.handleResponse(httpFlow, element, request, response);

        for (String headerName : element.getHeaders().keySet()) {
            if (headerName.equalsIgnoreCase(HttpFlowConstants.HTTP_FLOW_ASSERT_STATUS)) {
                assertStatus(element, response, headerName);
            }
            if (headerName.equalsIgnoreCase(HttpFlowConstants.HTTP_FLOW_ASSERT_HEADER)) {
                assertHeader(element, response, headerName);
            }
        }
    }

    private void assertStatus(HttpFlowElement element, HttpTemplateResponse<String> response, String headerName) {
        for (String status : element.getHeaders().get(headerName).asList()) {
            try {
                Assertions.assertEquals(Integer.parseInt(status.trim()), response.getCode());
            } catch (AssertionFailedError error) {
                throw new WrappedAssertionFailedError(error, headerName + " : " + status);
            }
        }
    }

    private void assertHeader(HttpFlowElement element, HttpTemplateResponse<String> response, String headerName) {
        for (String assertRule : element.getHeaders().get(headerName).asList()) {
            String targetHeader = assertRule.substring(0, assertRule.indexOf(":")).trim();
            String rule = assertRule.substring(assertRule.indexOf(":") + 1).trim();

            String assertType = null;
            String expected = null;
            if (rule.indexOf(" ") != -1) {
                assertType = rule.substring(0, rule.indexOf(" ")).trim();
                expected = rule.substring(rule.indexOf(" ") + 1);
            } else {
                assertType = rule;
            }

            for (Header respHeader : response.getHeaders()) {
                if (respHeader.getName().equalsIgnoreCase(targetHeader)) {
                    assertByReflection(assertRule, assertType, expected, respHeader.getValue());
                }
            }
        }
    }

    private void assertByReflection(String assertRule, String assertType, String expected, String value) {
        for (Method m : Assertions.class.getDeclaredMethods()) {
            if (m.getName().equalsIgnoreCase(assertType)) {
                try {
                    if (m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(Object.class)) {
                        m.invoke(null, value);
                    } else if (m.getParameterCount() == 2 && m.getParameterTypes()[0].equals(Object.class)&& m.getParameterTypes()[1].equals(Object.class)) {
                        m.invoke(null, expected, value);
                    }
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof AssertionFailedError) {
                        throw new WrappedAssertionFailedError(e.getCause(), assertRule);
                    }
                    throw new IllegalStateException(e.getCause());
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

}
