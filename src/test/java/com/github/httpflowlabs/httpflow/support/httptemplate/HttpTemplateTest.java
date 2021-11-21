package com.github.httpflowlabs.httpflow.support.httptemplate;

import com.github.httpflowlabs.httpflow.support.httptemplate.mock.MockHttpTemplate;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateEntity;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateResponse;
import com.github.httpflowlabs.httpflow.support.httptemplate.model.HttpTemplateUrlEncodedFormEntity;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HttpTemplateTest {

    public static final String MOCK_TEST_URL = "http://localhost/pets";

    @Test
    public void testGet() throws ProtocolException {
        String mockRespJson = "{\"id\": 1, \"name\":\"Happy\"}";
        MockHttpTemplate httpTemplate = new MockHttpTemplate(new StringEntity(mockRespJson));

        HttpTemplateEntity tRequest = new HttpTemplateEntity();
        tRequest.addHeader("Authorization", "Bearer AUTH");

        HttpTemplateResponse<String> response = httpTemplate.exchange(MOCK_TEST_URL, "GET", tRequest, String.class);

        Assertions.assertEquals(200, response.getCode());
        Assertions.assertEquals("Bearer AUTH", httpTemplate.getHttpRequest().getHeader("Authorization").getValue());
        Assertions.assertTrue(httpTemplate.getHttpRequest() instanceof HttpGet);
        Assertions.assertNull(httpTemplate.getHttpRequest().getEntity());

        Assertions.assertEquals(mockRespJson, response.getBody());

        HttpTemplateResponse<Map> responseMap = httpTemplate.exchange(MOCK_TEST_URL, "GET", tRequest, Map.class);
        Assertions.assertEquals(1, responseMap.getBody().get("id"));
        Assertions.assertEquals("Happy", responseMap.getBody().get("name"));
    }

    @Test
    public void testPost() throws ProtocolException {
        MockHttpTemplate httpTemplate = new MockHttpTemplate();

        HttpTemplateEntity tRequest = new HttpTemplateEntity();
        tRequest.setBody("{\n" +
                "  \"name\": \"Happy\",\n" +
                "  \"age\": 2\n" +
                "}");

        httpTemplate.exchange(MOCK_TEST_URL, "POST", tRequest, String.class);
        Assertions.assertTrue(httpTemplate.getHttpRequest() instanceof HttpPost);
        Assertions.assertEquals("application/json; charset=utf-8", httpTemplate.getHttpRequest().getHeader("Content-Type").getValue());
        Assertions.assertTrue(httpTemplate.getHttpRequest().getEntity().getClass().equals(StringEntity.class));
    }

    @Test
    public void testPost2() throws ProtocolException {
        MockHttpTemplate httpTemplate = new MockHttpTemplate();

        Map requestParam = new HashMap();
        requestParam.put("name", "Happy");
        requestParam.put("age", 2);

        HttpTemplateEntity tRequest = new HttpTemplateEntity();
        tRequest.setBody(requestParam);

        httpTemplate.exchange(MOCK_TEST_URL, "POST", tRequest, String.class);
        Assertions.assertEquals("application/json; charset=utf-8", httpTemplate.getHttpRequest().getHeader("Content-Type").getValue());
        Assertions.assertTrue(httpTemplate.getHttpRequest().getEntity().getClass().equals(StringEntity.class));
    }

    @Test
    public void testPost3() throws ProtocolException {
        MockHttpTemplate httpTemplate = new MockHttpTemplate();

        Map requestParam = new HashMap();
        requestParam.put("name", "Happy");
        requestParam.put("age", 2);

        HttpTemplateEntity tRequest = new HttpTemplateEntity();
        tRequest.setBody(requestParam);
        tRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        httpTemplate.exchange(MOCK_TEST_URL, "POST", tRequest, String.class);
        Assertions.assertEquals("application/x-www-form-urlencoded; charset=UTF-8", httpTemplate.getHttpRequest().getHeader("Content-Type").getValue());
        Assertions.assertTrue(httpTemplate.getHttpRequest().getEntity().getClass().equals(HttpTemplateUrlEncodedFormEntity.class));
    }

    @Test
    public void testPost4() throws ProtocolException {
        MockHttpTemplate httpTemplate = new MockHttpTemplate();

        HttpTemplateEntity tRequest = new HttpTemplateEntity();
        tRequest.setBody(new FileEntity(new File("~/data.json"), ContentType.APPLICATION_JSON));

        httpTemplate.exchange(MOCK_TEST_URL, "POST", tRequest, String.class);
        Assertions.assertTrue(httpTemplate.getHttpRequest().getEntity().getClass().equals(FileEntity.class));


        tRequest = new HttpTemplateEntity();
        tRequest.setBody(new File("~/data.json"));

        httpTemplate.exchange(MOCK_TEST_URL, "POST", tRequest, String.class);
        Assertions.assertEquals("application/json; charset=utf-8", httpTemplate.getHttpRequest().getHeader("Content-Type").getValue());
        Assertions.assertTrue(httpTemplate.getHttpRequest().getEntity().getClass().equals(FileEntity.class));
    }

}
