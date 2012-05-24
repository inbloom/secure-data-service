package org.slc.sli.modeling.xdm;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

public final class DmProcessingInstruction implements DmNode {

    private final String data;
    private final QName target;

    public DmProcessingInstruction(final String target, final String data) {
        if (target == null) {
            throw new NullPointerException("target");
        }
        if (data == null) {
            throw new NullPointerException("data");
        }
        this.target = new QName(target);
        this.data = data;
    }

    @Override
    public List<DmNode> getChildAxis() {
        return Collections.emptyList();
    }

    @Override
    public QName getName() {
        return target;
    }

    @Override
    public String getStringValue() {
        return data;
    }
}
