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


package org.slc.sli.test.generatorsR1;

import java.util.Random;

import org.slc.sli.test.edfi.entitiesR1.CourseCode;
import org.slc.sli.test.edfi.entitiesR1.CourseCodeSystemType;
import org.slc.sli.test.edfi.entitiesR1.EducationOrgIdentificationCode;
import org.slc.sli.test.edfi.entitiesR1.EducationOrgIdentificationSystemType;
import org.slc.sli.test.edfi.entitiesR1.EducationalEnvironmentType;
import org.slc.sli.test.edfi.entitiesR1.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entitiesR1.MediumOfInstructionType;
import org.slc.sli.test.edfi.entitiesR1.ObjectFactory;
import org.slc.sli.test.edfi.entitiesR1.PopulationServedType;
import org.slc.sli.test.edfi.entitiesR1.Section;
import org.slc.sli.test.edfi.entitiesR1.TermType;

public class SectionGenerator {


    public static Section generateMediumFiSliXsdRI(String sectionId, String schoolId, String courseId, String sessionId) {
   	 Section section = new Section();
//        String[] temp;
//        temp = courseId.split("-");
//        String courseTemp= temp[temp.length -1];
//        section.setUniqueSectionCode(sectionId + "-" + courseTemp);
        section.setUniqueSectionCode(sectionId);

        System.out.println("===============SECTION size ==" + section.toString());
//        section.setSequenceOfCourse(1);
//        // construct and add the school reference
//        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
//        edOrgIdentityType.setStateOrganizationId(schoolId);
//
//        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
//        schoolRef.setEducationalOrgIdentity(edOrgIdentityType);
//
//        section.setSchoolReference(schoolRef);
//
//        // construct and add the course reference
//        CourseCode courseCode = new CourseCode();
//        courseCode.setID(courseId);
//        courseCode.setIdentificationSystem(CourseCodeSystemType.CSSC_COURSE_CODE);
// 
//        
//        ObjectFactory of = new ObjectFactory();
// 
//        // construct and add the session reference
//        SessionIdentityType sessionIdentity = new SessionIdentityType();
//        sessionIdentity.setSessionName(sessionId);
//
//        SessionReferenceType sessionRef = new SessionReferenceType();
//        sessionRef.setSessionIdentity(sessionIdentity);
//
//        section.setSessionReference(sessionRef);
   
        return section;
   }
    

}
