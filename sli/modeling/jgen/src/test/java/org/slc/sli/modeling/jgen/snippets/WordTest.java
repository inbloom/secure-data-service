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
import org.slc.sli.modeling.jgen.MockJavaStreamWriter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertTrue;

/**
 * JUnit for Word
 * @author chung
 */
public class WordTest {

    MockJavaStreamWriter jsw;

    @Before
    public void setup() throws UnsupportedEncodingException {
        jsw = new MockJavaStreamWriter();
    }

    @Test
    public void testWriteNull() throws IOException {
        Word.NULL.write(jsw);
        String str = jsw.read();
        assertTrue(str.equals("null"));
    }

    @Test
    public void testWriteBlank() throws IOException {
        Word.BLANK.write(jsw);
        String str = jsw.read();
        assertTrue(str.equals(""));
    }

    @Test
    public void testWriteNew() throws IOException {
        Word.NEW.write(jsw);
        String str = jsw.read();
        assertTrue(str.equals("new"));
    }

    @Test
    public void testValueOf() throws IOException {
        Word.valueOf("NEW").write(jsw);
        String str = jsw.read();
        assertTrue(str.equals("new"));
    }

}
