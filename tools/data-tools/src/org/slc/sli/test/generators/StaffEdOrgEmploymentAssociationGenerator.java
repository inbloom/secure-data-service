package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.EmploymentPeriod;
import org.slc.sli.test.edfi.entities.EmploymentStatusType;
import org.slc.sli.test.edfi.entities.StaffEducationOrgEmploymentAssociation;
import org.slc.sli.test.edfi.entities.StaffIdentityType;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.meta.StaffMeta;

public class StaffEdOrgEmploymentAssociationGenerator {

    public static StaffEducationOrgEmploymentAssociation generateLowFi(StaffMeta staffMeta) {
        StaffEducationOrgEmploymentAssociation staffEdOrgEmploymentAssoc = new StaffEducationOrgEmploymentAssociation();

        StaffIdentityType staffIdentityType = new StaffIdentityType();
        staffIdentityType.setStaffUniqueStateId(staffMeta.id);

        StaffReferenceType staffReferenceType = new StaffReferenceType();
        staffReferenceType.setStaffIdentity(staffIdentityType);

        staffEdOrgEmploymentAssoc.setStaffReference(staffReferenceType);

        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(staffMeta.edOrgId);

        EducationalOrgReferenceType edOrgReferenceType = new EducationalOrgReferenceType();
        edOrgReferenceType.setEducationalOrgIdentity(edOrgIdentity);

        staffEdOrgEmploymentAssoc.setEducationOrganizationReference(edOrgReferenceType);

        staffEdOrgEmploymentAssoc.setEmploymentStatus(EmploymentStatusType.CONTRACTUAL);
        EmploymentPeriod employmentPeriod = new EmploymentPeriod();
        employmentPeriod.setHireDate("2012-01-01");
        staffEdOrgEmploymentAssoc.setEmploymentPeriod(employmentPeriod);

        return staffEdOrgEmploymentAssoc;
    }

}
