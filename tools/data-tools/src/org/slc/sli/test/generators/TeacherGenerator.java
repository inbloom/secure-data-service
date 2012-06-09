package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;
import org.slc.sli.test.edfi.entities.Teacher;

public class TeacherGenerator extends StaffGenerator {

    public TeacherGenerator(StateAbbreviationType state, boolean optional) {
        super(state, optional);
    }

    public Teacher generate(String teacherId) {
        Teacher teacher = new Teacher();
        populateFields(teacher, teacherId);
        return teacher;
    }
    
    protected void populateFields(Teacher teacher, String teacherId) {
        super.populateFields(teacher, teacherId);
        teacher.setHighlyQualifiedTeacher(random.nextBoolean());
    }
    
    public static Teacher generateLowFi(String teacherId) {
        Teacher teacher = new Teacher();
        populateFieldsLowFi(teacher, teacherId);
        return teacher;
    }

    public static StaffReferenceType getTeacherReference(String staffId) {
        return getStaffReference(staffId);
    }
}
