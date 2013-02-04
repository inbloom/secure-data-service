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
        //edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(staffMeta.edOrgId);
        edOrgIdentity.setStateOrganizationId(staffMeta.edOrgId);

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
