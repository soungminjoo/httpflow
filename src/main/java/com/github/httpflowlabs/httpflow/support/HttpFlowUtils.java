package com.github.httpflowlabs.httpflow.support;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.httpflowlabs.httpflow.core.context.HttpFlowContext;
import com.github.httpflowlabs.httpflow.support.velocityex.ReferenceHandlerForOgnl;
import com.github.httpflowlabs.httpflow.support.velocityex.VelocityContextEx;
import com.github.httpflowlabs.httpflow.support.velocityex.VelocityEngineEx;
import com.github.httpflowlabs.httpflow.support.velocityex.VelocityTemplateEx;
import com.google.common.net.UrlEscapers;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.utils.URIUtils;
import org.apache.velocity.app.event.EventCartridge;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;

public class HttpFlowUtils {

    // Empty Checks ----------------------------------------------------------------------------------------------------
    public static boolean isEmpty(String obj) {
        return isEmpty(obj, false);
    }

    public static boolean isEmpty(String obj, boolean trim) {
        if (obj == null) {
            return true;
        }
        if (trim) {
            return obj.trim().length() == 0;
        }
        return obj.length() == 0;
    }

    public static boolean isEmpty(Collection obj) {
        return obj == null || obj.size() == 0;
    }

    public static boolean isEmpty(Map obj) {
        return obj == null || obj.size() == 0;
    }


    // IO & ObjectMapper -----------------------------------------------------------------------------------------------
    public static String readInputStream(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            try { inputStream.close(); } catch (Exception e) { }
        }
    }

    public static String readFile(File file) {
        return readInputStream(toInputStream(file));
    }

    public static InputStream toInputStream(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return new ByteArrayInputStream(IOUtils.toByteArray(fis));
        } catch (Exception e) {
            return null;
        }
    }

    public static List readValueAsList(String jsonString) {
        return readValueAsList(jsonString, null);
    }

    public static List readValueAsList(String jsonString, Class listElementType) {
        if (HttpFlowUtils.isEmpty(jsonString)) {
            return new ArrayList();
        }

        if (jsonString.startsWith("{")) {
            List elements = new ArrayList<>();
            elements.add(HttpFlowUtils.readValueAsObject(jsonString, listElementType));
            return elements;
        }

        try {
            if (listElementType == null) {
                return new ObjectMapper().readValue(jsonString, List.class);
            }
            return new ObjectMapper().readValue(jsonString, TypeFactory.defaultInstance().constructCollectionType(List.class, listElementType));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Map readValueAsMap(String jsonString) {
        if (HttpFlowUtils.isEmpty(jsonString)) {
            return new HashMap();
        }

        try {
            return new ObjectMapper().readValue(jsonString, Map.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T readValueAsObject(String jsonString, Class objectType) {
        try {
            if (objectType == null) {
                objectType = Map.class;
            }
            return (T) new ObjectMapper().readValue(jsonString, objectType);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String writeValueAsString(Object object) {
        if (object == null) {
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }


    // URI -------------------------------------------------------------------------------------------------------------
    public static URI parseURI(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public static URI parseURI(String scheme, String host, String path) {
        try {
            return new URI(scheme, host, path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String parseAndGetHost(String url) {
        URI uri = HttpFlowUtils.parseURI(url);
        return uri.getHost();
    }

    public static String parseAndGetHostIncludingPortNum(String url) {
        URI uri = HttpFlowUtils.parseURI(url);
        String host = uri.getHost();

        if (uri.getPort() != 80) {
            host += ":" + uri.getPort();
        }
        return host;
    }

    public static int indexOfNullSafe(String string, String str) {
        if (string == null) {
            return -1;
        }
        return string.indexOf(str);
    }


    // Velocity --------------------------------------------------------------------------------------------------------
    public static String translateWithVelocity(String templateStr, VelocityContextEx velocityContext, boolean isOgnlMode) {
        VelocityEngineEx velocityEngineEx = new VelocityEngineEx(false);
        if (isOgnlMode) {
//            velocityEngineEx.addProperty("event_handler.invalid_references.class", InvalidRefHandlerForOgnl.class.getName());
        }
        velocityEngineEx.init();

        VelocityTemplateEx template = velocityEngineEx.getTemplateFromString(templateStr);
        if (isOgnlMode) {
            EventCartridge ec = new EventCartridge();
            ec.addEventHandler(new ReferenceHandlerForOgnl());
            ec.attachToContext(velocityContext);
        }
        return template.merge(velocityContext);
    }

    public static String translateWithVelocity(String templateStr, HttpFlowContext context) {
        VelocityContextEx velocityContext = context.getCookieMergedVelocityContext();
        return translateWithVelocity(templateStr, velocityContext, false);
    }

    // Reflection ------------------------------------------------------------------------------------------------------
    public static <T> T newInstance(String className) {
        try {
            return (T) Class.forName(className).newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // ETC -------------------------------------------------------------------------------------------------------------
    public static String[] splithttpFlowContents(String httpFlowContents) {
        String[] lines = httpFlowContents.replaceAll("\\n", "\n ").split("\n");
        for (int i = 1; i < lines.length; i++) {
            lines[i] = lines[i].substring(1);
        }
        return lines;
    }

}
