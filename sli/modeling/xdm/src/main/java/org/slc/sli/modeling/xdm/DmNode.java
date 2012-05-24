package org.slc.sli.modeling.xdm;

import java.util.List;

import javax.xml.namespace.QName;

public interface DmNode extends DmItem, DmNodeSequence {

    /**
     * The dm:name property.
     */
    QName getName();

    /**
     * The dm:child-axis property.
     */
    List<DmNode> getChildAxis();

    /**
     * Returns the dm:string-value property.
     */
    String getStringValue();
}
