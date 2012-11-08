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

import org.slc.sli.test.edfi.entities.CourseIdentityType;
import org.slc.sli.test.edfi.entities.CourseOffering;
import org.slc.sli.test.edfi.entities.CourseReferenceType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SessionReferenceType;
import org.slc.sli.test.edfi.entities.meta.CourseOfferingMeta;

public class CourseOfferingGenerator {
    public static CourseOffering generate(CourseOfferingMeta courseOfferingMeta) {
        CourseOffering courseOffering = new CourseOffering();
        courseOffering.setLocalCourseCode(courseOfferingMeta.id);

        courseOffering.setLocalCourseTitle(courseOfferingMeta.id + "-title");

        EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();
        eoit.setStateOrganizationId(courseOfferingMeta.schoolId.id);
        EducationalOrgReferenceType eort = new EducationalOrgReferenceType();
        eort.setEducationalOrgIdentity(eoit);
        courseOffering.setSchoolReference(eort);

        SessionReferenceType srt = SessionGenerator.getSessionReferenceType(courseOfferingMeta.schoolId.id,
                courseOfferingMeta.sessionMeta.id);

        courseOffering.setSessionReference(srt);

        CourseIdentityType cit = new CourseIdentityType();
        EducationalOrgIdentityType eoit2 = new EducationalOrgIdentityType();
        eoit2.setStateOrganizationId(courseOfferingMeta.courseMeta.schoolId);
        EducationalOrgReferenceType eort2 = new EducationalOrgReferenceType();
        eort2.setEducationalOrgIdentity(eoit2);
        courseOffering.setSchoolReference(eort2);
        cit.setEducationalOrgReference(eort2);
        cit.setUniqueCourseId(courseOfferingMeta.courseMeta.uniqueCourseId);
        CourseReferenceType crt = new CourseReferenceType();
        crt.setCourseIdentity(cit);
        courseOffering.setCourseReference(crt);

        return courseOffering;
    }

    public static CourseOffering generateLowFi(CourseOfferingMeta courseOfferingMeta) {
        return generate(courseOfferingMeta);
    }
}
