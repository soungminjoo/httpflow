package com.github.httpflowlabs.httpflow.core.context;

import com.github.httpflowlabs.httpflow.support.velocityex.VelocityContextEx;
import org.apache.hc.client5.http.cookie.CookieStore;

public interface HttpFlowContext {

    VelocityContextEx getVelocityContext();
    VelocityContextEx getCookieMergedVelocityContext();
    Object getContextVariableByOgnl(String ognlExpr);

    void addExternalParam(String key, Object value);

    CookieStore getCookieStore();
    void setCookieStore(CookieStore cookieStore);

    String getCsrfToken();
    void setCsrfToken(String csrfToken);

    boolean isStopped();
    void reset();

}
