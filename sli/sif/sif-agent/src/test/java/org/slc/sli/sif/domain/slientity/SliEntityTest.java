package org.slc.sli.sif.domain.slientity;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.slc.sli.api.client.impl.GenericEntity;

public class SliEntityTest {

    //test classes
    class TestSubObject {
        public String subFieldA = "A";
        public String subFieldB = "B";
    }

    class TestSliEntity extends SliEntity {

        public String fieldOne = "1";
        public TestSubObject testSubObject;

        TestSliEntity() {
            testSubObject = new TestSubObject();
        }

        @Override
        public String entityType() {
            return "TestEntity";
        }
    };

    SliEntity testSliEntity;

    @Test
    public void shouldTransformConcreteBodyToMap() {
        testSliEntity = new TestSliEntity();
        Map<String, Object> body = testSliEntity.createBody();

        Assert.assertEquals("Body should contain 2 elements", 2, body.size());
        Assert.assertEquals("Incorrect map element", "1", body.get("fieldOne"));

        @SuppressWarnings("unchecked")
        Map<String, Object> subObjectMap = (Map<String, Object>) body.get("testSubObject");
        Assert.assertNotNull("SubObject map null", subObjectMap);
        Assert.assertEquals("subObject should contain 2 elements", 2, subObjectMap.size());
        Assert.assertEquals("Incorrect map element", "A", subObjectMap.get("subFieldA"));
        Assert.assertEquals("Incorrect map element", "B", subObjectMap.get("subFieldB"));
    }

    @Test
    public void shouldCreateGenericEntity() {
        testSliEntity = new TestSliEntity();
        GenericEntity entity = testSliEntity.createGenericEntity();
        Assert.assertNotNull("GenericEntity null", entity);
        Assert.assertNotNull("Entity body null", entity.getData());
        Assert.assertEquals("Incorrect entity type", "TestEntity", entity.getEntityType());
    }
}
