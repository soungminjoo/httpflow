package com.github.httpflowlabs.httpflow.core.context;

import com.github.httpflowlabs.httpflow.support.HttpFlowUtils;
import com.github.httpflowlabs.httpflow.support.velocityex.VelocityContextEx;
import lombok.Getter;
import lombok.Setter;
import ognl.Ognl;
import ognl.OgnlContext;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.velocity.VelocityContext;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class DefaultHttpFlowContext implements HttpFlowContext {

    private boolean isStopped;

    private VelocityContextEx velocityContext = new VelocityContextEx();
    private CookieStore cookieStore = new BasicCookieStore();
    private Map<String, Object> externalParams = new HashMap<>();

    private String csrfToken;

    public DefaultHttpFlowContext() {
    }

    public DefaultHttpFlowContext(VelocityContext velocityContext) {
        this.velocityContext = new VelocityContextEx(velocityContext);
    }

    public void addExternalParam(String key, Object value) {
        externalParams.put(key, value);
    }

    public VelocityContextEx getCookieMergedVelocityContext() {
        Map<String, String> cookieMap = new HashMap<>();
        for (Cookie cookie : this.getCookieStore().getCookies()) {
            cookieMap.put(cookie.getName(), cookie.getValue());
        }

        VelocityContextEx finalContext = new VelocityContextEx(this.velocityContext);
        finalContext.put("cookie", cookieMap);

        if (!HttpFlowUtils.isEmpty(externalParams)) {
            finalContext.putAll(externalParams);
        }
        return finalContext;
    }

    public Object getContextVariableByOgnl(String ognlExpr) {
        Map root = getVelocityContext().toMap();
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(root);
        try {
            Object value = Ognl.compileExpression(context, root, ognlExpr).getValue(context, root);
            return value;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void reset() {
        this.velocityContext = new VelocityContextEx();
        this.cookieStore = new BasicCookieStore();
    }

}
