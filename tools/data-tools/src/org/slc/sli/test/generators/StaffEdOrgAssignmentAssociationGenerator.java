package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.Ref;
import org.slc.sli.test.edfi.entities.StaffClassificationType;
import org.slc.sli.test.edfi.entities.StaffEducationOrgAssignmentAssociation;
import org.slc.sli.test.edfi.entities.StaffIdentityType;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.meta.StaffMeta;

public class StaffEdOrgAssignmentAssociationGenerator {

    public static StaffEducationOrgAssignmentAssociation generateLowFi(StaffMeta staffMeta) {
        StaffEducationOrgAssignmentAssociation staffEdOrgAssignmentAssoc = new StaffEducationOrgAssignmentAssociation();

        StaffIdentityType staffIdentityType = new StaffIdentityType();
        staffIdentityType.setStaffUniqueStateId(staffMeta.id);

        StaffReferenceType staffReferenceType = new StaffReferenceType();
        Ref staffRef = new Ref(staffMeta.id);
        staffReferenceType.setRef(staffRef);
        staffEdOrgAssignmentAssoc.setStaffReference(staffReferenceType);
        
//        staffReferenceType.setStaffIdentity(staffIdentityType);
//        staffEdOrgAssignmentAssoc.setStaffReference(staffReferenceType);

        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        //edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(staffMeta.edOrgId);
        edOrgIdentity.setStateOrganizationId(staffMeta.edOrgId);

        EducationalOrgReferenceType edOrgReferenceType = new EducationalOrgReferenceType();
        edOrgReferenceType.setEducationalOrgIdentity(edOrgIdentity);

        staffEdOrgAssignmentAssoc.setEducationOrganizationReference(edOrgReferenceType);

        staffEdOrgAssignmentAssoc.setStaffClassification(StaffClassificationType.SPECIALIST_CONSULTANT);
        staffEdOrgAssignmentAssoc.setBeginDate("2012-01-01");

        return staffEdOrgAssignmentAssoc;
    }

}
