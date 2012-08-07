package org.slc.sli.api.resources.generic.util;

/**
 * @author jstokes
 */
public enum ResourceTemplate {
    ONE_PART("/generic/{resource}"),
    TWO_PART("/generic/{resource}/{id}"),
    THREE_PART("/generic/{endpoint}/{id}/{resource}"),
    FOUR_PART("/generic/{endpoint}/{id}/{association}/{resource}"),
    CUSTOM("/generic/{resource}/{id}/custom");

    private final String template;

    private ResourceTemplate(final String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
