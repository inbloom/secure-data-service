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

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * @author jstokes
 */
public class PsmConfigTest {

    @Test
    public void testGetDocuments() {
        final List<PsmDocument<String>> documents = new ArrayList<PsmDocument<String>>();
        final PsmConfig<String> config = new PsmConfig<String>(documents);

        assertEquals(config.getDocuments(), documents);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowsNull() {
        final PsmConfig<String> config = new PsmConfig<String>(null);
    }
}
