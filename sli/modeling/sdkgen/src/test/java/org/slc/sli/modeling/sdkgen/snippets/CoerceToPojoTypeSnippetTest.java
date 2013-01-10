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
import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
public class CoerceToPojoTypeSnippetTest {
    @Test
    public void testWrite() throws Exception {
        JavaParam param = mock(JavaParam.class);
        CoerceToPojoTypeSnippet snip = new CoerceToPojoTypeSnippet(param, "test", JavaType.JT_STRING);
        JavaStreamWriter jsw = mock(JavaStreamWriter.class);
        when(jsw.parenL()).thenReturn(jsw);
        when(jsw.write(anyString())).thenReturn(jsw);
        when(jsw.dblQte()).thenReturn(jsw);

        snip.write(jsw);

        verify(jsw).write("Coercions.");
        verify(jsw).write("toString");
        verify(jsw, atLeastOnce()).parenL();
        verify(jsw, times(2)).parenR();
    }
}
