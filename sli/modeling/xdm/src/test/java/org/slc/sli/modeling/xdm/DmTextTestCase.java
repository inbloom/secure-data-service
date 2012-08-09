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
