package org.slc.sli.view;

import org.slc.sli.entity.GenericEntity;


/**
 * Class to handle the oddities of getting absences from attendance objects.
 */
public class AttendanceAbsenceResolver implements AggregateResolver {

    private GenericEntity student;
    
    private final String compareValue = "abscence";

    public AttendanceAbsenceResolver(GenericEntity student) {
        this.student = student;
    }

    public AttendanceAbsenceResolver() {
    }

    public void setStudent(GenericEntity student) {
        this.student = student;
    }

    @Override
    public int getCountForPath(String path) {
        return 0;
    }
}
