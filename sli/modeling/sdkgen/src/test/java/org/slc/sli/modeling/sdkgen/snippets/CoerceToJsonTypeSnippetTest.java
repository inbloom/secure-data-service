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
import org.slc.sli.modeling.jgen.JavaCollectionKind;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.jgen.JavaTypeKind;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
public class CoerceToJsonTypeSnippetTest {
    @Test
    public void testWrite() throws Exception {
        JavaType mockType = mock(JavaType.class);
        CoerceToJsonTypeSnippet snip = new CoerceToJsonTypeSnippet("test", mockType);
        JavaStreamWriter jsw = mock(JavaStreamWriter.class);
        when(jsw.write(anyString())).thenReturn(jsw);
        when(jsw.parenR()).thenReturn(jsw);
        when(jsw.parenL()).thenReturn(jsw);

        when(mockType.getCollectionKind()).thenReturn(JavaCollectionKind.NONE);
        when(mockType.getTypeKind()).thenReturn(JavaTypeKind.SIMPLE);

        snip.write(jsw);

        verify(jsw).write("test");

        when(mockType.getTypeKind()).thenReturn(JavaTypeKind.ENUM);
        snip.write(jsw);
        verify(jsw, atLeastOnce()).write("test");
        verify(jsw, atLeastOnce()).write(".getName");

        when(mockType.getTypeKind()).thenReturn(JavaTypeKind.COMPLEX);
        snip.write(jsw);
        verify(jsw, atLeastOnce()).write("test");
        verify(jsw, atLeastOnce()).write(".toMap");
    }
}
