package org.slc.sli.test.generators;

import java.util.List;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.AcademicSubjectsType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.GradeLevelsType;
import org.slc.sli.test.edfi.entities.ProgramAssignmentType;
import org.slc.sli.test.edfi.entities.StaffIdentityType;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.TeacherSchoolAssociation;
import org.slc.sli.test.edfi.entities.meta.TeacherMeta;

public class TeacherSchoolAssociationGenerator {
    public TeacherSchoolAssociation generate(String staffId, List<String> stateOrgIds) {
        TeacherSchoolAssociation tsa = new TeacherSchoolAssociation();

        tsa.setTeacherReference(TeacherGenerator.getTeacherReference(staffId));

        EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();

        for (String stateOrgId : stateOrgIds) {
            eoit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(stateOrgId);
        }

        EducationalOrgReferenceType eor = new EducationalOrgReferenceType();
        eor.setEducationalOrgIdentity(eoit);
        tsa.getSchoolReference().add(eor);

        tsa.setProgramAssignment(ProgramAssignmentType.REGULAR_EDUCATION);

        GradeLevelsType glt = new GradeLevelsType();
        glt.getGradeLevel().add(GradeLevelType.EARLY_EDUCATION);
        tsa.setInstructionalGradeLevels(glt);

        AcademicSubjectsType ast = new AcademicSubjectsType();
        ast.getAcademicSubject().add(AcademicSubjectType.COMPUTER_AND_INFORMATION_SCIENCES);
        tsa.setAcademicSubjects(ast);
        return tsa;
    }

    public static TeacherSchoolAssociation generateLowFi(TeacherMeta teacherMeta, String schoolId) {

        TeacherSchoolAssociation teacherSchool = new TeacherSchoolAssociation();

        // construct and add the school references
        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);

        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);

        teacherSchool.getSchoolReference().add(schoolRef);

        // construct and add the teacher reference
        StaffIdentityType staffIdentity = new StaffIdentityType();
        staffIdentity.setStaffUniqueStateId(teacherMeta.id);

        StaffReferenceType teacherRef = new StaffReferenceType();
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
