package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.CourseCode;
import org.slc.sli.test.edfi.entities.CourseCodeSystemType;
import org.slc.sli.test.edfi.entities.CourseOfferingIdentityType;
import org.slc.sli.test.edfi.entities.CourseOfferingReferenceType;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationCode;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationSystemType;
import org.slc.sli.test.edfi.entities.EducationalEnvironmentType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.MediumOfInstructionType;
import org.slc.sli.test.edfi.entities.PopulationServedType;
import org.slc.sli.test.edfi.entities.Section;
import org.slc.sli.test.edfi.entities.SectionIdentityType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.SessionIdentityType;
import org.slc.sli.test.edfi.entities.SessionReferenceType;
import org.slc.sli.test.edfi.entities.TermType;

public class SectionGenerator {
    public static Section generate(String sectionCode, int sequenceOfCourse, String schoolId) {
        Section s = new Section();
        Random r = new Random();
        // String sectionCode = UUID.randomUUID().toString();

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

    public static Section generateLowFi(String sectionId, String schoolId, String courseId, String sessionId) {
        Section section = new Section();
        section.setUniqueSectionCode(sectionId);
        section.setSequenceOfCourse(1);

        // construct and add the school reference
        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
        edOrgIdentityType.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);

        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentityType);

        section.setSchoolReference(schoolRef);

        // construct and add the course reference
        CourseOfferingIdentityType courseOfferingIdentity = new CourseOfferingIdentityType();
        courseOfferingIdentity.setLocalCourseCode(courseId);
        CourseCode courseCode = new CourseCode();
        courseCode.setID(courseId);
        courseCode.setIdentificationSystem(CourseCodeSystemType.CSSC_COURSE_CODE);
        courseOfferingIdentity.getCourseCode().add(courseCode);
        courseOfferingIdentity.setTerm(TermType.SPRING_SEMESTER);
        courseOfferingIdentity.setSchoolYear("2011-2012");

        CourseOfferingReferenceType courseRef = new CourseOfferingReferenceType();
        courseRef.setCourseOfferingIdentity(courseOfferingIdentity);

        section.setCourseOfferingReference(courseRef);

        // construct and add the session reference
        SessionIdentityType sessionIdentity = new SessionIdentityType();
        sessionIdentity.setSessionName(sessionId);

        SessionReferenceType sessionRef = new SessionReferenceType();
        sessionRef.setSessionIdentity(sessionIdentity);

        section.setSessionReference(sessionRef);

        return section;
    }

    public static SectionReferenceType getSectionReference(Section section) {
        SectionReferenceType sectionRef = new SectionReferenceType();
        SectionIdentityType identity = new SectionIdentityType();
        sectionRef.setSectionIdentity(identity);

        CourseOfferingIdentityType courseIdentity = section.getCourseOfferingReference().getCourseOfferingIdentity();
        identity.getStateOrganizationIdOrEducationOrgIdentificationCode();
        identity.setUniqueSectionCode(section.getUniqueSectionCode());

        identity.setCourseCode(courseIdentity.getCourseCode().get(0));
        identity.setLocalCourseCode(courseIdentity.getLocalCourseCode());
        identity.setSchoolYear(courseIdentity.getSchoolYear());
        identity.setTerm(courseIdentity.getTerm());
        identity.setClassPeriodName(section.getClassPeriodReference().getClassPeriodIdentity().getClassPeriodName());
        identity.setLocation(section.getLocationReference().getLocationIdentity().getClassroomIdentificationCode());
        return sectionRef;
    }

    public static SectionReferenceType getSectionReferenceType(String stateOrganizationId,/*
                                                                                           * Ny
                                                                                           * State
                                                                                           * Board
                                                                                           * Of
                                                                                           * Education
                                                                                           */
            String educationOrgIdentificationCode_ID,/* Manhattan High School */
            String schoolYear, String classPeriodName, String location, String courseCodeID, String localCourseCode,
            String uniqueSectionCode) {
        SectionReferenceType sectionReference = new SectionReferenceType();
        SectionIdentityType sectionIdentity = new SectionIdentityType();
        sectionReference.setSectionIdentity(sectionIdentity);

        if (stateOrganizationId != null)
            sectionIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(stateOrganizationId);

        if (educationOrgIdentificationCode_ID != null) {
            EducationOrgIdentificationCode edOrgCode = new EducationOrgIdentificationCode();
            edOrgCode.setID(educationOrgIdentificationCode_ID);
            edOrgCode.setIdentificationSystem(EducationOrgIdentificationSystemType.SCHOOL);
            sectionIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(edOrgCode);
        }

        if (uniqueSectionCode != null)
            sectionIdentity.setUniqueSectionCode(uniqueSectionCode);
        if (schoolYear != null)
            sectionIdentity.setSchoolYear(schoolYear);
        sectionIdentity.setTerm(TermType.YEAR_ROUND);
        if (classPeriodName != null)
            sectionIdentity.setClassPeriodName(classPeriodName);
        if (location != null)
            sectionIdentity.setLocation(location);

        CourseCode courseCode = new CourseCode();
        if (courseCode != null)
            sectionIdentity.setCourseCode(courseCode);
        courseCode.setIdentificationSystem(CourseCodeSystemType.CSSC_COURSE_CODE);
        if (courseCodeID != null)
            courseCode.setID(courseCodeID);
        courseCode.setAssigningOrganizationCode("CourseCode Assigner");
        if (localCourseCode != null)
            sectionIdentity.setLocalCourseCode(localCourseCode);
        return sectionReference;
    }

    public static void main(String[] args) {
        SectionReferenceType sRef = SectionGenerator.getSectionReferenceType("stateOrganizationId",
                "educationOrgIdentificationCode_ID", "schoolYear", "classPeriodName", "location", "courseCodeID",
                "localCourseCode", "uniqueSectionCode");
        System.out.println(sRef);
    }
}
