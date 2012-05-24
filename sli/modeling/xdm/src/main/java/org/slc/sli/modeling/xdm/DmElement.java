package org.slc.sli.modeling.xdm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

public final class DmElement implements DmNode {

    private static final List<DmNode> EMPTY_CHILD_AXIS = Collections.emptyList();
    private final List<DmNode> children;
    private final QName name;

    public DmElement(final QName name) {
        this(name, EMPTY_CHILD_AXIS);
    }

    public DmElement(final QName name, final List<? extends DmNode> childAxis) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (childAxis == null) {
            throw new NullPointerException("childAxis");
        }
        this.name = name;
        this.children = Collections.unmodifiableList(new ArrayList<DmNode>(childAxis));
    }

    @Override
    public List<DmNode> getChildAxis() {
        return children;
    }

    @Override
    public QName getName() {
        return name;
    }

    @Override
    public String getStringValue() {
        throw new UnsupportedOperationException("TODO");
    }
}
