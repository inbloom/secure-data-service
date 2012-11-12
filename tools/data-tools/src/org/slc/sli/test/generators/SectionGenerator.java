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

import org.slc.sli.test.edfi.entities.CourseOfferingIdentityType;
import org.slc.sli.test.edfi.entities.CourseOfferingReferenceType;
import org.slc.sli.test.edfi.entities.EducationalEnvironmentType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.MediumOfInstructionType;
import org.slc.sli.test.edfi.entities.PopulationServedType;
import org.slc.sli.test.edfi.entities.Section;
import org.slc.sli.test.edfi.entities.SectionIdentityType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.SessionReferenceType;
import org.slc.sli.test.edfi.entities.meta.CourseOfferingMeta;
import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.edfi.entities.meta.SessionMeta;

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

        //This is a fake CourseOfferingReference
        CourseOfferingIdentityType coit = new CourseOfferingIdentityType();
        coit.setLocalCourseCode(sectionCode);
        coit.setEducationalOrgReference(getEducationalOrgReference(schoolId));
        coit.setSessionReference(SessionGenerator.getSessionReferenceType(schoolId, sectionCode));
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
    
    public static Section generateMediumFi(SectionMeta sectionMeta) {
    	String sectionId = sectionMeta.id;
    	String schoolId = sectionMeta.schoolId;
    	String sessionId = sectionMeta.sessionId;

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
         section.setCourseOfferingReference(getCourseOfferingReference(sectionMeta.courseOffering));

         // construct and add the session reference
        SessionReferenceType sessionRef = SessionGenerator.getSessionReferenceType(schoolId, sessionId);

         section.setSessionReference(sessionRef);

 
         return section;
    }

    public static Section generateLowFi(SectionMeta sectionMeta) {
    	String sectionId = sectionMeta.id;
    	String schoolId = sectionMeta.schoolId;
    	String sessionId = sectionMeta.sessionId;

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
        section.setCourseOfferingReference(getCourseOfferingReference(sectionMeta.courseOffering));

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

    public static CourseOfferingReferenceType getCourseOfferingReference(CourseOfferingMeta courseOfferingMeta) {
        CourseOfferingIdentityType ident = new CourseOfferingIdentityType();
        ident.setLocalCourseCode(courseOfferingMeta.id);
        ident.setEducationalOrgReference(getEducationalOrgReference(courseOfferingMeta.schoolId.id));
        ident.setSessionReference(getSessionReference(courseOfferingMeta.sessionMeta));
        CourseOfferingReferenceType ref = new CourseOfferingReferenceType();
        ref.setCourseOfferingIdentity(ident);
        return ref;
    }

    public static SessionReferenceType getSessionReference(SessionMeta sessionMeta) {
    	return SessionGenerator.getSessionReferenceType(sessionMeta.schoolId, sessionMeta.id);
    }

    public static EducationalOrgReferenceType getEducationalOrgReference(String stateOrganizationId) {
        EducationalOrgIdentityType ident = new EducationalOrgIdentityType();
        ident.setStateOrganizationId(stateOrganizationId);
        EducationalOrgReferenceType ref = new EducationalOrgReferenceType();
        ref.setEducationalOrgIdentity(ident);
        return ref;
    }
}
