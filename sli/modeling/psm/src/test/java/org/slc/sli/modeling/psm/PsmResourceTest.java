/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.modeling.psm;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author jstokes
 */
public class PsmResourceTest {

    @Test
    public void testGetName() {
        final PsmResource resource = new PsmResource("test");
        assertEquals("test", resource.getName());
        assertEquals("test", resource.toString());
    }

    @Test
    public void testTrim() {
        final PsmResource resource = new PsmResource("test     ");
        assertEquals("test", resource.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsNullPointer() {
        final PsmResource resource = new PsmResource(null);
    }
}

