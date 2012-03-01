package org.slc.sli.view;

import org.slc.sli.entity.GenericEntity;

/**
 * A class to aggregate all of the ways to deal with attendance data.
 */
public class AttendanceResolver {

    public AttendanceResolver() {
    }

    public AggregateResolver getAbscenceCountResolverForStudent(GenericEntity student) {
        return new AttendanceAbsenceResolver(student);
    }
}
