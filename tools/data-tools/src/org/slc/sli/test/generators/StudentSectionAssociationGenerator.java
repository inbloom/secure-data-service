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

import org.slc.sli.test.edfi.entities.RepeatIdentifierType;
import org.slc.sli.test.edfi.entities.SectionIdentityType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.StudentIdentityType;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.StudentSectionAssociation;

public class StudentSectionAssociationGenerator {

    public static StudentSectionAssociation generateLowFi(String studentId, String schoolId, String sectionCode) {
        StudentSectionAssociation ssa = new StudentSectionAssociation();

        StudentIdentityType sit = new StudentIdentityType();
        sit.setStudentUniqueStateId(studentId);
        StudentReferenceType srt = new StudentReferenceType();
        srt.setStudentIdentity(sit);
        ssa.setStudentReference(srt);

        SectionIdentityType secit = new SectionIdentityType();
        secit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        secit.setUniqueSectionCode(sectionCode);
        SectionReferenceType secrt = new SectionReferenceType();
        secrt.setSectionIdentity(secit);
        ssa.setSectionReference(secrt);

        ssa.setBeginDate("2011-03-04");
        ssa.setEndDate("2011-03-04");

        ssa.setHomeroomIndicator(true);

        ssa.setRepeatIdentifier(RepeatIdentifierType.NOT_REPEATED);

        return ssa;
    }
}
