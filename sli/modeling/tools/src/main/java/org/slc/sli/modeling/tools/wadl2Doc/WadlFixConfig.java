package org.slc.sli.modeling.tools.wadl2Doc;

/**
 * Configuration information for fixing the WADL.
 */
public final class WadlFixConfig {

    private final String prefix;
    private final String namespaceURI;

    public WadlFixConfig(final String prefix, final String namespaceURI) {
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }
}
