package com.github.httpflowlabs.httpflow.support.httptemplate.mock;

import com.github.httpflowlabs.httpflow.support.httptemplate.HttpTemplate;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;

import java.io.IOException;

public class MockHttpTemplate extends HttpTemplate {

    private ClassicHttpRequest httpRequest;
    private HttpEntity mockResponseEntity = new StringEntity("");

    public MockHttpTemplate() {
    }

    public MockHttpTemplate(HttpEntity mockResponseEntity) {
        this.mockResponseEntity = mockResponseEntity;
    }

    @Override
    protected ClassicHttpResponse execute(CloseableHttpClient httpclient, ClassicHttpRequest httpRequest) throws IOException {
        this.httpRequest = httpRequest;

        BasicClassicHttpResponse response = new BasicClassicHttpResponse(200);
        response.setEntity(mockResponseEntity);
        return response;
    }

    public ClassicHttpRequest getHttpRequest() {
        return httpRequest;
    }

}
