package org.slc.sli.view;

import java.util.List;
import java.util.Map;

import org.slc.sli.entity.GenericEntity;

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
        
    public AggregateRatioResolver getAttendanceRateResolverForStudent(Map student) {
        GenericEntity geStudent = new GenericEntity(student);
        return new AttendanceRateResolver(geStudent);
    }

}
