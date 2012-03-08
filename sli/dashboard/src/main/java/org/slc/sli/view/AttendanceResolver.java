package org.slc.sli.view;

import org.slc.sli.entity.GenericEntity;

import java.util.Map;

/**
 * A class to aggregate all of the ways to deal with attendance data.
 */
public class AttendanceResolver {

    public AttendanceResolver() {
    }

    public AggregateResolver getAbscenceCountResolverForStudent(Map student) {
        GenericEntity geStudent = new GenericEntity(student);
        return new AttendanceAbsenceResolver(geStudent);
    }
    
    public AggregateResolver getTardyCountResolverForStudent(Map student) {
        GenericEntity geStudent = new GenericEntity(student);
        return new AttendanceAbsenceResolver(geStudent);
    }
}
