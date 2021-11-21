package com.github.httpflowlabs.httpflow;

import com.github.httpflowlabs.httpflow.core.context.HttpFlowContext;
import com.github.httpflowlabs.httpflow.core.response.handler.jsoup.NotEmptyInputValueJsoupHandler;
import com.github.httpflowlabs.httpflow.core.response.listener.HttpFlowResponseListener;
import com.github.httpflowlabs.httpflow.core.response.listener.HttpFlowResponseListenerAdaptor;
import com.github.httpflowlabs.httpflow.resource.impl.FileHttpFlowResource;
import com.github.httpflowlabs.httpflow.resource.impl.InputStreamHttpFlowResource;
import com.github.httpflowlabs.httpflow.resource.impl.StringHttpFlowResource;
import com.github.httpflowlabs.httpflow.resource.parser.model.HttpFlowElement;
import com.github.httpflowlabs.httpflow.support.httptemplate.HttpFlowRequest;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateResponse;
import com.github.httpflowlabs.httpflow.support.TempFileTestcaseSupport;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpFlowTest {

    @Test
    public void testBasicUsage() {
        HttpFlow httpFlow = new HttpFlow();

        // Copy this string from chrome browser's developer tools (Network > Headers > Request Headers)
        StringHttpFlowResource resource = StringHttpFlowResource.of("" +
                ":authority: github.com\n" +
                ":method: GET\n" +
                ":path: /\n" +
                ":scheme: https\n" +
                "accept: text/html,application/xhtml+xml,application/xml\n" +
                "accept-encoding: gzip, deflate, br");

        Assertions.assertEquals(0, httpFlow.getContext().getCookieStore().getCookies().size());

        httpFlow.execute(resource);
        Assertions.assertNotEquals(0, httpFlow.getContext().getCookieStore().getCookies().size());
    }

    @Test
    public void testBasicUsageFile() {
        String httpFlowStr = ":authority: github.com\n" +
                ":method: GET\n" +
                ":path: /\n" +
                ":scheme: https\n" +
                "accept: text/html,application/xhtml+xml,application/xml\n" +
                "accept-encoding: gzip, deflate, br";

        new TempFileTestcaseSupport(httpFlowStr) {
            public void onFileCreated(File file) {

                HttpFlow httpFlow = new HttpFlow();
                Assertions.assertEquals(0, httpFlow.getContext().getCookieStore().getCookies().size());

                httpFlow.execute(new FileHttpFlowResource(file));
                Assertions.assertNotEquals(0, httpFlow.getContext().getCookieStore().getCookies().size());
            }
        };
    }

    @Test
    public void testBasicUsageInputStream() {
        String httpFlowStr = ":authority: github.com\n" +
                ":method: GET\n" +
                ":path: /\n" +
                ":scheme: https\n" +
                "accept: text/html,application/xhtml+xml,application/xml\n" +
                "accept-encoding: gzip, deflate, br";

        InputStream is = new ByteArrayInputStream(httpFlowStr.getBytes());

        HttpFlow httpFlow = new HttpFlow();
        Assertions.assertEquals(0, httpFlow.getContext().getCookieStore().getCookies().size());

        httpFlow.execute(new InputStreamHttpFlowResource(is));
        Assertions.assertNotEquals(0, httpFlow.getContext().getCookieStore().getCookies().size());
    }

    @Test
    public void testListener() {
        final Map<String, Boolean> flagMap = new HashMap<>();

        HttpFlow httpFlow = new HttpFlow();
        httpFlow.setResponseListener(new HttpFlowResponseListener() {
            public void beforeResponseProcess(HttpFlowContext context, HttpFlowElement element, HttpFlowRequest request, HttpTemplateResponse<String> response) {
                flagMap.put("Before", true);
            }
            public void afterResponseProcess(HttpFlowContext context, HttpFlowElement element, HttpFlowRequest request, HttpTemplateResponse<String> response) {
                flagMap.put("After", true);
            }
        });

        httpFlow.execute(StringHttpFlowResource.of("" +
                ":authority: github.com\n" +
                ":method: GET\n" +
                ":path: /\n" +
                ":scheme: https\n" +
                "accept: text/html,application/xhtml+xml,application/xml\n" +
                "accept-encoding: gzip, deflate, br"));

        Assertions.assertTrue(flagMap.get("Before"));
        Assertions.assertTrue(flagMap.get("After"));
    }

    @Test
    public void testListenerAdaptor() {
        final Map<String, Boolean> flagMap = new HashMap<>();

        HttpFlow httpFlow = new HttpFlow();
        httpFlow.setResponseListener(new HttpFlowResponseListenerAdaptor() {
            public void afterResponseProcess(HttpFlowContext context, HttpFlowElement element, HttpFlowRequest request, HttpTemplateResponse<String> response) {
                flagMap.put("After", true);
            }
        });

        httpFlow.execute(StringHttpFlowResource.of("" +
                ":authority: github.com\n" +
                ":method: GET\n" +
                ":path: /\n" +
                ":scheme: https\n" +
                "accept: text/html,application/xhtml+xml,application/xml\n" +
                "accept-encoding: gzip, deflate, br"));

        Assertions.assertNull(flagMap.get("Before"));
        Assertions.assertTrue(flagMap.get("After"));
    }

    @Test
    public void testListenerAdaptorClassExtends() {
        class TestResponseListener extends HttpFlowResponseListenerAdaptor {
            private Map<String, Boolean> flagMap = new HashMap<>();
            public Map<String, Boolean> getFlagMap() {
                return flagMap;
            }

            public void afterResponseProcess(HttpFlowContext context, HttpFlowElement element, HttpFlowRequest request, HttpTemplateResponse<String> response) {
                flagMap.put("After", true);
            }
        }

        HttpFlow httpFlow = new HttpFlow();
        TestResponseListener responseListener = new TestResponseListener();
        httpFlow.setResponseListener(responseListener);

        httpFlow.execute(StringHttpFlowResource.of("" +
                ":authority: github.com\n" +
                ":method: GET\n" +
                ":path: /\n" +
                ":scheme: https\n" +
                "accept: text/html,application/xhtml+xml,application/xml\n" +
                "accept-encoding: gzip, deflate, br"));

        Assertions.assertNull(responseListener.getFlagMap().get("Before"));
        Assertions.assertTrue(responseListener.getFlagMap().get("After"));
    }

    @Test
    public void testAssertStatus() {
        HttpFlow httpFlow = new HttpFlow();
        String wrongPath = "not-existing-path";

        // X-HTTPFLOW-ASSERT-STATUS: 200
        // Check response status code(200, 404, 500...) and throws Error if it doesn't match.

        Assertions.assertThrows(AssertionFailedError.class, () -> {
            httpFlow.execute(StringHttpFlowResource.of("" +
                    ":authority: www.google.com\n" +
                    ":method: GET\n" +
                    ":path: /" + wrongPath + "\n" +
                    ":scheme: https\n" +
                    "X-HTTPFLOW-ASSERT-STATUS: 200"));
        });
    }

    @Test
    public void testAssertHeader() {
        HttpFlow httpFlow = new HttpFlow();

        // X-HTTPFLOW-ASSERT-HEADER: {Header name}: {assertXXX method} {expected value}
        // X-HTTPFLOW-ASSERT-HEADER: Content-Type: assertEquals text/html; charset=UTF-8
        // X-HTTPFLOW-ASSERT-HEADER: Content-Type: assertNotNull
        // Check response header using assertXXX method. assertXXX method can be one of JUnit5 Assertions.assertXXX methods.
        // {expected value} is optional according to assertXXX method.

        httpFlow.execute(StringHttpFlowResource.of("" +
                ":authority: www.google.com\n" +
                ":method: GET\n" +
                ":path: /\n" +
                ":scheme: https\n" +
                "X-HTTPFLOW-ASSERT-HEADER: Content-Type: assertNotNull"));

        Assertions.assertThrows(AssertionFailedError.class, () -> {
            httpFlow.execute(StringHttpFlowResource.of("" +
                    ":authority: www.google.com\n" +
                    ":method: GET\n" +
                    ":path: /\n" +
                    ":scheme: https\n" +
                    "X-HTTPFLOW-ASSERT-HEADER: Content-Type: assertNull"));
        });

        httpFlow.execute(StringHttpFlowResource.of("" +
                ":authority: www.google.com\n" +
                ":method: GET\n" +
                ":path: /\n" +
                ":scheme: https\n" +
                "X-HTTPFLOW-ASSERT-HEADER: Content-Type: assertEquals text/html; charset=ISO-8859-1"));

        Assertions.assertThrows(AssertionFailedError.class, () -> {
            httpFlow.execute(StringHttpFlowResource.of("" +
                    ":authority: www.google.com\n" +
                    ":method: GET\n" +
                    ":path: /\n" +
                    ":scheme: https\n" +
                    "X-HTTPFLOW-ASSERT-HEADER: Content-Type: assertEquals application/json"));
        });
    }

    @Test
    public void testMultipleExecute() {
        HttpFlow httpFlow = new HttpFlow();
        Assertions.assertEquals(0, httpFlow.getContext().getVelocityContext().getKeys().length);

        // X-HTTPFLOW-MATCH-BODY-REGEX: {name_to_save} USING {regex with only one group}
        // Only one regex group means expression must have only one '(' and only one ')' that is indicating value string to find
        String extensionHeaderResponseBodyParse = "X-HTTPFLOW-MATCH-BODY-REGEX: authenticity_token USING .*name=\"authenticity_token\".*value=\"(.+?)\".*";
        String extensionHeaderResponseBodyParse2 = "X-HTTPFLOW-MATCH-BODY-REGEX: timestamp USING .*name=\"timestamp\".*value=\"(.+?)\".*";
        String extensionHeaderResponseBodyParse3 = "X-HTTPFLOW-MATCH-BODY-REGEX: timestamp_secret USING .*name=\"timestamp_secret\".*value=\"(.+?)\".*";

        httpFlow.execute(StringHttpFlowResource.of("" +
                ":authority: raw.githubusercontent.com\n" +
                ":method: GET\n" +
                ":path: /httpflowlabs/mock-login/main/login-page\n" +
                ":scheme: https\n" +
                "accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
                "accept-encoding: gzip, deflate, br\n" +
                "accept-language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\n" +
                "referer: https://github.com/httpflowlabs/mock-login/blob/main/login-page\n" +
                "sec-ch-ua: \"Chromium\";v=\"94\", \"Google Chrome\";v=\"94\", \";Not A Brand\";v=\"99\"\n" +
                "sec-ch-ua-mobile: ?0\n" +
                "sec-ch-ua-platform: \"macOS\"\n" +
                "sec-fetch-dest: document\n" +
                "sec-fetch-mode: navigate\n" +
                "sec-fetch-site: cross-site\n" +
                "sec-fetch-user: ?1\n" +
                "upgrade-insecure-requests: 1\n" +
                "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36\n" +
                extensionHeaderResponseBodyParse + "\n" +
                extensionHeaderResponseBodyParse2 + "\n" +
                extensionHeaderResponseBodyParse3));

        Assertions.assertEquals(3, httpFlow.getContext().getVelocityContext().getKeys().length);
        Assertions.assertNotNull(httpFlow.getContext().getVelocityContext().get("authenticity_token"));
        Assertions.assertNotNull(httpFlow.getContext().getVelocityContext().get("timestamp"));
        Assertions.assertNotNull(httpFlow.getContext().getVelocityContext().get("timestamp_secret"));

        httpFlow.execute(StringHttpFlowResource.of("" +
                ":authority: raw.githubusercontent.com\n" +
                ":method: GET\n" +
                ":path: /httpflowlabs/mock-login/main/session.do?id=httpflowlabs&password=********&authenticity_token=${authenticity_token}&timestamp=${timestamp}&timestamp_secret=${timestamp_secret}\n" +
                ":scheme: https\n" +
                "accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\n" +
                "accept-encoding: gzip, deflate, br\n" +
                "accept-language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7\n" +
                "cache-control: max-age=0\n" +
                "sec-ch-ua: \"Chromium\";v=\"94\", \"Google Chrome\";v=\"94\", \";Not A Brand\";v=\"99\"\n" +
                "sec-ch-ua-mobile: ?0\n" +
                "sec-ch-ua-platform: \"macOS\"\n" +
                "sec-fetch-dest: document\n" +
                "sec-fetch-mode: navigate\n" +
                "sec-fetch-site: none\n" +
                "sec-fetch-user: ?1\n" +
                "upgrade-insecure-requests: 1\n" +
                "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36"));
    }

    @Test
    public void testMultipleExecuteAsSingleFile() {
        // More than one execution can be saved in a single file.
        // Each execution can be delimited by a line staring '--' which is originally comment line.
        String httpFlowStr = "" +
                ":authority: raw.githubusercontent.com\n" +
                ":method: GET\n" +
                ":path: /httpflowlabs/mock-login/main/login-page\n" +
                ":scheme: https\n" +
                "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36\n" +
                "X-HTTPFLOW-MATCH-BODY-REGEX: authenticity_token USING .*name=\"authenticity_token\".*value=\"(.+?)\".*" + "\n" +
                "X-HTTPFLOW-MATCH-BODY-REGEX: timestamp USING .*name=\"timestamp\".*value=\"(.+?)\".*" + "\n" +
                "X-HTTPFLOW-MATCH-BODY-REGEX: timestamp_secret USING .*name=\"timestamp_secret\".*value=\"(.+?)\".*" + "\n" +
                "\n" +
                "---\n" + // Keyword --- is delimiter of each execution
                "\n" +
                ":authority: raw.githubusercontent.com\n" +
                ":method: GET\n" +
                ":path: /httpflowlabs/mock-login/main/session.do?id=httpflowlabs&password=********&authenticity_token=${authenticity_token}&timestamp=${timestamp}&timestamp_secret=${timestamp_secret}\n" +
                ":scheme: https\n" +
                "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36";

        new TempFileTestcaseSupport(httpFlowStr) {
            public void onFileCreated(File file) {
                HttpFlow httpFlow = new HttpFlow();
                httpFlow.execute(FileHttpFlowResource.of(file));

                Assertions.assertNotNull(httpFlow.getContext().getVelocityContext().get("authenticity_token"));
                Assertions.assertNotNull(httpFlow.getContext().getVelocityContext().get("timestamp"));
                Assertions.assertNotNull(httpFlow.getContext().getVelocityContext().get("timestamp_secret"));
            }
        };
    }

    @Test
    public void testJsoupHtmlHandler() {
        // Instead of setting X-HTTPFLOW-MATCH-BODY-REGEX header,
        // you can use JsoupHtmlHandler
        String httpFlowStr = "" +
                ":authority: raw.githubusercontent.com\n" +
                ":method: GET\n" +
                ":path: /httpflowlabs/mock-login/main/login-page\n" +
                ":scheme: https\n" +
                "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36\n" +
                // "X-HTTPFLOW-MATCH-BODY-REGEX: authenticity_token USING .*name=\"authenticity_token\".*value=\"(.+?)\".*" + "\n" +
                // "X-HTTPFLOW-MATCH-BODY-REGEX: timestamp USING .*name=\"timestamp\".*value=\"(.+?)\".*" + "\n" +
                // "X-HTTPFLOW-MATCH-BODY-REGEX: timestamp_secret USING .*name=\"timestamp_secret\".*value=\"(.+?)\".*" + "\n" +
                "\n" +
                "---\n" +
                "\n" +
                ":authority: raw.githubusercontent.com\n" +
                ":method: GET\n" +
                ":path: /httpflowlabs/mock-login/main/session.do?id=httpflowlabs&password=********&authenticity_token=${authenticity_token}&timestamp=${timestamp}&timestamp_secret=${timestamp_secret}\n" +
                ":scheme: https\n" +
                "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36";

        HttpFlow httpFlow = new HttpFlow();

        // Set NotEmptyInputValueJsoupHandler which collects <input> tags having not empty value and put the values to velocity context
        httpFlow.addJsoupHtmlHandler(new NotEmptyInputValueJsoupHandler());
        httpFlow.execute(StringHttpFlowResource.of(httpFlowStr));

        Assertions.assertNotNull(httpFlow.getContext().getVelocityContext().get("authenticity_token"));
        Assertions.assertNotNull(httpFlow.getContext().getVelocityContext().get("timestamp"));
        Assertions.assertNotNull(httpFlow.getContext().getVelocityContext().get("timestamp_secret"));
    }

    @Test
    public void testNotEmptyInputValueJsoupHtmlHandler() {
        // Instead of adding new NotEmptyInputValueJsoupHandler() in java code,
        // you can set below header for the same functionality
        // X-HTTPFLOW-MATCH-BODY-JSOUP: NOT_EMPTY_INPUT_VALUE
        String httpFlowStr = "" +
                ":authority: raw.githubusercontent.com\n" +
                ":method: GET\n" +
                ":path: /httpflowlabs/mock-login/main/login-page\n" +
                ":scheme: https\n" +
                "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36\n" +
                // HERE
                "X-HTTPFLOW-MATCH-BODY-JSOUP: NOT_EMPTY_INPUT_VALUE" + "\n" +
                "\n" +
                "---\n" +
                "\n" +
                ":authority: raw.githubusercontent.com\n" +
                ":method: GET\n" +
                ":path: /httpflowlabs/mock-login/main/session.do?id=httpflowlabs&password=********&authenticity_token=${authenticity_token}&timestamp=${timestamp}&timestamp_secret=${timestamp_secret}\n" +
                ":scheme: https\n" +
                "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36";

        HttpFlow httpFlow = new HttpFlow();

        // httpFlow.addJsoupHtmlHandler(new NotEmptyInputValueJsoupHandler());
        httpFlow.execute(StringHttpFlowResource.of(httpFlowStr));

        Assertions.assertNotNull(httpFlow.getContext().getVelocityContext().get("authenticity_token"));
        Assertions.assertNotNull(httpFlow.getContext().getVelocityContext().get("timestamp"));
        Assertions.assertNotNull(httpFlow.getContext().getVelocityContext().get("timestamp_secret"));
    }

    @Test
    public void testJsoupHtmlHandlerWithXPath() {
        // you can parse and get specific text in html using XPath expression through below header
        // X-HTTPFLOW-MATCH-BODY-JSOUP: SELECT text() | @attrName AS {variable name} XPATH {XPath syntax expression}
        // ex#1 X-HTTPFLOW-MATCH-BODY-JSOUP: SELECT @value AS timestamp XPATH //input[@name="timestamp"]
        // ex#2 X-HTTPFLOW-MATCH-BODY-JSOUP: SELECT text() AS title XPATH //span[@id="title"]
        String httpFlowStr = "" +
                ":authority: raw.githubusercontent.com\n" +
                ":method: GET\n" +
                ":path: /httpflowlabs/mock-login/main/login-page\n" +
                ":scheme: https\n" +
                "user-agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36\n" +
                "X-HTTPFLOW-MATCH-BODY-JSOUP: SELECT @value AS timestamp XPATH //input[@name=\"timestamp\"]";

        HttpFlow httpFlow = new HttpFlow();
        Assertions.assertNull(httpFlow.getContext().getVelocityContext().get("timestamp"));

        httpFlow.execute(StringHttpFlowResource.of(httpFlowStr));
        Assertions.assertNotNull(httpFlow.getContext().getVelocityContext().get("timestamp"));
    }

    @Test
    public void testForControl() {
        List<Integer> respCodeList = new ArrayList<>();

        HttpFlow httpFlow = new HttpFlow();
        httpFlow.setResponseListener(new HttpFlowResponseListenerAdaptor(){
            @Override
            public void afterResponseProcess(HttpFlowContext context, HttpFlowElement element, HttpFlowRequest request, HttpTemplateResponse<String> response) {
                respCodeList.add(response.getCode());
            }
        });

        httpFlow.execute(StringHttpFlowResource.of("" +
                "FOR 3 HTTPFLOW/1.0\n" +
                "---\n" +
                ":authority: www.google.com\n" +
                ":method: GET\n" +
                ":path: /\n" +
                ":scheme: https\n" +
                "\n" +
                "---\n" +
                "\n" +
                ":authority: www.google.com\n" +
                ":method: GET\n" +
                ":path: /\n" +
                ":scheme: https\n" +
                "---\n" +
                "END-FOR"));

        Assertions.assertEquals(6, respCodeList.size());
    }

    @Test
    public void testNestedRepeatStartEnd() {
        List<Integer> respCodeList = new ArrayList<>();

        HttpFlow httpFlow = new HttpFlow();
        httpFlow.setResponseListener(new HttpFlowResponseListenerAdaptor(){
            @Override
            public void afterResponseProcess(HttpFlowContext context, HttpFlowElement element, HttpFlowRequest request, HttpTemplateResponse<String> response) {
                respCodeList.add(response.getCode());
            }
        });

        httpFlow.execute(StringHttpFlowResource.of("" +
                "FOR 2 HTTPFLOW/1.0\n" +
                "---\n" +
                ":authority: www.google.com\n" +
                ":method: GET\n" +
                ":path: /search?q=a\n" +
                ":scheme: https\n" +
                "\n" +
                "---\n" +
                "\n" +
                "FOR 2 HTTPFLOW/1.0\n" +
                "---\n" +
                ":authority: www.google.com\n" +
                ":method: GET\n" +
                ":path: /search?q=b\n" +
                ":scheme: https\n" +
                "\n" +
                "---\n" +
                "\n" +
                ":authority: www.google.com\n" +
                ":method: GET\n" +
                ":path: /search?q=c\n" +
                ":scheme: https\n" +
                "---\n" +
                "END-FOR\n" +
                "---\n" +
                "END-FOR"));

        Assertions.assertEquals(10, respCodeList.size());
    }

}
