package org.slc.sli.modeling.xdm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

public final class DmTextTestCase extends TestCase {

    public void testConstruction() {
        final DmNode text = new DmText("Hello");
        assertEquals("Hello", text.getStringValue());
        assertEquals(new QName(""), text.getName());
        assertEquals(new LinkedList<DmNode>(), text.getChildAxis());
        final List<DmNode> children = text.getChildAxis();
        foo(children);
    }

    private static List<DmNode> foo(final List<? extends DmNode> nodes) {
        return new ArrayList<DmNode>(nodes);
    }
}
