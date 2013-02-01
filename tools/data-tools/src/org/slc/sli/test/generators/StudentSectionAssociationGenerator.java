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

import org.slc.sli.test.edfi.entities.RepeatIdentifierType;
import org.slc.sli.test.edfi.entities.SLCSectionIdentityType;
import org.slc.sli.test.edfi.entities.SLCSectionReferenceType;
import org.slc.sli.test.edfi.entities.SLCStudentIdentityType;
import org.slc.sli.test.edfi.entities.SLCStudentReferenceType;
import org.slc.sli.test.edfi.entities.SLCStudentSectionAssociation;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;

public class StudentSectionAssociationGenerator {

    public static SLCStudentSectionAssociation generateLowFi(String studentId, String schoolId, String sectionCode) {
        SLCStudentSectionAssociation ssa = new SLCStudentSectionAssociation();




        SLCStudentIdentityType sit = new SLCStudentIdentityType();
        sit.setStudentUniqueStateId(studentId);
        SLCStudentReferenceType srt = new SLCStudentReferenceType();
        srt.setStudentIdentity(sit);
        ssa.setStudentReference(srt);

        SLCSectionIdentityType secit = new SLCSectionIdentityType();
        SLCEducationalOrgReferenceType eort = new SLCEducationalOrgReferenceType();
        SLCEducationalOrgIdentityType eoit = new SLCEducationalOrgIdentityType();

        eoit.setStateOrganizationId(schoolId);
        eort.setEducationalOrgIdentity(eoit);

        secit.setEducationalOrgReference(eort);
        secit.setUniqueSectionCode(sectionCode);

        SLCSectionReferenceType secrt = new SLCSectionReferenceType();
        secrt.setSectionIdentity(secit);
        ssa.setSectionReference(secrt);

        ssa.setBeginDate("2011-03-04");
        ssa.setEndDate("2011-03-04");

        ssa.setHomeroomIndicator(true);

        ssa.setRepeatIdentifier(RepeatIdentifierType.NOT_REPEATED);

        return ssa;
    }
}
