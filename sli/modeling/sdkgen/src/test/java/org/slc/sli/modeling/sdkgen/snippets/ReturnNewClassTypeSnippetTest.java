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
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.jgen.MockJavaStreamWriter;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */
public class ReturnNewClassTypeSnippetTest {
    @Test
    public void testWrite() throws Exception {
        MockJavaStreamWriter jsw = new MockJavaStreamWriter();
        JavaParam mockParam = mock(JavaParam.class);
        when(mockParam.getName()).thenReturn("testParam");
        ReturnNewClassTypeSnippet stmt = new ReturnNewClassTypeSnippet(JavaType.JT_STRING, mockParam);

        stmt.write(jsw);

        assertEquals("return new String(testParam);", jsw.read());
    }
}
