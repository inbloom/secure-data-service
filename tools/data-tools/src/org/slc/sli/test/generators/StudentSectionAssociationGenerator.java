package org.slc.sli.test.generators;

import java.util.Calendar;
import java.util.Random;

import org.slc.sli.test.edfi.entities.RepeatIdentifierType;
import org.slc.sli.test.edfi.entities.SectionIdentityType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.StudentIdentityType;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.StudentSectionAssociation;

public class StudentSectionAssociationGenerator {
    private Random random = new Random();

    public StudentSectionAssociation generate(String student, String school, String sectionCode) {
        StudentSectionAssociation ssa = new StudentSectionAssociation();

        StudentIdentityType sit = new StudentIdentityType();
        sit.setStudentUniqueStateId(student);
        StudentReferenceType srt = new StudentReferenceType();
        srt.setStudentIdentity(sit);
        ssa.setStudentReference(srt);

        SectionIdentityType secit = new SectionIdentityType();
        secit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(school);
        secit.setUniqueSectionCode(sectionCode);
        SectionReferenceType secrt = new SectionReferenceType();
        secrt.setSectionIdentity(secit);
        ssa.setSectionReference(secrt);

        Calendar rightNow = Calendar.getInstance();
        Calendar nextWeek = Calendar.getInstance();
        nextWeek.roll(Calendar.WEEK_OF_YEAR, 1);

        ssa.setBeginDate(rightNow);
        ssa.setEndDate(nextWeek);

        ssa.setHomeroomIndicator(random.nextBoolean());

        ssa.setRepeatIdentifier(RepeatIdentifierType.NOT_REPEATED);

        return ssa;
    }
}
