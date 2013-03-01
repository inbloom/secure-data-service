/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import org.slc.sli.test.edfi.entities.SLCCourseOfferingIdentityType;
import org.slc.sli.test.edfi.entities.SLCCourseOfferingReferenceType;
import org.slc.sli.test.edfi.entities.EducationalEnvironmentType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.MediumOfInstructionType;
import org.slc.sli.test.edfi.entities.PopulationServedType;
import org.slc.sli.test.edfi.entities.SLCSection;
import org.slc.sli.test.edfi.entities.SLCSectionIdentityType;
import org.slc.sli.test.edfi.entities.SLCSectionReferenceType;
import org.slc.sli.test.edfi.entities.SLCSessionReferenceType;
import org.slc.sli.test.edfi.entities.meta.CourseOfferingMeta;
import org.slc.sli.test.edfi.entities.meta.SectionMeta;
import org.slc.sli.test.edfi.entities.meta.SessionMeta;

public class SectionGenerator {
    public static SLCSection generate(String sectionCode, int sequenceOfCourse, String schoolId) {
        SLCSection s = new SLCSection();
        Random r = new Random(31);
        // String sectionCode = UUID.randomUUID().toString();

        s.setUniqueSectionCode(sectionCode);

        s.setSequenceOfCourse(r.nextInt(7) + 1);

        s.setEducationalEnvironment(EducationalEnvironmentType.CLASSROOM);

        s.setMediumOfInstruction(MediumOfInstructionType.CORRESPONDENCE_INSTRUCTION);

        s.setPopulationServed(PopulationServedType.BILINGUAL_STUDENTS);

        //This is a fake CourseOfferingReference
        SLCCourseOfferingIdentityType coit = new SLCCourseOfferingIdentityType();
        coit.setLocalCourseCode(sectionCode);
        coit.setEducationalOrgReference(getEducationalOrgReference(schoolId));
        coit.setSessionReference(SessionGenerator.getSessionReferenceType(schoolId, sectionCode));
        SLCCourseOfferingReferenceType corft = new SLCCourseOfferingReferenceType();
        corft.setCourseOfferingIdentity(coit);
        s.setCourseOfferingReference(corft);

        SLCEducationalOrgIdentityType eoit = new SLCEducationalOrgIdentityType();
        eoit.setStateOrganizationId(schoolId);
        SLCEducationalOrgReferenceType eor = new SLCEducationalOrgReferenceType();
        eor.setEducationalOrgIdentity(eoit);

        s.setSchoolReference(eor);

        return s;
    }
    
    public static SLCSection generateMediumFi(SectionMeta sectionMeta) {
        String sectionId = sectionMeta.id;
        String schoolId = sectionMeta.schoolId;
        String sessionId = sectionMeta.sessionId;

        SLCSection section = new SLCSection();
//         String[] temp;
//         temp = courseId.split("-");
//         String courseTemp= temp[temp.length -1];
//         section.setUniqueSectionCode(sectionId + "-" + courseTemp);
         section.setUniqueSectionCode(sectionId);

         section.setSequenceOfCourse(1);

         // construct and add the school reference
         SLCEducationalOrgIdentityType edOrgIdentityType = new SLCEducationalOrgIdentityType();
         edOrgIdentityType.setStateOrganizationId(schoolId);

         SLCEducationalOrgReferenceType schoolRef = new SLCEducationalOrgReferenceType();
         schoolRef.setEducationalOrgIdentity(edOrgIdentityType);

         section.setSchoolReference(schoolRef);

         // construct and add the course reference
         section.setCourseOfferingReference(getCourseOfferingReference(sectionMeta.courseOffering));

         // construct and add the session reference
         SLCSessionReferenceType sessionRef = SessionGenerator.getSessionReferenceType(schoolId, sessionId);

         section.setSessionReference(sessionRef);

 
         return section;
    }

    public static SLCSection generateLowFi(SectionMeta sectionMeta) {
        String sectionId = sectionMeta.id;
        String schoolId = sectionMeta.schoolId;
        String sessionId = sectionMeta.sessionId;

        SLCSection section = new SLCSection();

        section.setUniqueSectionCode(sectionId);
        section.setSequenceOfCourse(1);

        // construct and add the school reference
        SLCEducationalOrgIdentityType edOrgIdentityType = new SLCEducationalOrgIdentityType();
        edOrgIdentityType.setStateOrganizationId(schoolId);

        SLCEducationalOrgReferenceType schoolRef = new SLCEducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentityType);

        section.setSchoolReference(schoolRef);

        // construct and add the course reference
        section.setCourseOfferingReference(getCourseOfferingReference(sectionMeta.courseOffering));

        // construct and add the session reference
        SLCSessionReferenceType sessionRef = SessionGenerator.getSessionReferenceType(schoolId, sessionId);

        section.setSessionReference(sessionRef);

        return section;
    }

    public static SLCSectionReferenceType getSectionReference(SLCSection section) {
        SLCSectionReferenceType sectionRef = new SLCSectionReferenceType();
        SLCSectionIdentityType identity = new SLCSectionIdentityType();
        sectionRef.setSectionIdentity(identity);

        identity.setEducationalOrgReference(section.getSchoolReference());
        identity.setUniqueSectionCode(section.getUniqueSectionCode());

        return sectionRef;
    }

    public static SLCSectionReferenceType getSectionReferenceType(String stateOrganizationId,/*
                                                                                           * Ny
                                                                                           * State
                                                                                           * Board
                                                                                           * Of
                                                                                           * Education
                                                                                           */
            String uniqueSectionCode) {
        SLCSectionReferenceType sectionReference = new SLCSectionReferenceType();
        SLCSectionIdentityType sectionIdentity = new SLCSectionIdentityType();
        sectionReference.setSectionIdentity(sectionIdentity);

        if (stateOrganizationId != null) {
            SLCEducationalOrgReferenceType edOrgRef = new SLCEducationalOrgReferenceType();
            SLCEducationalOrgIdentityType edOrgId = new SLCEducationalOrgIdentityType();
            edOrgId.setStateOrganizationId(stateOrganizationId);
            edOrgRef.setEducationalOrgIdentity(edOrgId);
            sectionIdentity.setEducationalOrgReference(edOrgRef);
        }
        
        if (uniqueSectionCode != null)
            sectionIdentity.setUniqueSectionCode(uniqueSectionCode);

        return sectionReference;
    }

    public static void main(String[] args) {
        SLCSectionReferenceType sRef = SectionGenerator.getSectionReferenceType("stateOrganizationId",
                "uniqueSectionCode");
        System.out.println(sRef);
    }

    public static SLCCourseOfferingReferenceType getCourseOfferingReference(CourseOfferingMeta courseOfferingMeta) {
        SLCCourseOfferingIdentityType ident = new SLCCourseOfferingIdentityType();
        ident.setLocalCourseCode(courseOfferingMeta.id);
        ident.setEducationalOrgReference(getEducationalOrgReference(courseOfferingMeta.schoolId.id));
        ident.setSessionReference(getSessionReference(courseOfferingMeta.sessionMeta));
        SLCCourseOfferingReferenceType ref = new SLCCourseOfferingReferenceType();
        ref.setCourseOfferingIdentity(ident);
        return ref;
    }

    public static SLCSessionReferenceType getSessionReference(SessionMeta sessionMeta) {
        return SessionGenerator.getSessionReferenceType(sessionMeta.schoolId, sessionMeta.id);
    }

    public static SLCEducationalOrgReferenceType getEducationalOrgReference(String stateOrganizationId) {
        SLCEducationalOrgIdentityType ident = new SLCEducationalOrgIdentityType();
        ident.setStateOrganizationId(stateOrganizationId);
        SLCEducationalOrgReferenceType ref = new SLCEducationalOrgReferenceType();
        ref.setEducationalOrgIdentity(ident);
        return ref;
    }
}
