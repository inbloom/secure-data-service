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

import org.slc.sli.test.edfi.entities.SLCCourseIdentityType;
import org.slc.sli.test.edfi.entities.SLCCourseOffering;
import org.slc.sli.test.edfi.entities.SLCCourseReferenceType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCSessionReferenceType;
import org.slc.sli.test.edfi.entities.meta.CourseOfferingMeta;

public class CourseOfferingGenerator {
    public static SLCCourseOffering generate(CourseOfferingMeta courseOfferingMeta) {
        SLCCourseOffering courseOffering = new SLCCourseOffering();
        courseOffering.setLocalCourseCode(courseOfferingMeta.id);

        courseOffering.setLocalCourseTitle(courseOfferingMeta.id + "-title");

        SLCEducationalOrgIdentityType eoit = new SLCEducationalOrgIdentityType();
        eoit.setStateOrganizationId(courseOfferingMeta.schoolId.id);
        SLCEducationalOrgReferenceType eort = new SLCEducationalOrgReferenceType();
        eort.setEducationalOrgIdentity(eoit);
        courseOffering.setSchoolReference(eort);

        SLCSessionReferenceType srt = SessionGenerator.getSessionReferenceType(courseOfferingMeta.schoolId.id,
                courseOfferingMeta.sessionMeta.id);

        courseOffering.setSessionReference(srt);

        SLCCourseIdentityType cit = new SLCCourseIdentityType();
        SLCEducationalOrgIdentityType eoit2 = new SLCEducationalOrgIdentityType();
        eoit2.setStateOrganizationId(courseOfferingMeta.courseMeta.schoolId);
        SLCEducationalOrgReferenceType eort2 = new SLCEducationalOrgReferenceType();
        eort2.setEducationalOrgIdentity(eoit2);
        courseOffering.setSchoolReference(eort2);
        cit.setEducationalOrgReference(eort2);
        cit.setUniqueCourseId(courseOfferingMeta.courseMeta.uniqueCourseId);
        SLCCourseReferenceType crt = new SLCCourseReferenceType();
        crt.setCourseIdentity(cit);
        courseOffering.setCourseReference(crt);

        return courseOffering;
    }

    public static SLCCourseOffering generateLowFi(CourseOfferingMeta courseOfferingMeta) {
        return generate(courseOfferingMeta);
    }
}
