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

import java.util.List;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.AcademicSubjectsType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.GradeLevelsType;
import org.slc.sli.test.edfi.entities.ProgramAssignmentType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCStaffIdentityType;
import org.slc.sli.test.edfi.entities.SLCStaffReferenceType;
import org.slc.sli.test.edfi.entities.SLCTeacherSchoolAssociation;
import org.slc.sli.test.edfi.entities.meta.TeacherMeta;

public class TeacherSchoolAssociationGenerator {
    public SLCTeacherSchoolAssociation generate(String staffId, List<String> stateOrgIds) {
        SLCTeacherSchoolAssociation tsa = new SLCTeacherSchoolAssociation();

        tsa.setTeacherReference(TeacherGenerator.getTeacherReference(staffId));

        SLCEducationalOrgIdentityType eoit = new SLCEducationalOrgIdentityType();

        for (String stateOrgId : stateOrgIds) {
            // eoit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(stateOrgId);
            eoit.setStateOrganizationId(stateOrgId);
        }

        SLCEducationalOrgReferenceType eor = new SLCEducationalOrgReferenceType();
        eor.setEducationalOrgIdentity(eoit);
        tsa.setSchoolReference(eor);

        tsa.setProgramAssignment(ProgramAssignmentType.REGULAR_EDUCATION);

        GradeLevelsType glt = new GradeLevelsType();
        glt.getGradeLevel().add(GradeLevelType.EARLY_EDUCATION);
        tsa.setInstructionalGradeLevels(glt);

        AcademicSubjectsType ast = new AcademicSubjectsType();
        ast.getAcademicSubject().add(AcademicSubjectType.COMPUTER_AND_INFORMATION_SCIENCES);
        tsa.setAcademicSubjects(ast);
        return tsa;
    }

    public static SLCTeacherSchoolAssociation generateLowFi(TeacherMeta teacherMeta, String schoolId) {

        SLCTeacherSchoolAssociation teacherSchool = new SLCTeacherSchoolAssociation();

        // construct and add the school references
        SLCEducationalOrgIdentityType edOrgIdentity = new SLCEducationalOrgIdentityType();
        // edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        edOrgIdentity.setStateOrganizationId(schoolId);

        SLCEducationalOrgReferenceType schoolRef = new SLCEducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);

        teacherSchool.setSchoolReference(schoolRef);

        SLCStaffIdentityType staffIdentity = new SLCStaffIdentityType();
        staffIdentity.setStaffUniqueStateId(teacherMeta.id);
        SLCStaffReferenceType teacherRef = new SLCStaffReferenceType();
        teacherRef.setStaffIdentity(staffIdentity);

        teacherSchool.setTeacherReference(teacherRef);

        teacherSchool.setProgramAssignment(ProgramAssignmentType.REGULAR_EDUCATION);

        GradeLevelsType glt = new GradeLevelsType();
        glt.getGradeLevel().add(GradeLevelType.EARLY_EDUCATION);
        teacherSchool.setInstructionalGradeLevels(glt);

        AcademicSubjectsType ast = new AcademicSubjectsType();
        ast.getAcademicSubject().add(AcademicSubjectType.COMPUTER_AND_INFORMATION_SCIENCES);
        teacherSchool.setAcademicSubjects(ast);
        return teacherSchool;
    }
}
