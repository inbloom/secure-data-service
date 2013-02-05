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

package org.slc.sli.api.resources.v1.view;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Unit Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class OptionalViewTest {

    @Autowired
    private OptionalView optionalView;

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testAppendOptionalFieldsNoOptionsGiven() {
        MultivaluedMap map = new MultivaluedMapImpl();

        EntityBody body = new EntityBody();
        body.put("student", "{\"somekey\":\"somevalue\"}");

        List<EntityBody> entities = new ArrayList<EntityBody>();
        entities.add(body);

        entities = optionalView.add(entities, ResourceNames.SECTIONS, map);

        assertEquals("Should only have one", 1, entities.size());
        assertEquals("Should match", body, entities.get(0));
    }

    @Test
    public void testExtractOptionalFieldParams() {
        Map<String, String> values = optionalView.extractOptionalFieldParams("attendances.1");
        assertEquals("Should match", "attendances", values.get(OptionalFieldAppenderFactory.APPENDER_PREFIX));
        assertEquals("Should match", "1", values.get(OptionalFieldAppenderFactory.PARAM_PREFIX));

        values = optionalView.extractOptionalFieldParams("attendances");
        assertEquals("Should match", "attendances", values.get(OptionalFieldAppenderFactory.APPENDER_PREFIX));
        assertEquals("Should match", null, values.get(OptionalFieldAppenderFactory.PARAM_PREFIX));

        values = optionalView.extractOptionalFieldParams("attendances.1.2.3");
        assertEquals("Should match", "attendances", values.get(OptionalFieldAppenderFactory.APPENDER_PREFIX));
        assertEquals("Should match", "1", values.get(OptionalFieldAppenderFactory.PARAM_PREFIX));

        values = optionalView.extractOptionalFieldParams("attendances%1");
        assertEquals("Should match", "attendances%1", values.get(OptionalFieldAppenderFactory.APPENDER_PREFIX));
        assertEquals("Should match", null, values.get(OptionalFieldAppenderFactory.PARAM_PREFIX));

        values = optionalView.extractOptionalFieldParams("attendances.someparam");
        assertEquals("Should match", "attendances", values.get(OptionalFieldAppenderFactory.APPENDER_PREFIX));
        assertEquals("Should match", "someparam", values.get(OptionalFieldAppenderFactory.PARAM_PREFIX));
    }
}
