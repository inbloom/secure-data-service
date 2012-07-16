package org.slc.sli.modeling.waudit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Configuration information for fixing the WADL.
 */
public final class WadlAuditConfig {

    private final String prefix;
    private final String namespaceURI;
    private final ModelIndex model;
    private final Map<String, QName> elementNames;

    public WadlAuditConfig(final String prefix, final String namespaceURI, final ModelIndex model,
            final Map<String, QName> elementNames) {
        if (prefix == null) {
            throw new NullPointerException("prefix");
        }
        if (namespaceURI == null) {
            throw new NullPointerException("namespaceURI");
        }
        if (model == null) {
            throw new NullPointerException("model");
        }
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;
        this.model = model;
        this.elementNames = Collections.unmodifiableMap(new HashMap<String, QName>(elementNames));
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public ModelIndex getModel() {
        return model;
    }

    public Map<String, QName> getElementNameMap() {
        return elementNames;
    }
}
