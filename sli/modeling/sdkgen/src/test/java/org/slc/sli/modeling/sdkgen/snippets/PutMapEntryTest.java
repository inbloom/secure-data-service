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

package org.slc.sli.modeling.sdkgen.snippets;

import org.junit.Test;
import org.slc.sli.modeling.jgen.JavaSnippet;
import org.slc.sli.modeling.jgen.JavaStreamWriter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
public class PutMapEntryTest {
    @Test
    public void testWrite() throws Exception {
        JavaSnippet mockSnippet = mock(JavaSnippet.class);
        PutMapEntry expr = new PutMapEntry("test", mockSnippet);
        JavaStreamWriter jsw = mock(JavaStreamWriter.class);

        when(jsw.write(anyString())).thenReturn(jsw);
        when(jsw.parenL()).thenReturn(jsw);
        when(jsw.dblQte()).thenReturn(jsw);
        when(jsw.comma()).thenReturn(jsw);
        when(jsw.write(any(JavaSnippet.class))).thenReturn(jsw);

        expr.write(jsw);

        verify(jsw).beginStmt();
        verify(jsw).write("data.put");
        verify(jsw).parenL();
        verify(jsw, atLeastOnce()).dblQte();

        verify(jsw).endStmt();
    }
}
