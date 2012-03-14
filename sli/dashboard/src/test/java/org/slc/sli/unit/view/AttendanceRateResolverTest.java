package org.slc.sli.unit.view;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.view.AttendanceRateResolver;

/**
 * 
 * @author pwolf
 *
 */
public class AttendanceRateResolverTest {
    
    private AttendanceRateResolver resolver;
    private GenericEntity mockStudent;
    
    private static final String IN_ATTENDANCE = "In Attendance";
    private static final String TARDY = "Tardy";
    private static final String EXCUSED = "Excused Absence";
    private Field tardyField = null;
    private Field attendanceField = null;
    
    
    @Before
    public void setUp() throws Exception {
        resolver = new AttendanceRateResolver();
        mockStudent = new GenericEntity();
        resolver.setStudent(mockStudent);
        
        tardyField = new Field();
        tardyField.setValue("ATTENDANCE.TardyRate");
        
        attendanceField = new Field();
        attendanceField.setValue("ATTENDANCE.AttendanceRate");
    }
    
    @Test
    public void testNoTardy() {
        List<Map<String, String>> attendances = new ArrayList<Map<String, String>>();
        for (int i = 0; i < 10; ++i) {
            attendances.add(getAttendanceObject(IN_ATTENDANCE));
        }
        mockStudent.put("attendances", attendances);
        assertEquals(0, resolver.getCountForPath(tardyField));
        assertEquals(10, resolver.getSize(tardyField));
    }
    
    @Test
    public void testNoTardyMix() {
        List<Map<String, String>> attendances = new ArrayList<Map<String, String>>();
        for (int i = 0; i < 10; ++i) {
            attendances.add(getAttendanceObject(IN_ATTENDANCE));
        }
        for (int i = 0; i < 10; ++i) {
            attendances.add(getAttendanceObject(EXCUSED));
        }
        mockStudent.put("attendances", attendances);
        assertEquals(0, resolver.getCountForPath(tardyField));
        assertEquals(20, resolver.getSize(tardyField));
    }
    
    @Test
    public void testSomeTardy() {
        List<Map<String, String>> attendances = new ArrayList<Map<String, String>>();
        for (int i = 0; i < 10; ++i) {
            attendances.add(getAttendanceObject(IN_ATTENDANCE));
        }
        for (int i = 0; i < 10; ++i) {
            attendances.add(getAttendanceObject(EXCUSED));
        }
        for (int i = 0; i < 5; ++i) {
            attendances.add(getAttendanceObject(TARDY));
        }
        mockStudent.put("attendances", attendances);
        assertEquals(5, resolver.getCountForPath(tardyField));
        assertEquals(25, resolver.getSize(tardyField));
        
        assertEquals(10, resolver.getCountForPath(attendanceField));
        assertEquals(25, resolver.getSize(attendanceField));
    }
    
    @Test
    public void testPerfectAttendance() {
        List<Map<String, String>> attendances = new ArrayList<Map<String, String>>();
        for (int i = 0; i < 10; ++i) {
            attendances.add(getAttendanceObject(IN_ATTENDANCE));
        }
        mockStudent.put("attendances", attendances);
        assertEquals(10, resolver.getCountForPath(attendanceField));
        assertEquals(10, resolver.getSize(attendanceField));
    }
    
    @Test
    public void testBadAttendance() {
        List<Map<String, String>> attendances = new ArrayList<Map<String, String>>();
        for (int i = 0; i < 10; ++i) {
            attendances.add(getAttendanceObject(EXCUSED));
        }
        mockStudent.put("attendances", attendances);
        assertEquals(0, resolver.getCountForPath(attendanceField));
        assertEquals(10, resolver.getSize(attendanceField));
    }
    
    @Test
    public void testHasCutoffPoints() {
        assertNotNull(resolver.getCutoffPoints(tardyField));
        assertNotNull(resolver.getCutoffPoints(attendanceField));
    }
    
    @Test
    public void testNoAttendance() {
        assertEquals(0, resolver.getSize(tardyField));
    }
    
    @Test
    public void testUnknownField() {
        Field field = new Field();
        field.setValue("Hello");
        assertEquals(0, resolver.getCutoffPoints(field).length);
    }
    
    private Map<String, String> getAttendanceObject(String type) {
        Map<String, String> attendance = new HashMap<String, String>();
        attendance.put("attendanceEventCategory", type);
        return attendance;
    }


}
