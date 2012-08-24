package org.slc.sli.api.resources.generic.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.CalculatedData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * Unit Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class AggregateEntityDecoratorTest {

    @Autowired
    private AggregateEntityDecorator aggregateEntityDecorator;

    @Test
    public void testDecoratorNonParam() {
        MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
        map.add(ParameterConstants.INCLUDE_AGGREGATES, String.valueOf(false));

        EntityDefinition definition = mock(EntityDefinition.class);
        when(definition.supportsAggregates()).thenReturn(true);

        EntityBody body = createTestEntity();
        aggregateEntityDecorator.decorate(body, definition, map);
        assertEquals("Should match", 3, body.keySet().size());
        assertEquals("Should match", "Male", body.get("sex"));
        assertEquals("Should match", 1234, body.get("studentUniqueStateId"));
    }

    @Test
    public void testDecoratorWithParam() {
        MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
        map.add(ParameterConstants.INCLUDE_AGGREGATES, String.valueOf(true));

        CalculatedData<Map<String, Integer>> aggregates = mock(CalculatedData.class);

        EntityService service = mock(EntityService.class);
        when(service.getAggregates("1234")).thenReturn(aggregates);

        EntityDefinition definition = mock(EntityDefinition.class);
        when(definition.getService()).thenReturn(service);
        when(definition.supportsAggregates()).thenReturn(true);

        EntityBody body = createTestEntity();
        aggregateEntityDecorator.decorate(body, definition, map);
        assertEquals("Should match", 4, body.keySet().size());
        assertEquals("Should match", "Male", body.get("sex"));
        assertEquals("Should match", 1234, body.get("studentUniqueStateId"));
        assertNotNull("Should not be null", body.get(ResourceConstants.AGGREGATE_VALUE_TYPE));
    }

    private EntityBody createTestEntity() {
        EntityBody entity = new EntityBody();
        entity.put("id", "1234");
        entity.put("sex", "Male");
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }
}
