package org.slc.sli.modeling.jgen;

import javax.xml.namespace.QName;

public final class JavaType {

    private final QName name;

    public JavaType(final String simpleName) {
        name = new QName(simpleName);
    }

    public String getSimpleName() {
        return name.getLocalPart();
    }
}
