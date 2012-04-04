package org.slc.sli.modeling.xsd2xmi;

import javax.xml.namespace.QName;

/**
 * Keeps track of all the things we might need while we are making the transformation.
 */
public final class Xsd2UmlContext {
    
    /**
     * Provides type identifier from schema type qualified name.
     */
    public final Xsd2UmlLookup<QName> typeId = new Xsd2UmlLookup<QName>();
    
    /**
     * Provides tag definition identifier from tag definition name.
     */
    public final Xsd2UmlLookup<String> tagDefinition = new Xsd2UmlLookup<String>();
    
    public Xsd2UmlContext() {
    }
}
