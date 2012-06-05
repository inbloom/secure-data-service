package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.EducationOrganizationCategoriesType;
import org.slc.sli.test.edfi.entities.EducationOrganizationCategoryType;
import org.slc.sli.test.edfi.entities.OperationalStatusType;
import org.slc.sli.test.edfi.entities.ProgramReferenceType;
import org.slc.sli.test.edfi.entities.Ref;
import org.slc.sli.test.edfi.entities.StateEducationAgency;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.SeaMeta;

public class StateEducationAgencyGenerator {

    public static StateEducationAgency generateLowFi(String id, SeaMeta seaMeta) {

        StateEducationAgency stateEducationAgency = new StateEducationAgency();
        stateEducationAgency.setId(id);
        stateEducationAgency.setStateOrganizationId(id);

        stateEducationAgency.setNameOfInstitution("Institution name " + id);
        stateEducationAgency.setShortNameOfInstitution("Institution " + id);

        EducationOrganizationCategoriesType category = new EducationOrganizationCategoriesType();
        category.getOrganizationCategory().add(EducationOrganizationCategoryType.STATE_EDUCATION_AGENCY);
        stateEducationAgency.setOrganizationCategories(category);
        stateEducationAgency.setOperationalStatus(OperationalStatusType.ACTIVE);

        stateEducationAgency.getAddress().add(AddressGenerator.generateLowFi());
       int  counter =0;
       for (String pid:seaMeta.programs.keySet()){
    	   ProgramMeta pm = seaMeta.programs.get(pid); 
    	   Ref programRef = new Ref(pm.id);
    	   ProgramReferenceType prt = new ProgramReferenceType();
    	   prt.setRef(programRef);
    	   stateEducationAgency.getProgramReference().add(prt);
       }
        return stateEducationAgency;
    }
}
