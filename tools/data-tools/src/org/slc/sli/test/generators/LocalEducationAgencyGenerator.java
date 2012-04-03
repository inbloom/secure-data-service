package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.LocalEducationAgency;

public class LocalEducationAgencyGenerator {

    public static LocalEducationAgency getFastLocalEducationAgency(String id, String seaId) {

        LocalEducationAgency localEducationAgency = new LocalEducationAgency();
        localEducationAgency.setStateOrganizationId(id);

        // construct and add the SEA reference
        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
        edOrgIdentityType.getStateOrganizationIdOrEducationOrgIdentificationCode().add(seaId);

        EducationalOrgReferenceType seaRef = new EducationalOrgReferenceType();
        seaRef.setEducationalOrgIdentity(edOrgIdentityType);

        localEducationAgency.setStateEducationAgencyReference(seaRef);

        return localEducationAgency;
    }
}
