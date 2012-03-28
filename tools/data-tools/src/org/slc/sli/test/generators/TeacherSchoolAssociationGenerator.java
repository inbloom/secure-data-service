package org.slc.sli.test.generators;

import java.util.List;

import org.slc.sli.test.edfi.entities.*;

public class TeacherSchoolAssociationGenerator {
    public static TeacherSchoolAssociation generate(String staffId, List<String> stateOrgIds) {
        TeacherSchoolAssociation tsa = new TeacherSchoolAssociation();

        StaffIdentityType sit = new StaffIdentityType();
        sit.setStaffUniqueStateId(staffId);
        StaffReferenceType srt = new StaffReferenceType();
        srt.setStaffIdentity(sit);
        tsa.setTeacherReference(srt);

        EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();

        for (String stateOrgId : stateOrgIds) {
            eoit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(stateOrgId);
        }

        EducationalOrgReferenceType eor = new EducationalOrgReferenceType();
        eor.setEducationalOrgIdentity(eoit);

        tsa.getSchoolReference().add(eor);

        return tsa;
    }
}
