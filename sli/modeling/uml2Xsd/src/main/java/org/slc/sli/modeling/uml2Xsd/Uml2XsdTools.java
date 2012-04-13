package org.slc.sli.modeling.uml2Xsd;

import javax.xml.namespace.QName;

final class Uml2XsdTools {
    
    /**
     * Changes the local-name part of the {@link QName} to camelCase.
     */
    public static final QName camelCase(final QName name) {
        final String text = name.getLocalPart();
        final String localName = text.substring(0, 1).toLowerCase().concat(text.substring(1));
        return new QName(name.getNamespaceURI(), localName, name.getPrefix());
    }
    
}
