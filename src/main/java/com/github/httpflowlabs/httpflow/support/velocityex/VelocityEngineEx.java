package com.github.httpflowlabs.httpflow.support.velocityex;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

public class VelocityEngineEx extends VelocityEngine {

    public static final String STRING_RESOURCE_NAME = "stringResource";

    public VelocityEngineEx() {
        this(true);
    }

    public VelocityEngineEx(boolean needInit) {
        super.setProperty(Velocity.RESOURCE_LOADER, "string");
        super.setProperty(Velocity.PARSER_HYPHEN_ALLOWED, true);
        super.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
        super.addProperty("string.resource.loader.repository.static", "false");

        if (needInit) {
            super.init();
        }
    }

    public VelocityTemplateEx getTemplateFromString(String templateStr) {
        if (templateStr == null) {
            return new VelocityTemplateEx(null);
        }

        Object resourceRepository = super.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
        StringResourceRepository repo = (StringResourceRepository) resourceRepository;
        repo.putStringResource(STRING_RESOURCE_NAME, templateStr);

        return new VelocityTemplateEx(this.getTemplate(STRING_RESOURCE_NAME));
    }

}
