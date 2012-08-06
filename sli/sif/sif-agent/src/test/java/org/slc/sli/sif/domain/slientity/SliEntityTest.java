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
