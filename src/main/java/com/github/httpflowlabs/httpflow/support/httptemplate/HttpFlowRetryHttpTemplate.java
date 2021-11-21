package com.github.httpflowlabs.httpflow.support.httptemplate;

import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateResponse;
import org.apache.hc.client5.http.cookie.CookieStore;

import javax.net.ssl.SSLException;
import java.util.function.Function;

public class HttpFlowRetryHttpTemplate {

    public HttpTemplateResponse<String> exchange(HttpFlowRequest element, CookieStore cookieStore) {
        String method = element.getMethod();
        HttpFlowHttpTemplateEntity request = new HttpFlowHttpTemplateEntity(element);

        HttpTemplate httpTemplate = getHttpTemplate();
        // check global setting cookie enabled
        httpTemplate.setCookieStore(cookieStore);
        request.removeCookieHeader();
        // else use cookie value in .hfd file

        return exchangeWithRetryOption(element.getUrl(), url ->
                httpTemplate.exchange(url, method, request, String.class)
        );
    }

    protected HttpTemplate getHttpTemplate() {
        return new HttpTemplate();
    }

    private HttpTemplateResponse<String> exchangeWithRetryOption(String url, Function<String, HttpTemplateResponse<String>> command) {
        try {
            return command.apply(url);

        } catch (RuntimeException e) {
            if (e.getCause() instanceof SSLException && url.startsWith("https://")) {
                url = url.replaceFirst("https://", "http://");
                return command.apply(url);
            }
            throw e;
        }
    }

}
