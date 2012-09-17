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

package org.slc.sli.modeling.xsdgen;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author jstokes
 */
public class Uml2XsdToolsTest {
    @Test
    public void testCamelCase() throws Exception {
        String expected;
        String actual;

        expected = "myTest";
        actual = Uml2XsdTools.camelCase("MyTest");
        assertEquals(expected, actual);

        expected = "myTest";
        actual = Uml2XsdTools.camelCase("myTest");
        assertEquals(expected, actual);
    }

    @Test
    public void testInit() {
        Uml2XsdTools tools = new Uml2XsdTools();
        assertNotNull(tools);
    }
}
