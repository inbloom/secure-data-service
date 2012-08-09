/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
