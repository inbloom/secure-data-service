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


package org.slc.sli.modeling.wadl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.slc.sli.modeling.rest.ParamStyle;

public class WadlSyntaxTestCase extends TestCase {

    public void testDecodeParamStyle() {

        assertEquals(5, ParamStyle.values().length);

        assertEquals(ParamStyle.PLAIN, WadlSyntax.decodeParamStyle("plain"));
        assertEquals(ParamStyle.QUERY, WadlSyntax.decodeParamStyle("query"));
        assertEquals(ParamStyle.MATRIX, WadlSyntax.decodeParamStyle("matrix"));
        assertEquals(ParamStyle.HEADER, WadlSyntax.decodeParamStyle("header"));
        assertEquals(ParamStyle.TEMPLATE, WadlSyntax.decodeParamStyle("template"));

        assertNull(WadlSyntax.decodeParamStyle(null));

        try {
            assertEquals(ParamStyle.TEMPLATE, WadlSyntax.decodeParamStyle("foo"));
            fail();
        } catch (final IllegalArgumentException e) {
            // Expected
        }
    }

    public void testEncodeParamStyle() {

        assertEquals(5, ParamStyle.values().length);

        assertEquals("plain", WadlSyntax.encodeParamStyle(ParamStyle.PLAIN));
        assertEquals("query", WadlSyntax.encodeParamStyle(ParamStyle.QUERY));
        assertEquals("matrix", WadlSyntax.encodeParamStyle(ParamStyle.MATRIX));
        assertEquals("header", WadlSyntax.encodeParamStyle(ParamStyle.HEADER));
        assertEquals("template", WadlSyntax.encodeParamStyle(ParamStyle.TEMPLATE));

        assertNull(WadlSyntax.encodeParamStyle(null));
    }

    public void testEncodeStringList() {
        final ArrayList<String> mutableList = new ArrayList<String>();
        final List<String> immutableList = Collections.unmodifiableList(mutableList);

        assertEquals("", WadlSyntax.encodeStringList(immutableList));

        mutableList.add("a");

        assertEquals("a", WadlSyntax.encodeStringList(immutableList));

        mutableList.add("b");

        assertEquals("a b", WadlSyntax.encodeStringList(immutableList));

        mutableList.add("c");

        assertEquals("a b c", WadlSyntax.encodeStringList(immutableList));

        assertNull(WadlSyntax.encodeStringList(null));
    }
}
