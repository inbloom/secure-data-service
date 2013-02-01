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

import static org.junit.Assert.assertTrue;

/**
 * JUnit test for dmProcessingInstruction class.
 */
public class DmProcessingInstructionTest {

    @Test
    public void test() {
        String target = "foo";
        String data = "bar";

        DmProcessingInstruction dmProcessingInstruction = new DmProcessingInstruction(target, data);

        assertTrue(dmProcessingInstruction.getChildAxis().size() == 0);
        assertTrue(dmProcessingInstruction.getName().equals(new QName(target)));
        assertTrue(dmProcessingInstruction.getStringValue().equals(data));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPrefixThrowsException() {
        new DmProcessingInstruction("foo", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullNamespaceThrowsException() {
        new DmProcessingInstruction(null, "bar");
    }
}
