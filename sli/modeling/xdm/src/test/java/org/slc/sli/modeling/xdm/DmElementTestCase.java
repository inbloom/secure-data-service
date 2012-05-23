package org.slc.sli.modeling.xdm;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

public final class DmElementTestCase extends TestCase {

    public void testConstruction() {
        final QName name = new QName("");
        final List<DmNode> nodes = new LinkedList<DmNode>();
        final DmElement element = new DmElement(name, nodes);
        assertEquals(name, element.getName());
        assertEquals(nodes, element.getChildAxis());
    }

    public void testConstructionNameOnly() {
        final QName name = new QName("");
        final List<DmNode> nodes = new LinkedList<DmNode>();
        final DmElement element = new DmElement(name);
        assertEquals(name, element.getName());
        assertEquals(nodes, element.getChildAxis());
    }

    /**
     * Producer extends Consumer super.
     */
    public void testConstructionPECS() {
        // We're just looking for compilation and no exceptions
        new DmElement(new QName(""), new LinkedList<DmElement>());
        new DmElement(new QName(""), new LinkedList<DmText>());
        new DmElement(new QName(""), new LinkedList<DmProcessingInstruction>());
        new DmElement(new QName(""), new LinkedList<DmComment>());
    }

    public void testConstructionNullName() {
        try {
            new DmElement(null, new LinkedList<DmNode>());
            fail();
        } catch (final NullPointerException e) {
            assertEquals("name", e.getMessage());
        }
    }

    public void testConstructionNullNodes() {
        try {
            new DmElement(new QName(""), null);
            fail();
        } catch (final NullPointerException e) {
            assertEquals("childAxis", e.getMessage());
        }
    }
}
