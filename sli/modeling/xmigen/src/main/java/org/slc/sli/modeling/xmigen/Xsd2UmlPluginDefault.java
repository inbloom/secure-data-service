package org.slc.sli.modeling.xmigen;

import javax.xml.namespace.QName;

/**
 * Provides default behavior, though implementation inheritance for a plug-in.
 */
public abstract class Xsd2UmlPluginDefault implements Xsd2UmlPlugin {
    
    @Override
    public final String nameFromComplexTypeExtension(final QName complexType, final QName base) {
        if (complexType == null) {
            throw new NullPointerException("complexType");
        }
        if (base == null) {
            throw new NullPointerException("base");
        }
        return nameFromSchemaTypeName(complexType).concat(" extends ").concat(nameFromSchemaTypeName(base));
    }
    
    /**
     * Retains only the local-name part of the qualified name.
     */
    @Override
    public String nameFromSchemaAttributeName(final QName name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return name.getLocalPart();
    }
    
    /**
     * Retains only the local-name part of the qualified name.
     */
    @Override
    public String nameFromSchemaElementName(final QName name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return name.getLocalPart();
    }
    
    /**
     * Retains only the local-name part of the qualified name.
     */
    @Override
    public String nameFromSchemaTypeName(final QName name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        return name.getLocalPart();
    }
    
    @Override
    public final String nameFromSimpleTypeRestriction(final QName simpleType, final QName base) {
        if (simpleType == null) {
            throw new NullPointerException("simpleType");
        }
        if (base == null) {
            throw new NullPointerException("base");
        }
        return nameFromSchemaTypeName(simpleType).concat(" restricts ").concat(nameFromSchemaTypeName(base));
    }
}
