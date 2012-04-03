package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.EducationOrganizationCategoriesType;
import org.slc.sli.test.edfi.entities.EducationOrganizationCategoryType;
import org.slc.sli.test.edfi.entities.OperationalStatusType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;
import org.slc.sli.test.edfi.entities.StateEducationAgency;

public class StateEducationAgencyGenerator {

    private static AddressGenerator ag;

    public static StateEducationAgency getFastStateEducationAgency(String id) {
        StateEducationAgency stateEducationAgency = new StateEducationAgency();
        stateEducationAgency.setStateOrganizationId(id);
        stateEducationAgency.setNameOfInstitution("Institution name " + id);

        EducationOrganizationCategoriesType category = new EducationOrganizationCategoriesType();
        category.getOrganizationCategory().add(EducationOrganizationCategoryType.STATE_EDUCATION_AGENCY);
        stateEducationAgency.setOrganizationCategories(category);

        stateEducationAgency.getAddress().add(AddressGenerator.getFastAddress());
        return stateEducationAgency;
    }

    public static StateEducationAgency getStateEducationAgency(String id) {
        try {
            if (ag == null)
                ag = new AddressGenerator(StateAbbreviationType.NY);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        StateEducationAgency stateEducationAgency = new StateEducationAgency();
        stateEducationAgency.setStateOrganizationId(id);
        stateEducationAgency.setNameOfInstitution("Institution name " + id);
        stateEducationAgency.setShortNameOfInstitution("Institution " + id);

        EducationOrganizationCategoriesType category = new EducationOrganizationCategoriesType();
        category.getOrganizationCategory().add(EducationOrganizationCategoryType.STATE_EDUCATION_AGENCY);
        stateEducationAgency.setOrganizationCategories(category);
        stateEducationAgency.setOperationalStatus(OperationalStatusType.ACTIVE);

        stateEducationAgency.getAddress().add(ag.getRandomAddress());

        return stateEducationAgency;
    }
}
