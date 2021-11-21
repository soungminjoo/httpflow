package com.github.httpflowlabs.httpflow.support.velocityex;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.StringWriter;

public class VelocityTemplateEx {

    private Template template;

    public VelocityTemplateEx(Template template) {
        this.template = template;
    }

    public String merge(VelocityContext context) {
        if (template == null) {
            return null;
        }

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

}
