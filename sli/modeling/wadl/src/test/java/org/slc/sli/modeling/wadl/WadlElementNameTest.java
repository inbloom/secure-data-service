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

package org.slc.sli.modeling.wadl;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * JUnit test for WadlElementName class.
 *
 * @author wscott
 *
 */
public class WadlElementNameTest {

    @Test
    public void testWadlElementName() {
        final String elementName = "application";
        WadlElementName wadlElementName = WadlElementName.getElementName(elementName);
        assertEquals(elementName, wadlElementName.getLocalName());
    }

    @Test
    public void testInvalidWadlElementName() {
        WadlElementName wadlElementName = WadlElementName.getElementName("NOTAREALNAME");
        assertEquals(null, wadlElementName);
    }

    @Test
    public void testNullWadlElementName() {
        WadlElementName wadlElementName = WadlElementName.getElementName(null);
        assertEquals(null, wadlElementName);
    }

}
