/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import org.junit.Test;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * JUnit test for DmElement class.
 */
public final class DmElementTest {

    @Test
    public void testConstruction() {
        final QName name = new QName("");
        final List<DmNode> nodes = new ArrayList<DmNode>();
        final DmElement element = new DmElement(name, nodes);
        assertEquals(name, element.getName());
        assertEquals(nodes, element.getChildAxis());
    }

    @Test
    public void testConstructionNameOnly() {
        final QName name = new QName("");
        final List<DmNode> nodes = new ArrayList<DmNode>();
        final DmElement element = new DmElement(name);
        assertEquals(name, element.getName());
        assertEquals(nodes, element.getChildAxis());
    }

    @Test
    public void testConstructionPECS() {
        // We're just looking for compilation and no exceptions
        new DmElement(new QName(""), new ArrayList<DmElement>());
        new DmElement(new QName(""), new ArrayList<DmText>());
        new DmElement(new QName(""), new ArrayList<DmProcessingInstruction>());
        new DmElement(new QName(""), new ArrayList<DmComment>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructionNullName() {
        new DmElement(null, new ArrayList<DmNode>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructionNullNodes() {
        new DmElement(new QName(""), null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetStringValue() {
        new DmElement(new QName(""), new ArrayList<DmElement>()).getStringValue();
    }
}
