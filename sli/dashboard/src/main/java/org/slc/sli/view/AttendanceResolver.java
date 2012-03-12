package org.slc.sli.view;

import org.slc.sli.entity.GenericEntity;

import java.util.List;
import java.util.Map;

/**
 * A class to aggregate all of the ways to deal with attendance data.
 */
public class AttendanceResolver {

    private List<GenericEntity> attendances;

    public AttendanceResolver() {
    }

    public AggregateResolver getAttendanceCountResolverForStudent(Map student) {
        GenericEntity geStudent = new GenericEntity(student);
        return new AttendanceAbsenceResolver(geStudent);
    }
    
    public AggregateRatioResolver getTardinessResolverForStudent(Map student) {
        GenericEntity geStudent = new GenericEntity(student);
        return new AttendanceTardinessResolver(geStudent);
    }
}
