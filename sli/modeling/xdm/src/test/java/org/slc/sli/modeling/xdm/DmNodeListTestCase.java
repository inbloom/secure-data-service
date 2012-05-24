package org.slc.sli.modeling.xdm;

import java.util.LinkedList;

import junit.framework.TestCase;

public final class DmNodeListTestCase extends TestCase {

    public void testConstruction() {
        new DmNodeList(new LinkedList<DmNode>());
        new DmNodeList(new LinkedList<DmElement>());
        new DmNodeList(new LinkedList<DmAttribute>());
        new DmNodeList(new LinkedList<DmText>());
        new DmNodeList(new LinkedList<DmProcessingInstruction>());
        new DmNodeList(new LinkedList<DmComment>());
        new DmNodeList(new LinkedList<DmDocument>());
    }

}
