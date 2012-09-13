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

package org.slc.sli.modeling.sdkgen;

import org.junit.Test;
import org.slc.sli.modeling.rest.Application;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author jstokes
 */
public class WadlTest {
    @Test
    public void testGetApplication() throws Exception {
        Application app = mock(Application.class);
        Wadl<String> wadl = new Wadl<String>(app, "test");
        assertEquals(app, wadl.getApplication());
    }

    @Test
    public void testGetSource() throws Exception {
        Application app = mock(Application.class);
        Wadl<String> wadl = new Wadl<String>(app, "test");
        assertEquals("test", wadl.getSource());
    }
}
