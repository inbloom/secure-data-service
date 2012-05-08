package org.slc.sli.unit.view;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.view.AttendanceAbsenceResolver;

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
        GenericEntity attendanceEvents = new GenericEntity();
        attendanceEvents.put("Excused Absence", 20.0);
        GenericEntity attendances = new GenericEntity();
        attendances.put("attendances", attendanceEvents);

        mockStudent.put("attendances", attendances);
        Field f = new Field();
        f.setValue("ATTENDANCE.AbsenceCount");
        
        assert (resolver.getCountForPath(f) == 20);
    }

    @Test
    public void testGetMixedCount() throws Exception {
        GenericEntity attendanceEvents = new GenericEntity();
        attendanceEvents.put("Excused Absence", 20.0);
        attendanceEvents.put("Tardy", 15.0);
        GenericEntity attendances = new GenericEntity();
        attendances.put("attendances", attendanceEvents);
        
        Field f = new Field();
        f.setValue("ATTENDANCE.AbsenceCount");
        mockStudent.put("attendances", attendances);
        assert (resolver.getCountForPath(f) == 20);
    }
    
    @Test
    public void testTardyCount() {
        GenericEntity attendanceEvents = new GenericEntity();
        attendanceEvents.put("Tardy", 17.0);
        GenericEntity attendances = new GenericEntity();
        attendances.put("attendances", attendanceEvents);
        
        mockStudent.put("attendances", attendances);
        Field f = new Field();
        f.setValue("ATTENDANCE.TardyCount");
        assert (resolver.getCountForPath(f) == 17);
    }
    
    @Test
    public void testTardyCountMixed() {
        GenericEntity attendanceEvents = new GenericEntity();
        attendanceEvents.put("Excused Absence", 20.0);
        attendanceEvents.put("Tardy", 15.0);
        GenericEntity attendances = new GenericEntity();
        attendances.put("attendances", attendanceEvents);
        
        Field f = new Field();
        f.setValue("ATTENDANCE.TardyCount");
        mockStudent.put("attendances", attendances);
        assert (resolver.getCountForPath(f) == 15);
    }

//    private Map getValidAttendanceObject() {
//        Map<String, String> attendance = new HashMap<String, String>();
//        attendance.put("attendanceEventCategory", "Excused Absence");
//        return attendance;
//    }
//
//    private Map getInvalidAttendanceObject() {
//        Map<String, String> attendance = new HashMap<String, String>();
//        attendance.put("attendanceEventCategory", "Tardy");
//        return attendance;
//    }
}
