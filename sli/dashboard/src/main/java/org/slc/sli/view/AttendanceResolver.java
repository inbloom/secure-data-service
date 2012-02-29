package org.slc.sli.view;

import org.slc.sli.entity.GenericEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class to aggregate all of the ways to deal with attendance data.
 */
public class AttendanceResolver {
    
    List<GenericEntity> studentSummaries;
    List<GenericEntity> attendance;
    
    public AttendanceResolver(List<GenericEntity> studentSummaries, List<GenericEntity> attendance) {
        this.studentSummaries = studentSummaries;
        this.attendance = attendance;
    }

    public AttendanceResolver() {
    }

    public void setStudentSummaries(List<GenericEntity> studentSummaries) {
        this.studentSummaries = studentSummaries;
    }

    public void setAttendance(List<GenericEntity> attendance) {
        this.attendance = attendance;
    }
    
    public AggregateResolver getAbscenceCountResolverForStudent(GenericEntity student) {
        return null;
    }
}
