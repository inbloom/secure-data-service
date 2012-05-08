package org.slc.sli.view;

import java.util.Map;

import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author pwolf
 *
 */
public class AttendanceRateResolver implements AggregateRatioResolver {

    private static Logger logger = LoggerFactory.getLogger(AttendanceAbsenceResolver.class);

    private GenericEntity student;

    public static final String CATEGORY = "attendanceEventCategory";
    private static final String ATTENDANCE_RATE = "ATTENDANCE.AttendanceRate";
    private static final String TARDY_RATE = "ATTENDANCE.TardyRate";

    public AttendanceRateResolver(GenericEntity student) {
        this.student = student;
    }

    public AttendanceRateResolver() {
    }

    public void setStudent(GenericEntity student) {
        this.student = student;
    }

//    @Override
//    public int getSize(Field configField) {
//        List<Map> attendances = student.getList(Constants.ATTR_STUDENT_ATTENDANCES);
//        if (attendances == null)
//            return 0;
//        return attendances.size();
//    }

    @SuppressWarnings("unchecked")
    @Override
    public int getSize(Field configField) {
        Map<String, Object> attendances = (Map<String, Object>) student.get(Constants.ATTR_STUDENT_ATTENDANCES);
        if (attendances == null)
            return 0;

        Map<String, Object> attendanceEvents = (Map<String, Object>) attendances.get(Constants.ATTR_STUDENT_ATTENDANCES);
        if (attendanceEvents == null)
            return 0;
        
        if (attendanceEvents.get("Total") == null)
            return 0;

        return (int) ((Double) attendanceEvents.get("Total")).doubleValue();
    }

//    @Override
//    public int getCountForPath(Field configField) {
//        List<Map> attendances = student.getList(Constants.ATTR_STUDENT_ATTENDANCES);
//        int count = 0;
//        if (attendances != null)
//            if (configField.getValue().equals(ATTENDANCE_RATE)) {
//                return getAttendanceCount(attendances);
//            } else if (configField.getValue().equals(TARDY_RATE)) {
//                return getTardyCount(attendances);
//            }
//        assert false;   //should never get here unless it's an known field value
//        return 0;
//    }

    @SuppressWarnings("unchecked")
    @Override
    public int getCountForPath(Field configField) {
        Map<String, Object> attendances = (Map<String, Object>) student.get(Constants.ATTR_STUDENT_ATTENDANCES);
        
        if (attendances != null) {
            Map<String, Object> attendanceEvents = (Map<String, Object>) attendances.get(Constants.ATTR_STUDENT_ATTENDANCES);
            if (attendanceEvents != null) {
                if (configField.getValue().equals(ATTENDANCE_RATE)) {
                    return getAttendanceCount(attendanceEvents);
                } else if (configField.getValue().equals(TARDY_RATE)) {
                    return getTardyCount(attendanceEvents);
                }
            }
        }
        assert false;   //should never get here unless it's an known field value
        return 0;
    }
    
//    private int getAttendanceCount(List<Map> attendances) {
//        int count = attendances.size();
//        for (Map attendance : attendances) {
//            String value = (String) attendance.get(CATEGORY);
//            if (value.contains("Absence")) {
//                count--;
//            }
//        }
//        return count;
//    }

    private int getAttendanceCount(Map<String, Object> attendances) {
        if (attendances.get("Total") == null) return 0;
        
        int count = (int) ((Double) attendances.get("Total")).doubleValue();
        int absenceCount = 0;
        
        for (Map.Entry<String, Object> e : attendances.entrySet()) {
            if (e.getKey().contains("Absence")) {
                if (e.getValue() != null)
                    absenceCount += (Double) e.getValue();
            }
        }

        return count - absenceCount;
    }

//    private int getTardyCount(List<Map> attendances) {
//        int count = 0;
//        for (Map attendance : attendances) {
//            String value = (String) attendance.get(CATEGORY);
//            if (value.contains("Tardy")) {
//                count++;
//            }
//        }
//        return count;
//    }

    private int getTardyCount(Map<String, Object> attendances) {
        if (attendances.get("Tardy") == null) {
            return 0;
        } else {        
            return (int) ((Double) attendances.get("Tardy")).doubleValue();
        }
    }

    @Override
    public int[] getCutoffPoints(Field field) {

        if (field.getValue().equals(TARDY_RATE)) {
            return new int[] {10, 5, 1};
        } else if (field.getValue().equals(ATTENDANCE_RATE)) {
            return new int[] {90, 95, 99};
        }
        return new int[] {};
        
    }

}
