package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.*;

public class StudentSchoolAssociationGenerator {
    public StudentSchoolAssociation generate(String student, String school) {
        StudentSchoolAssociation ssa = new StudentSchoolAssociation();

        StudentIdentityType sit = new StudentIdentityType();
        sit.setStudentUniqueStateId(student);
        StudentReferenceType srt = new StudentReferenceType();
        srt.setStudentIdentity(sit);
        ssa.setStudentReference(srt);

        EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();
        eoit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(school);
        EducationalOrgReferenceType eor = new EducationalOrgReferenceType();
        eor.setEducationalOrgIdentity(eoit);
        ssa.setSchoolReference(eor);

        ssa.setEntryGradeLevel(GradeLevelType.FIFTH_GRADE);

        return ssa;
    }
}
