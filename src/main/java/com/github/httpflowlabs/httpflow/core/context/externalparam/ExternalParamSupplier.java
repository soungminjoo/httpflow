package com.github.httpflowlabs.httpflow.core.context.externalparam;

import java.util.Map;

public interface ExternalParamSupplier {

    Map<String, Object> getExternalParams();

}
