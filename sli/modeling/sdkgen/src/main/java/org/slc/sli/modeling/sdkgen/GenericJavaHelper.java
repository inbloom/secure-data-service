package org.slc.sli.modeling.sdkgen;

import javax.xml.namespace.QName;

public final class GenericJavaHelper {

    public static String getJavaType(final QName name) {
        if (name.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")) {
            final String localName = name.getLocalPart();
            if (localName.equals("string")) {
                return String.class.getSimpleName();
            } else {
                throw new AssertionError("localName: " + name.getLocalPart());
            }
        } else {
            throw new AssertionError("namespaceURI: " + name.getNamespaceURI());
        }
    }
}
