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

package org.slc.sli.modeling.xmi.comp;

import org.junit.Test;

import org.junit.Assert;

/**
 * JUnit test for XmiDefinition class.
 */
public class XmiDefinitionTest {

    private static final String NAME = "NAME";
    private static final String VERSION = "VERSION";
    private static final String FILE = "FILE";

    private XmiDefinition xmiDefinition = new XmiDefinition(NAME, VERSION, FILE);

    @Test
    public void testGetName() {
        Assert.assertEquals(NAME, xmiDefinition.getName());
    }

    @Test
    public void testGetVersion() {
        Assert.assertEquals(VERSION, xmiDefinition.getVersion());
    }

    @Test
    public void testGetFile() {
        Assert.assertEquals(FILE, xmiDefinition.getFile());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam1() {
        new XmiDefinition(null, VERSION, FILE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam2() {
        new XmiDefinition(NAME, null, FILE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam3() {
        new XmiDefinition(NAME, VERSION, null);
    }
}
