package org.slc.sli.api.resources.v1.view.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.view.OptionalFieldAppender;
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
    
    @Autowired
    private OptionalFieldAppender studentAttendanceOptionalFieldStrategy;
    
    @Test
    public void testApplyOptionalField() {
        List<EntityBody> entities = new ArrayList<EntityBody>();
        
        entities = studentAttendanceOptionalFieldStrategy.applyOptionalField(entities);
        
        //test should be updated as code is put in
        assertEquals("Should be 1", 1, entities.size());
        assertEquals("Should match", "studentattendances", entities.get(0).get("attendances"));
    }
}
