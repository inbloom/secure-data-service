package org.slc.sli.dal.lifecycle;

import static org.junit.Assert.fail;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests for ModelEntity
 * @author vmcglaughlin
 *
 */
public class ModelEntityTest {

    @Test
    public void testGetMapper() {
        Assert.assertNotNull("ModelEntity mapper should not be null", ModelEntity.getMapper());
    }

    @Test
    public void testFromJson() {
        String json = "{\"type\" : \"test\",\n"
                + "\"id\" : \"1234567890\",\n"
                + "\"body\" : { \"key1\" : \"value1\", \"key2\" : \"value2\", \"key3\" : \"value3\" },\n"
                + "\"metaData\" : { \"key4\" : \"value4\", \"key5\" : \"value5\", \"key6\" : \"value6\" } }";

        ModelEntity me = ModelEntity.fromJson(json);

        Assert.assertEquals("type should be 'test'", "test", me.getType());
        Assert.assertEquals("Id should be '1234567890'", "1234567890", (String) me.getId());
        Map<String, Object> body = me.getBody();
        Assert.assertEquals("Body should have 3 keys", 3, body.size());
        Assert.assertEquals("Value for key1 should be 'value1'", "value1", (String) body.get("key1"));
        Assert.assertEquals("Value for key1 should be 'value2'", "value2", (String) body.get("key2"));
        Assert.assertEquals("Value for key1 should be 'value3'", "value3", (String) body.get("key3"));
        Map<String, Object> metaData = me.getMetaData();
        Assert.assertEquals("MetaData should have 3 keys", 3, metaData.size());
        Assert.assertEquals("Value for key4 should be 'value4'", "value4", (String) metaData.get("key4"));
        Assert.assertEquals("Value for key5 should be 'value5'", "value5", (String) metaData.get("key5"));
        Assert.assertEquals("Value for key6 should be 'value6'", "value6", (String) metaData.get("key6"));
    }

    @Test
    public void testGetEnumFromString() {
        fail("Not yet implemented");
    }

    @Test
    public void testModelEntity() {
        fail("Not yet implemented");
    }

    @Test
    public void testModelEntityString() {
        fail("Not yet implemented");
    }

    @Test
    public void testModelEntityStringMapOfStringObject() {
        fail("Not yet implemented");
    }

    @Test
    public void testModelEntityStringObjectMapOfStringObjectMapOfStringObjectMapOfStringStringModelLifecycle() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetType() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetType() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetId() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetId() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetBody() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetBody() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetMetaData() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetMetaData() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetKeys() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetKeysMapOfStringString() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetKeysString() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetKeysStringStringArray() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetLifecycle() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetLifecycle() {
        fail("Not yet implemented");
    }

    @Test
    public void testEqualsObject() {
        fail("Not yet implemented");
    }

    @Test
    public void testToString() {
        fail("Not yet implemented");
    }

    @Test
    public void testToJson() {
        fail("Not yet implemented");
    }

    @Test
    public void testToXml() {
        fail("Not yet implemented");
    }

}
