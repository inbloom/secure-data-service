package org.slc.sli.modeling.xdm;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

public final class DmNamespace implements DmNode {

    private final QName prefix;
    private final String namespace;

    public DmNamespace(final String prefix, final String namespace) {
        if (prefix == null) {
            throw new NullPointerException("prefix");
        }
        if (namespace == null) {
            throw new NullPointerException("namespace");
        }
        this.prefix = new QName(prefix);
        this.namespace = namespace;
    }

    @Override
    public QName getName() {
        return prefix;
    }

    @Override
    public String getStringValue() {
        return namespace;
    }

    @Override
    public List<DmNode> getChildAxis() {
        return Collections.emptyList();
    }
}
