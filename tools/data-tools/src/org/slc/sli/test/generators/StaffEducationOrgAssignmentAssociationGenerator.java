package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.StaffClassificationType;
import org.slc.sli.test.edfi.entities.StaffEducationOrgAssignmentAssociation;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.meta.StaffEducationOrgAssignmentAssociationMeta;

public class StaffEducationOrgAssignmentAssociationGenerator {
    private static Random random = new Random();
    private static StaffClassificationType[] staffClassifications = StaffClassificationType.values();

    public static StaffEducationOrgAssignmentAssociation generate(StaffEducationOrgAssignmentAssociationMeta meta) {
        StaffEducationOrgAssignmentAssociation staffEducationOrgAssignmentAssociation = new StaffEducationOrgAssignmentAssociation();

        StaffReferenceType srt = new StaffReferenceType();
        srt.setRef(meta.staffId);
        staffEducationOrgAssignmentAssociation.setStaffReference(srt);

        EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();
        eoit.setStateOrganizationId(meta.edOrgId);
        EducationalOrgReferenceType eort = new EducationalOrgReferenceType();
        eort.setEducationalOrgIdentity(eoit);
        staffEducationOrgAssignmentAssociation.setEducationOrganizationReference(eort);

        staffEducationOrgAssignmentAssociation.setStaffClassification(staffClassifications[random
                .nextInt(staffClassifications.length)]);

        staffEducationOrgAssignmentAssociation.setBeginDate("2011-02-04");
        staffEducationOrgAssignmentAssociation.setEndDate("2012-06-05");

        staffEducationOrgAssignmentAssociation.setPositionTitle("Position Title");

        return staffEducationOrgAssignmentAssociation;
    }

    public static StaffEducationOrgAssignmentAssociation generateLowFi(StaffEducationOrgAssignmentAssociationMeta meta) {
        return generate(meta);
    }
}
