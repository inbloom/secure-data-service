package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.EducationOrganizationCategoriesType;
import org.slc.sli.test.edfi.entities.EducationOrganizationCategoryType;
import org.slc.sli.test.edfi.entities.OperationalStatusType;
import org.slc.sli.test.edfi.entities.StateEducationAgency;

public class StateEducationAgencyGenerator {

    public static StateEducationAgency generateLowFi(String id) {

        StateEducationAgency stateEducationAgency = new StateEducationAgency();
        stateEducationAgency.setStateOrganizationId(id);
        stateEducationAgency.setNameOfInstitution("Institution name " + id);
        stateEducationAgency.setShortNameOfInstitution("Institution " + id);

        EducationOrganizationCategoriesType category = new EducationOrganizationCategoriesType();
        category.getOrganizationCategory().add(EducationOrganizationCategoryType.STATE_EDUCATION_AGENCY);
        stateEducationAgency.setOrganizationCategories(category);
        stateEducationAgency.setOperationalStatus(OperationalStatusType.ACTIVE);

        stateEducationAgency.getAddress().add(AddressGenerator.generateLowFi());

        return stateEducationAgency;
    }
}
