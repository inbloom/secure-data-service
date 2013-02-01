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

import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.StaffClassificationType;
import org.slc.sli.test.edfi.entities.StaffEducationOrgAssignmentAssociation;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.meta.StaffEducationOrgAssignmentAssociationMeta;

public class StaffEducationOrgAssignmentAssociationGenerator {
    private static Random random = new Random(31);
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
