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

package org.slc.sli.shtick;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Tests for {@link Entity}, treating it as an immutable POJO.
 */
public final class RestEntityTestCase extends TestCase {

    public void testInitialization() {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("someBoolean", Boolean.TRUE);
        data.put("someString", "Hello");
        data.put("id", "1234567890");

        final Entity student = new Entity("student", data);
        assertEquals("student", student.getType());
        assertEquals(data, student.getData());
        assertEquals(Boolean.TRUE, student.getData().get("someBoolean"));
        assertEquals("Hello", student.getData().get("someString"));
    }

    public void testEntityTypeIsRequired() {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("someBoolean", Boolean.TRUE);
        data.put("someString", "Hello");

        try {
            new Entity(null, data);
            fail("Expecting NPE for null type argument.");
        } catch (final NullPointerException e) {
            assertEquals("type", e.getMessage());
        }
    }

    public void testEntityDataIsRequired() {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("someBoolean", Boolean.TRUE);
        data.put("someString", "Hello");

        try {
            new Entity("student", null);
            fail("Expecting NPE for null data argument.");
        } catch (final NullPointerException e) {
            assertEquals("data", e.getMessage());
        }
    }

    public void testMakingDefensiveCopyOfArguments() {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("someBoolean", Boolean.TRUE);
        data.put("someString", "Hello");

        final Entity student = new Entity("student", data);
        assertFalse(data == student.getData());
    }

    // Ignoring this for now -- Data can be modified at the moment
    public void DataCannotBeChanged() {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("someBoolean", Boolean.TRUE);
        data.put("someString", "Hello");

        final Entity student = new Entity("student", data);
        final Map<String, Object> copyOfData = student.getData();
        try {
            copyOfData.put("someKey", "Hello");
            fail("data should not be modifiable.");
        } catch (final UnsupportedOperationException e) {
            // Expected
        }
    }

    public void testToString() {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("s", "123");

        final Entity student = new Entity("x", data);
        assertEquals("{type : x, data : {s=123}}", student.toString());
    }
}
