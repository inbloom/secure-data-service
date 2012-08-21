package org.slc.sli.api.resources.generic.util;

/**
 * @author jstokes
 */
public enum ResourceTemplate {
    ONE_PART("/rest/{version}/{resource}"),
    TWO_PART("/rest/{version}/{resource}/{id}"),
    THREE_PART("/rest/{version}/{base}/{id}/{resource}"),
    FOUR_PART("/rest/{version}/{base}/{id}/{association}/{resource}"),
    CUSTOM("/rest/{version}/{resource}/{id}/custom"),
    AGGREGATES("/rest/{version}/{resource}/{id}/aggregates");

    private final String template;

    private ResourceTemplate(final String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
