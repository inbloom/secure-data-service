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

import org.slc.sli.test.edfi.entities.ClassroomPositionType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCSectionIdentityType;
import org.slc.sli.test.edfi.entities.SLCSectionReferenceType;
import org.slc.sli.test.edfi.entities.SLCStaffIdentityType;
import org.slc.sli.test.edfi.entities.SLCStaffReferenceType;
import org.slc.sli.test.edfi.entities.SLCTeacherSectionAssociation;
import org.slc.sli.test.edfi.entities.meta.TeacherMeta;

public class TeacherSectionAssociationGenerator {
    private Random r = new Random(31);

    public SLCTeacherSectionAssociation generate(String teacher, String school, String sectionCode) {

        SLCTeacherSectionAssociation tsa = new SLCTeacherSectionAssociation();

        tsa.setTeacherReference(TeacherGenerator.getTeacherReference(teacher));

        SLCSectionIdentityType secit = new SLCSectionIdentityType();
        SLCEducationalOrgReferenceType eort = new SLCEducationalOrgReferenceType();
        SLCEducationalOrgIdentityType eoit = new SLCEducationalOrgIdentityType();

        eoit.setStateOrganizationId(school);
        eort.setEducationalOrgIdentity(eoit);
        secit.setEducationalOrgReference(eort);
        secit.setUniqueSectionCode(sectionCode);
        SLCSectionReferenceType secrt = new SLCSectionReferenceType();
        secrt.setSectionIdentity(secit);
        tsa.setSectionReference(secrt);

        tsa.setClassroomPosition(ClassroomPositionType.TEACHER_OF_RECORD);

        tsa.setHighlyQualifiedTeacher(r.nextBoolean());

        return tsa;
    }

    public static SLCTeacherSectionAssociation generateLowFi(TeacherMeta teacherMeta, String sectionId) {

        SLCTeacherSectionAssociation teacherSection = new SLCTeacherSectionAssociation();

        // construct and add the teacher reference
        SLCStaffIdentityType staffIdentity = new SLCStaffIdentityType();
        staffIdentity.setStaffUniqueStateId(teacherMeta.id);

        // StaffReferenceType teacherRef = new StaffReferenceType();
        // teacherRef.setStaffIdentity(staffIdentity);
        //
        // teacherSection.setTeacherReference(teacherRef);

        teacherSection.setClassroomPosition(ClassroomPositionType.TEACHER_OF_RECORD);

        // construct and add the section references
        SLCSectionIdentityType sectionIdentity = new SLCSectionIdentityType();
        sectionIdentity.setUniqueSectionCode(sectionId);
        SLCEducationalOrgReferenceType eort = new SLCEducationalOrgReferenceType();
        SLCEducationalOrgIdentityType eoit = new SLCEducationalOrgIdentityType();

        eoit.setStateOrganizationId(teacherMeta.schoolIds.get(0));
        eort.setEducationalOrgIdentity(eoit);

        sectionIdentity.setEducationalOrgReference(eort);

        // EducationOrgIdentificationCode edOrgIdCode = new EducationOrgIdentificationCode();
        // edOrgIdCode.setID(sectionId);
        // edOrgIdCode.setIdentificationSystem(EducationOrgIdentificationSystemType.SCHOOL);
        // sectionIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(edOrgIdCode);

        SLCSectionReferenceType sectionRef = new SLCSectionReferenceType();
        sectionRef.setSectionIdentity(sectionIdentity);
        teacherSection.setSectionReference(sectionRef);

        SLCStaffReferenceType teacherRef = new SLCStaffReferenceType();
        teacherRef.setStaffIdentity(staffIdentity);

        teacherSection.setTeacherReference(teacherRef);

        teacherSection.setClassroomPosition(ClassroomPositionType.TEACHER_OF_RECORD);

        return teacherSection;
    }
}
