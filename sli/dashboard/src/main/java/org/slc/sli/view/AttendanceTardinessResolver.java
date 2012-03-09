package org.slc.sli.view;

import java.util.List;
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
public class AttendanceTardinessResolver implements AggregateRatioResolver {

    private static Logger logger = LoggerFactory.getLogger(AttendanceAbsenceResolver.class);

    private GenericEntity student;

    public static final String CATEGORY = "attendanceEventCategory";

    public AttendanceTardinessResolver(GenericEntity student) {
        this.student = student;
    }

    public AttendanceTardinessResolver() {
    }

    public void setStudent(GenericEntity student) {
        this.student = student;
    }

    @Override
    public int getTotalCountForPath(Field configField) {
        // TODO: This should be a lot more generic.
        List<Map> attendances = student.getList(Constants.ATTR_STUDENT_ATTENDANCES);
        int count = 0;
        if (attendances != null)
            for (Map attendance : attendances) {
                String value = (String) attendance.get(CATEGORY);
                if (!value.contains("Absence")) {
                    ++count;
                }
            }
        return count;
    }

    @Override
    public int getSubCountForPath(Field configField) {
        List<Map> attendances = student.getList(Constants.ATTR_STUDENT_ATTENDANCES);
        int count = 0;
        if (attendances != null)
            for (Map attendance : attendances) {
                String value = (String) attendance.get(CATEGORY);
                if (value.contains("Tardy")) {
                    ++count;
                }
            }
        return count;
    }

}
