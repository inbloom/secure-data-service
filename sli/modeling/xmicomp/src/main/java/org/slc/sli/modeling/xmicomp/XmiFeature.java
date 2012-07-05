package org.slc.sli.modeling.xmicomp;

import javax.xml.namespace.QName;

public final class XmiFeature {
    private final QName name;
    private final QName type;

    public XmiFeature(final QName name, final QName type) {
        if (null == name) {
            throw new NullPointerException("name");
        }
        if (null == type) {
            throw new NullPointerException("type");
        }
        this.name = name;
        this.type = type;
    }

    public QName getName() {
        return name;
    }

    public QName getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("{name : %s, type : %s}", name, type);
    }
}