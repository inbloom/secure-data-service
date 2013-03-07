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
package org.slc.sli.api.resources.generic.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 *
 * Unit Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class CustomEntityDecoratorTest {

    @Autowired
    private CustomEntityDecorator customEntityDecorator;

    @Test
    public void testDecoratorNonParam() {
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add(ParameterConstants.INCLUDE_CUSTOM, String.valueOf(false));

        EntityBody body = createTestEntity();
        customEntityDecorator.decorate(body, null, map);
        assertEquals("Should match", 3, body.keySet().size());
        assertEquals("Should match", "Male", body.get("sex"));
        assertEquals("Should match", 1234, body.get("studentUniqueStateId"));
    }

    @Test
    public void testDecoratorWithParam() {
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add(ParameterConstants.INCLUDE_CUSTOM, String.valueOf(true));

        EntityBody custom = createCustomEntity();

        EntityService service = mock(EntityService.class);
        when(service.getCustom("1234")).thenReturn(custom);

        EntityDefinition definition = mock(EntityDefinition.class);
        when(definition.getService()).thenReturn(service);

        EntityBody body = createTestEntity();
        customEntityDecorator.decorate(body, definition, map);
        assertEquals("Should match", 4, body.keySet().size());
        assertEquals("Should match", "Male", body.get("sex"));
        assertEquals("Should match", 1234, body.get("studentUniqueStateId"));

        EntityBody customBody = (EntityBody) body.get(ResourceConstants.CUSTOM);
        assertNotNull("Should not be null", customBody);
        assertEquals("Should match", "1", customBody.get("customValue"));
    }

    private EntityBody createTestEntity() {
        EntityBody entity = new EntityBody();
        entity.put("id", "1234");
        entity.put("sex", "Male");
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    private EntityBody createCustomEntity() {
        EntityBody entity = new EntityBody();
        entity.put("customValue", "1");
        return entity;
    }

}
