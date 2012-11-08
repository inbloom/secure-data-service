/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.CourseCode;
import org.slc.sli.test.edfi.entities.CourseCodeSystemType;
import org.slc.sli.test.edfi.entities.CourseOfferingIdentityType;
import org.slc.sli.test.edfi.entities.CourseOfferingReferenceType;
import org.slc.sli.test.edfi.entities.EducationalEnvironmentType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.MediumOfInstructionType;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.PopulationServedType;
import org.slc.sli.test.edfi.entities.Section;
import org.slc.sli.test.edfi.entities.SectionIdentityType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.SessionReferenceType;
import org.slc.sli.test.edfi.entities.TermType;

public class SectionGenerator {
    public static Section generate(String sectionCode, int sequenceOfCourse, String schoolId) {
        Section s = new Section();
        Random r = new Random(31);
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
        eoit.setStateOrganizationId(schoolId);
        EducationalOrgReferenceType eor = new EducationalOrgReferenceType();
        eor.setEducationalOrgIdentity(eoit);

        s.setSchoolReference(eor);

        return s;
    }

 
    
    public static Section generateMediumFi(String sectionId, String schoolId, String courseId, String sessionId) {
    	 Section section = new Section();
//         String[] temp;
//         temp = courseId.split("-");
//         String courseTemp= temp[temp.length -1];
//         section.setUniqueSectionCode(sectionId + "-" + courseTemp);
         section.setUniqueSectionCode(sectionId);
         
         section.setSequenceOfCourse(1);
         // construct and add the school reference
         EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
         edOrgIdentityType.setStateOrganizationId(schoolId);

         EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
         schoolRef.setEducationalOrgIdentity(edOrgIdentityType);

         section.setSchoolReference(schoolRef);

         // construct and add the course reference
         CourseOfferingIdentityType courseOfferingIdentity = new CourseOfferingIdentityType();
         String lcc = schoolId + "-l" + sessionId.substring(sessionId.lastIndexOf('-'))  + courseId.substring(courseId.lastIndexOf('-'));
         courseOfferingIdentity.setLocalCourseCode(lcc);
         CourseCode courseCode = new CourseCode();
         courseCode.setID(courseId);
         courseCode.setIdentificationSystem(CourseCodeSystemType.CSSC_COURSE_CODE);
         courseOfferingIdentity.getCourseCode().add(courseCode);
         courseOfferingIdentity.setTerm(TermType.SPRING_SEMESTER);
         courseOfferingIdentity.setSchoolYear("2011-2012");
         
         ObjectFactory of = new ObjectFactory();
         
         courseOfferingIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);

         CourseOfferingReferenceType courseRef = new CourseOfferingReferenceType();
         courseRef.setCourseOfferingIdentity(courseOfferingIdentity);

         section.setCourseOfferingReference(courseRef);

         // construct and add the session reference
        SessionReferenceType sessionRef = SessionGenerator.getSessionReferenceType(schoolId, sessionId);

         section.setSessionReference(sessionRef);

 
         return section;
    }

    public static Section generateLowFi(String sectionId, String schoolId, String courseId, String sessionId) {
        Section section = new Section();

        section.setUniqueSectionCode(sectionId);
        section.setSequenceOfCourse(1);

        // construct and add the school reference
        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
        edOrgIdentityType.setStateOrganizationId(schoolId);

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
        SessionReferenceType sessionRef = SessionGenerator.getSessionReferenceType(schoolId, sessionId);

        section.setSessionReference(sessionRef);

        return section;
    }

    public static SectionReferenceType getSectionReference(Section section) {
        SectionReferenceType sectionRef = new SectionReferenceType();
        SectionIdentityType identity = new SectionIdentityType();
        sectionRef.setSectionIdentity(identity);

        identity.setStateOrganizationId(section.getSchoolReference().getEducationalOrgIdentity().getStateOrganizationId());
        identity.setUniqueSectionCode(section.getUniqueSectionCode());

        return sectionRef;
    }

    public static SectionReferenceType getSectionReferenceType(String stateOrganizationId,/*
                                                                                           * Ny
                                                                                           * State
                                                                                           * Board
                                                                                           * Of
                                                                                           * Education
                                                                                           */
            String uniqueSectionCode) {
        SectionReferenceType sectionReference = new SectionReferenceType();
        SectionIdentityType sectionIdentity = new SectionIdentityType();
        sectionReference.setSectionIdentity(sectionIdentity);

        if (stateOrganizationId != null)
            sectionIdentity.setStateOrganizationId(stateOrganizationId);

        if (uniqueSectionCode != null)
            sectionIdentity.setUniqueSectionCode(uniqueSectionCode);

        return sectionReference;
    }

    public static void main(String[] args) {
        SectionReferenceType sRef = SectionGenerator.getSectionReferenceType("stateOrganizationId",
                "uniqueSectionCode");
        System.out.println(sRef);
    }
}
