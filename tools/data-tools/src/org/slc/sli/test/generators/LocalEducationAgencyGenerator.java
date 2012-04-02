package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.LocalEducationAgency;

public class LocalEducationAgencyGenerator {

    public static LocalEducationAgency getFastLocalEducationAgency(String id) {
        LocalEducationAgency localEducationAgency = new LocalEducationAgency();
        localEducationAgency.setStateOrganizationId(id);
        return localEducationAgency;
    }

}
