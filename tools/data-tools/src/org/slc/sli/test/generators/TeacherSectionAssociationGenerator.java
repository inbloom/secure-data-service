package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.ClassroomPositionType;
import org.slc.sli.test.edfi.entities.SectionIdentityType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.StaffIdentityType;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.TeacherSectionAssociation;

public class TeacherSectionAssociationGenerator {
    public static TeacherSectionAssociation generate(String teacher, String school, String sectionCode) {
        Random r = new Random();

        TeacherSectionAssociation tsa = new TeacherSectionAssociation();

        StaffIdentityType sit = new StaffIdentityType();
        sit.setStaffUniqueStateId(teacher);
        StaffReferenceType srt = new StaffReferenceType();
        srt.setStaffIdentity(sit);
        tsa.setTeacherReference(srt);

        SectionIdentityType secit = new SectionIdentityType();
        secit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(school);
        secit.setUniqueSectionCode(sectionCode);
        SectionReferenceType secrt = new SectionReferenceType();
        secrt.setSectionIdentity(secit);
        tsa.setSectionReference(secrt);

        tsa.setClassroomPosition(ClassroomPositionType.TEACHER_OF_RECORD);

        tsa.setHighlyQualifiedTeacher(r.nextBoolean());

        return tsa;
    }
}
