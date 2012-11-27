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

package org.slc.sli.modeling.jgen;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnit for StandardJavaStreamWriter
 * @author chung
 */
public class StandardJavaStreamWriterTest {

    MockJavaStreamWriter jsw;

    @Before
    public void setup() throws UnsupportedEncodingException {
        jsw = new MockJavaStreamWriter();
    }

    @Test
    public void testCatch() throws IOException {
        jsw.beginCatch(JavaType.JT_STRING, "foo");
        jsw.endCatch();
        String str = jsw.read();
        assertTrue(str.equals("catch(final String foo){}"));
    }

    @Test
    public void testClassWithImplements() throws IOException {
        jsw.beginClass("TestClass", new ArrayList<String>() {
            {
                add("Interface1");
                add("Interface2");
            }
        });
        jsw.endClass();
        String str = jsw.read();
        assertTrue(str.equals("public class TestClass implements Interface1, Interface2{}"));
    }

    @Test
    public void testClassWithExtendsAndImplements() throws IOException {
        jsw.beginClass("TestClass", Arrays.asList("Interface1", "Interface2"), "BaseClass");
        jsw.endClass();
        String str = jsw.read();
        assertEquals(str, "public class TestClass extends BaseClass implements Interface1, Interface2{}");
    }

    @Test
    public void testClassWithExtends() throws IOException {
        jsw.beginClass("TestClass", "ParentClass");
        jsw.endClass();
        String str = jsw.read();
        assertTrue(str.equals("public class TestClass extends ParentClass{}"));
    }

    @Test
    public void testEnum() throws IOException {
        jsw.beginEnum("TestEnum");
        jsw.endEnum();
        String str = jsw.read();
        assertTrue(str.equals("public enum TestEnum{}"));
    }

    @Test
    public void testInterface() throws IOException {
        jsw.beginInterface("TestInterface");
        jsw.endBlock();
        String str = jsw.read();
        assertTrue(str.equals("public interface TestInterface{}"));
    }

    @Test
    public void testCastAs() throws IOException {
        jsw.castAs(JavaType.JT_DOUBLE);
        String str = jsw.read();
        assertTrue(str.equals("(Double)"));
    }

    @Test (expected = IOException.class)
    public void testClose() throws IOException {
        jsw.close();
        jsw.write("test");
    }

    @Test
    public void testElementName() throws IOException {
        jsw.elementName("TitleCase");
        String str = jsw.read();
        assertTrue(str.equals("titleCase"));
    }

    @Test
    public void testWriteArgs() throws IOException {
        String[] args = new String[] { "arg1", "arg2" };
        jsw.writeArgs(args);
        String str = jsw.read();
        assertTrue(str.equals("arg1, arg2"));
    }

    @Test
    public void testWriteAttribute() throws IOException {
        JavaParam param = new JavaParam("param", JavaType.JT_STRING, false);
        jsw.writeAttribute(param);
        String str = jsw.read();
        assertTrue(str.equals("private final String param;"));
    }

    @Test
    public void testWriteAttributeWithTypeName() throws IOException {
        jsw.writeAttribute("param", "String");
        String str = jsw.read();
        assertTrue(str.equals("private final String param;"));
    }

    @Test
    public void testWriteEnumLiteral() throws IOException {
        String testString = "-/,()':;.";
        jsw.writeEnumLiteral(testString, "unusedString");
        String str = jsw.read();
        assertTrue(str.equals("_HYPHEN__FWDSLASH__COMMA__LPAREN__RPAREN__APOS__COLON__SEMICOLON__PERIOD_"));
    }

    @Test
    public void testWriteOverride() throws IOException {
        jsw.writeOverride();
        String str = jsw.read();
        assertTrue(str.equals("@Override\r"));
    }

    @Test
    public void testWriteParams() throws IOException {
        JavaParam[] params = new JavaParam[] {
                new JavaParam("param1", JavaType.JT_STRING, false),
                new JavaParam("param2", JavaType.JT_STRING, true)
        };
        jsw.writeParams(params);
        String str = jsw.read();
        assertTrue(str.equals("String param1, final String param2"));
    }

    @Test
    public void testWriteThrows() throws IOException {
        JavaType[] exceptions = new JavaType[] {
                JavaType.complexType("Exception1", JavaType.JT_EXCEPTION),
                JavaType.complexType("Exception2", JavaType.JT_EXCEPTION),
        };
        jsw.writeThrows(exceptions);
        String str = jsw.read();
        assertTrue(str.equals(" throws Exception1, Exception2"));
    }

}
