package org.slc.sli.unit.view;

import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.view.AttendanceTardinessResolver;

/**
 * 
 * @author pwolf
 *
 */
public class AttendanceTardinessResolverTest {
    
    private AttendanceTardinessResolver resolver;
    private GenericEntity mockStudent;
    
    private static final String IN_ATTENDANCE = "In Attendance";
    private static final String TARDY = "Tardy";
    private static final String EXCUSED = "Excused Absence";
    
    
    @Before
    public void setUp() throws Exception {
        resolver = new AttendanceTardinessResolver();
        mockStudent = new GenericEntity();
        resolver.setStudent(mockStudent);
    }
    
    @Test
    public void testNoTardy() {
        List<Map<String, String>> attendances = new ArrayList<Map<String, String>>();
        for (int i = 0; i < 10; ++i) {
            attendances.add(getAttendanceObject(IN_ATTENDANCE));
        }
        mockStudent.put("attendances", attendances);
        assertEquals(0, resolver.getCountForPath(new Field()));
        assertEquals(10, resolver.getSize(new Field()));
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
        assertEquals(0, resolver.getCountForPath(new Field()));
        assertEquals(20, resolver.getSize(new Field()));
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
        assertEquals(5, resolver.getCountForPath(new Field()));
        assertEquals(25, resolver.getSize(new Field()));
    }
    
    private Map<String, String> getAttendanceObject(String type) {
        Map<String, String> attendance = new HashMap<String, String>();
        attendance.put("attendanceEventCategory", type);
        return attendance;
    }


}
