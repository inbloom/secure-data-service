package org.slc.sli.unit.view;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.view.AttendanceAbsenceResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test for the AttendanceAbsenceResolverTest
 * 
 */
public class AttendanceAbsenceResolverTest {
    
    private AttendanceAbsenceResolver resolver;
    private GenericEntity mockStudent;
    
    @Before
    public void setUp() throws Exception {
        resolver = new AttendanceAbsenceResolver();
        mockStudent = new GenericEntity();
        resolver.setStudent(mockStudent);
    }

    @After
    public void tearDown() throws Exception {
        resolver = null;
        mockStudent = null;
    }

    @Test
    public void testGetCountForPath() throws Exception {
        List<Map> attendances = new ArrayList<Map>();
        for (int i = 0; i < 20; ++i) {
            attendances.add(getValidAttendanceObject());  
        }
        mockStudent.put("attendances", attendances);
        assert (resolver.getCountForPath(new Field()) == 20);
    }

    @Test
    public void testGetMixedCount() throws Exception {
        List<Map> attendances = new ArrayList<Map>();
        for (int i = 0; i < 20; ++i) {
            attendances.add(getValidAttendanceObject());
        }
        for (int i = 0; i < 15; ++i) {
            attendances.add(getInvalidAttendanceObject());
        }
        mockStudent.put("attendances", attendances);
        assert (resolver.getCountForPath(new Field()) == 20);
    }

    private Map getValidAttendanceObject() {
        Map<String, String> attendance = new HashMap<String, String>();
        attendance.put("attendanceEventCategory", "Excused Absence");
        return attendance;
    }

    private Map getInvalidAttendanceObject() {
        Map<String, String> attendance = new HashMap<String, String>();
        attendance.put("attendanceEventCategory", "Tardy");
        return attendance;
    }
}
