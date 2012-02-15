package org.slc.sli.validation;

import static org.junit.Assert.assertTrue;
import static org.slc.sli.validation.ValidationTestUtils.makeDummyEntity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;

/**
 * Test for validation of Student Section associations
 * 
 * @author nbrown
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentSectionAssociationTest {
    
    @Autowired
    private EntityValidator validator;
    
    @Autowired
    private DummyEntityRepository repo;
    
    @Before
    public void init() {
        repo.addEntity("student", "Calvin", makeDummyEntity("student", "Calvin"));
        repo.addEntity("section", "Math Class", makeDummyEntity("section", "Math Class"));
    }
    
    private Entity goodAssociation() throws ParseException {
        final Map<String, Object> goodSection = new HashMap<String, Object>();
        goodSection.put("studentId", "Calvin");
        goodSection.put("sectionId", "Math Class");
        goodSection.put("beginDate", "1985-11-18");
        goodSection.put("endDate", "1995-12-31");
        goodSection.put("homeroomIndicator", true);
        goodSection.put("repeatIdentifier", "Repeated, not counted in grade point average");
        List<Map<String, Object>> gradeBookEntries = new ArrayList<Map<String, Object>>();
        // TODO add actual gradebook entries
        goodSection.put("studentGradebookEntries", gradeBookEntries);
        List<Map<String, Object>> attendance = new ArrayList<Map<String, Object>>();
        Map<String, Object> tardyEvent = new HashMap<String, Object>();
        tardyEvent.put("eventDate", "1987-08-08");
        tardyEvent.put("attendanceEventType", "Section Attendance");
        tardyEvent.put("attendanceEventCategory", "Tardy");
        tardyEvent.put("attendanceEventReason", "Delayed by bed monster");
        tardyEvent.put("educationalEnvironment", "Classroom");
        Map<String, Object> sickEvent = new HashMap<String, Object>();
        sickEvent.put("eventDate", "1988-10-19");
        sickEvent.put("attendanceEventType", "Section Attendance");
        sickEvent.put("attendanceEventCategory", "Excused Absence");
        sickEvent.put("attendanceEventReason", "Sick");
        sickEvent.put("educationalEnvironment", "Classroom");
        attendance.add(tardyEvent);
        attendance.add(sickEvent);
        goodSection.put("studentAttendance", attendance);
        return new Entity() {
            
            @Override
            public String getType() {
                return "studentSectionAssociation";
            }
            
            @Override
            public String getEntityId() {
                return "id";
            }
            
            @Override
            public Map<String, Object> getBody() {
                return goodSection;
            }
            
            @Override
            public Map<String, Object> getMetaData() {
                return new HashMap<String, Object>();
            }
        };
    }
    
    @Test
    public void testGoodAssociation() throws ParseException {
        Entity goodAssociation = goodAssociation();
        assertTrue(validator.validate(goodAssociation));
    }
    
}
