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

package org.slc.sli.modeling.jgen.snippets;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.MockJavaStreamWriter;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * JUnit for IfThenElse
 * @author chung
 */
public class IfThenElseTest {

    IfThenElse ifThenElse;
    MockJavaStreamWriter jsw;

    @Before
    public void setup() {
        JavaSnippet testSnip = new NotEqual(new VarNameExpr("var1"), new VarNameExpr("var2"));
        JavaSnippet thenSnip = new TestAnnotation();
        JavaSnippet elseSnip = new ReturnStmt(new VarNameExpr("returnVal"));
        ifThenElse = new IfThenElse(testSnip, thenSnip, elseSnip);
        jsw = new MockJavaStreamWriter();
    }

    @Test
    public void testWrite() throws IOException {
        ifThenElse.write(jsw);
        String str = jsw.read();
        assertTrue(str.equals("if (var1 != var2){@Test\n}else{return returnVal;}"));
    }

}
