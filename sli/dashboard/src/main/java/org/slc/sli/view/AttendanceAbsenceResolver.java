package org.slc.sli.view;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;

/**
 * Class to handle the oddities of getting absences from attendance objects.
 */
public class AttendanceAbsenceResolver implements AggregateResolver {
    private static Logger logger = LoggerFactory.getLogger(AttendanceAbsenceResolver.class);
    

    private GenericEntity student;
    
    public static final String CATEGORY = "attendanceEventCategory";
    public static final String TARDY_VALUE = "ATTENDANCE.TardyCount";
    public static final String ABSENT_VALUE = "ATTENDANCE.AbsenceCount";

    public AttendanceAbsenceResolver(GenericEntity student) {
        this.student = student;
    }

    public AttendanceAbsenceResolver() {
    }

    public void setStudent(GenericEntity student) {
        this.student = student;
    }

//    @Override
//    public int getCountForPath(Field configField) {
//        // TODO: This should be a lot more generic.
//        List<Map> attendances = student.getList(Constants.ATTR_STUDENT_ATTENDANCES);
//        int count = 0;
//        if (attendances != null) {
//            for (Map attendance : attendances) {
//                logger.debug("Attendance: {}", attendance);
//                String value = (String) attendance.get(CATEGORY);
//                String compareValue = null;
//                if (configField.getValue().equals(TARDY_VALUE)) {
//                    compareValue = "Tardy";
//                } else if (configField.getValue().equals(ABSENT_VALUE)) {
//                    compareValue = "Absence";
//                }
//                if (compareValue != null && value.contains(compareValue)) {
//                    ++count;
//                }
//            }
//        }
//        return count;
//    }


    @SuppressWarnings("unchecked")
    @Override
    public int getCountForPath(Field configField) {
        Map<String, Object> attendance = (Map<String, Object>) student.get(Constants.ATTR_STUDENT_ATTENDANCES);

        double count = 0;

        if (attendance != null) {
            logger.debug("Attendance: {}", attendance);
            Map<String, Object> attendanceEvents = (Map<String, Object>) attendance.get(Constants.ATTR_STUDENT_ATTENDANCES);

            if (attendanceEvents != null) {

                if (configField.getValue().equals(TARDY_VALUE)) {
                    if (attendanceEvents.get("Tardy") != null) {
                        count = (Double) attendanceEvents.get("Tardy");
                    }
                } else if (configField.getValue().equals(ABSENT_VALUE)) {
                    for (Map.Entry<String, Object> e : attendanceEvents.entrySet()) {
                        if (e.getKey().contains("Absence")) {
                            if (e.getValue() != null)
                                count += (Double) e.getValue();
                        }
                    }
                }

            }
        }

        return (int) count;
    }
    
    @Override
    public int[] getCutoffPoints(Field field) {
        return new int[]{0, 5, 10, 11};
    }
}
