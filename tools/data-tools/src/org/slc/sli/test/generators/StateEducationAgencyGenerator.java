package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.StateEducationAgency;

public class StateEducationAgencyGenerator {

    public static StateEducationAgency getFastStateEducationAgency(String id) {
        StateEducationAgency stateEducationAgency = new StateEducationAgency();
        stateEducationAgency.setStateOrganizationId(id);
        return stateEducationAgency;
    }
}
