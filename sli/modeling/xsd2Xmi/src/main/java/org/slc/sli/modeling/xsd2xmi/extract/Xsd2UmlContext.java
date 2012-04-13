package org.slc.sli.modeling.xsd2xmi.extract;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.uml.Identifier;

/**
 * Keeps track of all the things we might need while we are making the transformation.
 */
public final class Xsd2UmlContext {
    
    private final Xsd2UmlLookup<QName> typeId = new Xsd2UmlLookup<QName>();
    
    /**
     * Provides type identifier from schema type qualified name.
     */
    public Identifier ensureId(final QName name) {
        return typeId.from(name);
    }
    
    /**
     * Provides tag definition identifier from tag definition name.
     */
    private final Xsd2UmlLookup<String> tagDefinition = new Xsd2UmlLookup<String>();
    
    public Identifier ensureTagDefinitionId(final String name) {
        return tagDefinition.from(name);
    }
    
    public Xsd2UmlContext() {
    }
}
