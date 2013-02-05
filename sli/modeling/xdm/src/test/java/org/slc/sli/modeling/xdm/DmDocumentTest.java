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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * JUnit test for DmDocument class.
 */
public class DmDocumentTest {

    @Test
    public void testConstructorAndGetters() {

        List<DmNode> dmNodes = new ArrayList<DmNode>();
        dmNodes.add(null);
        dmNodes.add(null);

        DmDocument dmDocument = new DmDocument(dmNodes);

        assertTrue(dmDocument.getChildAxis().equals(dmNodes));
        assertTrue(dmDocument.getName() == DmDocument.NO_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullListForConstructorThrowsException() {
        new DmDocument(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetStringValue() {

        DmDocument dmDocument = new DmDocument(new ArrayList<DmNode>());
        dmDocument.getStringValue();
    }

}
