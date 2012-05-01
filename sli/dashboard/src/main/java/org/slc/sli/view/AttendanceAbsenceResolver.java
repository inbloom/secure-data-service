package org.slc.sli.view;

import org.slc.sli.config.Field;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;

import java.util.List;
import java.util.Map;

/**
 * Class to handle the oddities of getting absences from attendance objects.
 */
public class AttendanceAbsenceResolver implements AggregateResolver {
    private GenericEntity student;

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

    @Override
    public int getCountForPath(Field configField) {
        // TODO: This should be a lot more generic.
        Map<String, Object> attendanceBody = (Map<String, Object>) student.get(Constants.ATTR_STUDENT_ATTENDANCES);
        List<Map<String, Object>> attendances = null;
        if( attendanceBody != null ) {
            attendances = (List<Map<String, Object>>) attendanceBody.get(Constants.ATTR_STUDENT_ATTENDANCES);
        }
        int count = 0;
        if (attendances != null) {
            for (Map attendance : attendances) {
                String value = (String) attendance.get(Constants.ATTR_ATTENDANCE_EVENT_CATEGORY);
                String compareValue = null;
                if (configField.getValue().equals(TARDY_VALUE)) {
                    compareValue = "Tardy";
                } else if (configField.getValue().equals(ABSENT_VALUE)) {
                    compareValue = "Absence";
                }
                if (compareValue != null && value.contains(compareValue)) {
                    ++count;
                }
            }
        }
        return count;
    }

    @Override
    public int[] getCutoffPoints(Field field) {
        return new int[]{0, 5, 10, 11};
    }
}
