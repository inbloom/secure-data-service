package org.slc.sli.api.resources.v1.view.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.mongodb.CommandResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentAttendanceOptionalFieldAppenderTest {
    private static final String COLLECTION = "tempCollection";
    private static final String STUDENT_ID = "1234";
    
    @Autowired
    private StudentAttendanceOptionalFieldAppender studentAttendanceOptionalFieldAppender;

    private StudentAttendanceOptionalFieldAppender mockAppender;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        List<Object> attendances = buildAttendances();

        CommandResult result = mock(CommandResult.class);
        when(result.ok()).thenReturn(true);

        MongoTemplate template = mock(MongoTemplate.class);
        when(template.findAll(Object.class, COLLECTION)).thenReturn(attendances);
        when(template.executeCommand(anyString())).thenReturn(result);

        mockAppender = spy(studentAttendanceOptionalFieldAppender);
        when(mockAppender.buildCollectionName()).thenReturn(COLLECTION);

        mockAppender.setTemplate(template);
    }

    @Test
    public void testApplyOptionalField() {
        List<EntityBody> entities = new ArrayList<EntityBody>();
        entities.add(new EntityBody(createTestStudentEntity(STUDENT_ID)));
        
        entities = mockAppender.applyOptionalField(entities);
        
        //test should be updated as code is put in
        assertEquals("Should be 1", 1, entities.size());
        assertNotNull("Should not be null", entities.get(0).get("attendances"));

        EntityBody body = (EntityBody) ((EntityBody) entities.get(0).get("attendances")).get("attendances");
        assertNotNull("Should not be null", body);
        assertEquals("Should match", 10, body.get("In attendance"));
        assertEquals("Should match", 5, body.get("Tardy"));
    }

    @Test
    public void testBuildCommand() {
        Set<String> sectionIds = new HashSet<String>();
        sectionIds.add("1234");
        sectionIds.add("9999");

        String command = mockAppender.buildCommand(COLLECTION, sectionIds);

        assertTrue("Should have the sectionIds", command.contains("[\"1234\",\"9999\"]"));
        assertTrue("Should have the collection", command.contains(COLLECTION));
    }

    private Map<String, Object> createTestStudentEntity(String id) {
        Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("id", id);
        entity.put("field2", 2);
        entity.put("studentUniqueStateId", 1234);
        return entity;
    }

    private List<Object> buildAttendances() {
        List<Object> list = new ArrayList<Object>();

        list.add(buildAttendaceBody("1234"));
        list.add(buildAttendaceBody("5678"));
        list.add(buildAttendaceBody("9999"));

        return list;
    }

    private EntityBody buildAttendaceBody(String studentId) {
        EntityBody body = new EntityBody();
        body.put("_id", studentId);

        EntityBody att = new EntityBody();
        att.put("In attendance", 10);
        att.put("Tardy", 5);

        EntityBody value = new EntityBody();
        value.put("attendance", att);

        body.put("value", value);

        return body;
    }
}
