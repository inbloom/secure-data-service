package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.EducationOrganizationCategoriesType;
import org.slc.sli.test.edfi.entities.EducationOrganizationCategoryType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.LEACategoryType;
import org.slc.sli.test.edfi.entities.LocalEducationAgency;
import org.slc.sli.test.edfi.entities.OperationalStatusType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;

public class LocalEducationAgencyGenerator {

    private static AddressGenerator ag;
    
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
    
    public static LocalEducationAgency getLocalEducationAgency(String id, String seaId) {
        try {
            if (ag == null)
                ag = new AddressGenerator(StateAbbreviationType.NY);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        LocalEducationAgency localEducationAgency = new LocalEducationAgency();
        localEducationAgency.setStateOrganizationId(id);
        localEducationAgency.setNameOfInstitution("Institution name " + id);
        localEducationAgency.setShortNameOfInstitution("Institution " + id);

        EducationOrganizationCategoriesType category = new EducationOrganizationCategoriesType();
        category.getOrganizationCategory().add(EducationOrganizationCategoryType.LOCAL_EDUCATION_AGENCY);
        localEducationAgency.setOrganizationCategories(category) ;
        localEducationAgency.setOperationalStatus(OperationalStatusType.ACTIVE) ;

        localEducationAgency.getAddress().add(ag.getRandomAddress());

        localEducationAgency.setLEACategory(LEACategoryType.CHARTER);
        
        // construct and add the SEA reference
        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
        edOrgIdentityType.getStateOrganizationIdOrEducationOrgIdentificationCode().add(seaId);

        EducationalOrgReferenceType seaRef = new EducationalOrgReferenceType();
        seaRef.setEducationalOrgIdentity(edOrgIdentityType);

        localEducationAgency.setStateEducationAgencyReference(seaRef);

        return localEducationAgency;
    }
}
