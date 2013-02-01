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

package org.slc.sli.modeling.xsd;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author jstokes
 */
public class XsdElementNameTest {
    @Test
    public void testGetLocalName() {
        final XsdElementName attr = XsdElementName.CHOICE;
        assertEquals(XsdElementName.CHOICE.getLocalName(), attr.getLocalName());
    }

    @Test
    public void testValueOf() {
        final XsdElementName attr = XsdElementName.valueOf("ALL");
        assertEquals(XsdElementName.ALL, attr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEnum() {
        final XsdElementName attr = XsdElementName.valueOf("test");
    }

    @Test(expected = NullPointerException.class)
    public void testNullEnum() {
        final XsdElementName test = XsdElementName.valueOf(null);
    }
}
