package org.slc.sli.shtick;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Tests for {@link RestEntity}, treating it as an immutable POJO.
 */
public final class RestEntityTestCase extends TestCase {

    public void testInitialization() {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("someBoolean", Boolean.TRUE);
        data.put("someString", "Hello");
        data.put("id", "1234567890");

        final RestEntity student = new RestEntity("student", data);
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
            new RestEntity(null, data);
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
            new RestEntity("student", null);
            fail("Expecting NPE for null data argument.");
        } catch (final NullPointerException e) {
            assertEquals("data", e.getMessage());
        }
    }

    /**
     * FIXME: We're only going to the first level here.
     */
    public void testMakingDefensiveCopyOfArguments() {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("someBoolean", Boolean.TRUE);
        data.put("someString", "Hello");

        final RestEntity student = new RestEntity("student", data);
        assertFalse(data == student.getData());
    }

    public void testDataCannotBeChanged() {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("someBoolean", Boolean.TRUE);
        data.put("someString", "Hello");

        final RestEntity student = new RestEntity("student", data);
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

        final RestEntity student = new RestEntity("x", data);
        assertEquals("{type : x, data : {s=123}}", student.toString());
    }
}
