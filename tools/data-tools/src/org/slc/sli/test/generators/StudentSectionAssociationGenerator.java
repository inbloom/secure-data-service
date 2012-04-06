package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.RepeatIdentifierType;
import org.slc.sli.test.edfi.entities.SectionIdentityType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.StudentIdentityType;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.StudentSectionAssociation;

public class StudentSectionAssociationGenerator {

    public static StudentSectionAssociation generateLowFi(String studentId, String schoolId, String sectionCode) {
        StudentSectionAssociation ssa = new StudentSectionAssociation();

        StudentIdentityType sit = new StudentIdentityType();
        sit.setStudentUniqueStateId(studentId);
        StudentReferenceType srt = new StudentReferenceType();
        srt.setStudentIdentity(sit);
        ssa.setStudentReference(srt);

        SectionIdentityType secit = new SectionIdentityType();
        secit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        secit.setUniqueSectionCode(sectionCode);
        SectionReferenceType secrt = new SectionReferenceType();
        secrt.setSectionIdentity(secit);
        ssa.setSectionReference(secrt);

        ssa.setBeginDate("2011-03-04");
        ssa.setEndDate("2011-03-04");

        ssa.setHomeroomIndicator(true);

        ssa.setRepeatIdentifier(RepeatIdentifierType.NOT_REPEATED);

        return ssa;
    }
}
