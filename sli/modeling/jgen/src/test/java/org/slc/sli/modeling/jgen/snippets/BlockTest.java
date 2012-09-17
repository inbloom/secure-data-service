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
 * JUnit for Block
 * @author chung
 */
public class BlockTest {

    Block block;
    MockJavaStreamWriter jsw;

    @Before
    public void setup() {
        jsw = new MockJavaStreamWriter();
        JavaSnippet[] stmts = new JavaSnippet[] { new Annotation("tag1"), new Annotation("tag2") };
        block = new Block(stmts);
    }

    @Test
    public void testWrite() throws IOException {
        block.write(jsw);
        String str = jsw.read();
        assertTrue(str.equals("{@tag1\n@tag2\n}"));
    }

}
