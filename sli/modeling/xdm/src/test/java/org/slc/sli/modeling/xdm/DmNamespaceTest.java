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
 * JUnit test for DmNamespace class.
 */
public class DmNamespaceTest {

    @Test
    public void test() {
        String prefix = "foo";
        String namespace = "bar";

        DmNamespace dmNamespace = new DmNamespace(prefix, namespace);

        assertTrue(dmNamespace.getChildAxis().size() == 0);
        assertTrue(dmNamespace.getName().equals(new QName(prefix)));
        assertTrue(dmNamespace.getStringValue().equals(namespace));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPrefixThrowsException() {
        new DmNamespace("foo", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullNamespaceThrowsException() {
        new DmNamespace(null, "bar");
    }
}
