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


package org.slc.sli.modeling.uml;

import junit.framework.TestCase;

/**
 * Tests for {@link Identifier}.
 */
public final class IdentifierTestCase extends TestCase {
    
    public void testInitializationFromRandom() {
        final Identifier id = Identifier.random();
        final Identifier idCopy = Identifier.fromString(id.toString());
        assertEquals(id, idCopy);
    }
    
    /**
     * Verify that the identifier can also hold plain strings.
     */
    public void testInitializationFromPlainString() {
        final Identifier id = Identifier.fromString("1234");
        assertEquals("1234", id.toString());
    }
}
