package org.slc.sli.modeling.xdm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DmNodeList implements DmNodeSequence {

    @SuppressWarnings("unused")
    private final List<DmNode> nodes;

    public DmNodeList(final List<? extends DmNode> nodes) {
        if (nodes == null) {
            throw new NullPointerException("nodes");
        }
        this.nodes = Collections.unmodifiableList(new ArrayList<DmNode>(nodes));
    }
}
