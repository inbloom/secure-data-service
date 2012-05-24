package org.slc.sli.modeling.xdm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

public final class DmDocument implements DmNode {

    private static final QName NO_NAME = new QName("");

    private final List<DmNode> children;

    public DmDocument(final List<? extends DmNode> childAxis) {
        if (childAxis == null) {
            throw new NullPointerException("childAxis");
        }
        this.children = Collections.unmodifiableList(new ArrayList<DmNode>(childAxis));
    }

    @Override
    public List<DmNode> getChildAxis() {
        return children;
    }

    @Override
    public QName getName() {
        return NO_NAME;
    }

    @Override
    public String getStringValue() {
        throw new UnsupportedOperationException("TODO");
    }
}
