package org.slc.sli.modeling.wadl;

/**
 * Symbolic constants used for attribute names in XMI.
 */
public enum WadlAttributeName {
    /**
     * The <code>base</code> attribute.
     */
    BASE("base"),
    /**
     * The <code>default</code> attribute.
     */
    DEFAULT_VALUE("default"),
    /**
     * The <code>element</code> attribute.
     */
    ELEMENT("element"),
    /**
     * The <code>fixed</code> attribute.
     */
    FIXED("fixed"),
    /**
     * The <code>href</code> attribute.
     */
    HREF("href"),
    /**
     * The <code>id</code> attribute.
     */
    ID("id"),
    /**
     * The <code>lang</code> attribute.
     */
    LANG("lang"),
    /**
     * The <code>mediaType</code> attribute.
     */
    MEDIA_TYPE("mediaType"),
    /**
     * The <code>name</code> attribute.
     */
    NAME("name"),
    /**
     * The <code>path</code> attribute.
     */
    PATH("path"),
    /**
     * The <code>profile</code> attribute.
     */
    PROFILE("profile"),
    /**
     * The <code>style</code> attribute.
     */
    QUERY_TYPE("queryType"),
    /**
     * The <code>repeating</code> attribute.
     */
    REPEATING("repeating"),
    /**
     * The <code>required</code> attribute.
     */
    REQUIRED("required"),
    /**
     * The <code>style</code> attribute.
     */
    STYLE("style"),
    /**
     * The <code>title</code> attribute.
     */
    TITLE("title"),
    /**
     * The <code>type</code> attribute.
     */
    TYPE("type"),
    /**
     * The <code>value</code> attribute.
     */
    VALUE("value");

    private final String localName;

    WadlAttributeName(final String localName) {
        this.localName = localName;
    }

    public String getLocalName() {
        return localName;
    }
}
