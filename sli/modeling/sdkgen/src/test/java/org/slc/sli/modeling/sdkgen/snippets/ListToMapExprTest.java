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
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.jgen.snippets.VarNameExpr;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author jstokes
 */
public class ListToMapExprTest {
    @Test
    public void testWrite() throws Exception {
        VarNameExpr expr = new VarNameExpr("test");
        ListToMapExpr snip = new ListToMapExpr(expr);
        JavaStreamWriter jsw = mock(JavaStreamWriter.class);

        snip.write(jsw);

        verify(jsw).write("CoerceToJson");
        verify(jsw).write("toListOfMap");
        verify(jsw).parenL();
        verify(jsw).parenR();
    }
}
