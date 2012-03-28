package org.slc.sli.test.generators;

import java.util.Random;
import java.util.UUID;

import org.slc.sli.test.edfi.entities.*;

public class SectionGenerator {
    public static Section generate(String sectionCode, int sequenceOfCourse, String schoolId) {
        Section s = new Section();
        Random r = new Random();
        //String sectionCode = UUID.randomUUID().toString();

        s.setUniqueSectionCode(sectionCode);

        s.setSequenceOfCourse(r.nextInt(7) + 1);

        s.setEducationalEnvironment(EducationalEnvironmentType.CLASSROOM);

        s.setMediumOfInstruction(MediumOfInstructionType.CORRESPONDENCE_INSTRUCTION);

        s.setPopulationServed(PopulationServedType.BILINGUAL_STUDENTS);

        CourseOfferingIdentityType coit = new CourseOfferingIdentityType();
        coit.setLocalCourseCode(sectionCode);
        coit.setSchoolYear("2011-2012");
        coit.setTerm(TermType.FALL_SEMESTER);
        CourseOfferingReferenceType corft = new CourseOfferingReferenceType();
        corft.setCourseOfferingIdentity(coit);
        s.setCourseOfferingReference(corft);

        EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();
        eoit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        EducationalOrgReferenceType eor = new EducationalOrgReferenceType();
        eor.setEducationalOrgIdentity(eoit);

        s.setSchoolReference(eor);

        return s;
    }
}
