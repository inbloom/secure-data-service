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

import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCStudentIdentityType;
import org.slc.sli.test.edfi.entities.SLCStudentReferenceType;
import org.slc.sli.test.edfi.entities.SLCStudentSchoolAssociation;

public class StudentSchoolAssociationGenerator {

    public static SLCStudentSchoolAssociation generateLowFi(String studentId, String schoolId) {

        SLCStudentSchoolAssociation ssa = new SLCStudentSchoolAssociation();

        String graduationPlan = schoolId + "-gPlan0";

        SLCStudentIdentityType sit = new SLCStudentIdentityType();
        sit.setStudentUniqueStateId(studentId);
        SLCStudentReferenceType srt = new SLCStudentReferenceType();
        srt.setStudentIdentity(sit);
        ssa.setStudentReference(srt);

        SLCEducationalOrgIdentityType eoit = new SLCEducationalOrgIdentityType();
        // eoit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        eoit.setStateOrganizationId(schoolId);
        SLCEducationalOrgReferenceType eor = new SLCEducationalOrgReferenceType();
        eor.setEducationalOrgIdentity(eoit);
        ssa.setSchoolReference(eor);

        ssa.setEntryGradeLevel(GradeLevelType.FIFTH_GRADE);
        ssa.setSchoolYear("2011-2012");
        ssa.setEntryDate("2011-09-01");

        return ssa;
    }
}
