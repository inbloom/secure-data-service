package org.slc.sli.view;

import org.slc.sli.entity.GenericEntity;

import java.util.List;

/**
 * Class to handle the oddities of getting absences from attendance objects.
 */
public class AttendanceAbsenceResolver implements AggregateResolver {

    List<GenericEntity> studentList;

    public AttendanceAbsenceResolver(List<GenericEntity> studentList) {
        this.studentList = studentList;
    }

    public AttendanceAbsenceResolver() {
    }

    public void setStudentList(List<GenericEntity> studentList) {
        this.studentList = studentList;
    }

    @Override
    public int getCountForPath(String path) {
        return 0;
    }
}
