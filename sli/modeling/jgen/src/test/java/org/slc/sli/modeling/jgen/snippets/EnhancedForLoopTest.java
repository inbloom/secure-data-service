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
import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaSnippetExpr;
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.jgen.MockJavaStreamWriter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertTrue;

/**
 * JUnit for EnhancedForLoop
 * @author chung
 */
public class EnhancedForLoopTest {

    EnhancedForLoop enhancedForLoop;
    MockJavaStreamWriter jsw;

    @Before
    public void setup() throws UnsupportedEncodingException {
        JavaParam param = new JavaParam("param", JavaType.JT_BOOLEAN, false);
        JavaSnippetExpr[] args = new JavaSnippetExpr[] { NullExpr.SINGLETON, NullExpr.SINGLETON };
        enhancedForLoop = new EnhancedForLoop(param,
                new MethodCallExpr(new VarNameExpr("testVar"), "testMethod", args),
                new SuppressUncheckedAnnotation());
        jsw = new MockJavaStreamWriter();
    }

    @Test
    public void testWrite() throws IOException {
        enhancedForLoop.write(jsw);
        String str = jsw.read();
        assertTrue(str.equals(
                "for (final Boolean param : testVar.testMethod(null,null)){@SuppressWarnings(\"unchecked\")\n}"
        ));
    }

}
