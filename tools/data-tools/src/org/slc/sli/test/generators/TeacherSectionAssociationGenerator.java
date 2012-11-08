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

import org.slc.sli.test.edfi.entities.ClassroomPositionType;
import org.slc.sli.test.edfi.entities.Ref;
import org.slc.sli.test.edfi.entities.SectionIdentityType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.StaffIdentityType;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.TeacherSectionAssociation;
import org.slc.sli.test.edfi.entities.meta.TeacherMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;

public class TeacherSectionAssociationGenerator {
    private Random r = new Random(31);

    public TeacherSectionAssociation generate(String teacher, String school, String sectionCode) {

        TeacherSectionAssociation tsa = new TeacherSectionAssociation();

        tsa.setTeacherReference(TeacherGenerator.getTeacherReference(teacher));

        SectionIdentityType secit = new SectionIdentityType();
        secit.setStateOrganizationId(school);
        secit.setUniqueSectionCode(sectionCode);
        SectionReferenceType secrt = new SectionReferenceType();
        secrt.setSectionIdentity(secit);
        tsa.setSectionReference(secrt);

        tsa.setClassroomPosition(ClassroomPositionType.TEACHER_OF_RECORD);

        tsa.setHighlyQualifiedTeacher(r.nextBoolean());

        return tsa;
    }

    public static TeacherSectionAssociation generateLowFi(TeacherMeta teacherMeta, String sectionId) {

        TeacherSectionAssociation teacherSection = new TeacherSectionAssociation();

        // construct and add the teacher reference
        StaffIdentityType staffIdentity = new StaffIdentityType();
        staffIdentity.setStaffUniqueStateId(teacherMeta.id);

//        StaffReferenceType teacherRef = new StaffReferenceType();
//        teacherRef.setStaffIdentity(staffIdentity);
//
//        teacherSection.setTeacherReference(teacherRef);

        teacherSection.setClassroomPosition(ClassroomPositionType.TEACHER_OF_RECORD);

        // construct and add the section references
        SectionIdentityType sectionIdentity = new SectionIdentityType();
        sectionIdentity.setUniqueSectionCode(sectionId);
        sectionIdentity.setStateOrganizationId(teacherMeta.schoolIds.get(0));

//        EducationOrgIdentificationCode edOrgIdCode = new EducationOrgIdentificationCode();
//        edOrgIdCode.setID(sectionId);
//        edOrgIdCode.setIdentificationSystem(EducationOrgIdentificationSystemType.SCHOOL);
//        sectionIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(edOrgIdCode);

		SectionReferenceType sectionRef = new SectionReferenceType();
		sectionRef.setSectionIdentity(sectionIdentity);
		teacherSection.setSectionReference(sectionRef);
        
		if (MetaRelations.TeacherSectionAssociation_Ref) {
			Ref teacherRefer = new Ref(teacherMeta.id);
			StaffReferenceType sRef = new StaffReferenceType();
			sRef.setRef(teacherRefer);
			teacherSection.setTeacherReference(sRef);
		} else {
			StaffReferenceType teacherRef = new StaffReferenceType();
			teacherRef.setStaffIdentity(staffIdentity);

			teacherSection.setTeacherReference(teacherRef);
		}
        teacherSection.setClassroomPosition(ClassroomPositionType.TEACHER_OF_RECORD);

        return teacherSection;
    }
}
