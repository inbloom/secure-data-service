package org.slc.sli.modeling.xdm;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

public final class DmComment implements DmNode {

    private static final QName NO_NAME = new QName("");
    private final String value;

    public DmComment(final String value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value = value;
    }

    @Override
    public List<DmNode> getChildAxis() {
        return Collections.emptyList();
    }

    @Override
    public QName getName() {
        return NO_NAME;
    }

    @Override
    public String getStringValue() {
        return value;
    }
}
