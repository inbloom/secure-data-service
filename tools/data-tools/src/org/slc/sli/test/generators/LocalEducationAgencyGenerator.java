package org.slc.sli.test.generators;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slc.sli.test.edfi.entities.EducationOrganizationCategoriesType;
import org.slc.sli.test.edfi.entities.EducationOrganizationCategoryType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.LEACategoryType;
import org.slc.sli.test.edfi.entities.LocalEducationAgency;
import org.slc.sli.test.edfi.entities.OperationalStatusType;
import org.slc.sli.test.edfi.entities.meta.ESCMeta;
import org.slc.sli.test.edfi.entities.meta.SeaMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;

public class LocalEducationAgencyGenerator {

    public static LocalEducationAgency generateLowFi(String id, String seaId) {

        LocalEducationAgency localEducationAgency = new LocalEducationAgency();
        localEducationAgency.setId(id);
        localEducationAgency.setStateOrganizationId(id);
        //grammar, middle, high, indenpend study programm
//        localEducationAgency.setNameOfInstitution("Institution name " + id);
//        localEducationAgency.setShortNameOfInstitution("Institution " + id);

        localEducationAgency.setNameOfInstitution(id);
        localEducationAgency.setShortNameOfInstitution(id);

        EducationOrganizationCategoriesType category = new EducationOrganizationCategoriesType();
        category.getOrganizationCategory().add(EducationOrganizationCategoryType.LOCAL_EDUCATION_AGENCY);
        localEducationAgency.setOrganizationCategories(category);
        localEducationAgency.setOperationalStatus(OperationalStatusType.ACTIVE);

        localEducationAgency.getAddress().add(AddressGenerator.generateLowFi());

        localEducationAgency.setLEACategory(LEACategoryType.CHARTER);

        // construct and add the SEA reference
        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
        edOrgIdentityType.setStateOrganizationId(seaId);

        EducationalOrgReferenceType seaRef = new EducationalOrgReferenceType();
        seaRef.setEducationalOrgIdentity(edOrgIdentityType);

        localEducationAgency.setStateEducationAgencyReference(seaRef);

        //associate this lea with a esCenter. A SEA can have multiple esCenters.
        SeaMeta seaMeta = MetaRelations.SEA_MAP.get(seaId);
        if(seaMeta != null) {
            Map<String, ESCMeta> escMetas = seaMeta.escs;
            if(escMetas != null) {
                int escCount = escMetas.size();
                if(escCount > 0) {
                    List<String> escIds = new LinkedList<String>(escMetas.keySet());
                    Collections.shuffle(escIds);
                    String escId = escIds.get(0);
                    
                    EducationalOrgIdentityType escIdentityType = new EducationalOrgIdentityType();
                    escIdentityType.setStateOrganizationId(escId);
                    
                    EducationalOrgReferenceType escRef = new EducationalOrgReferenceType();
                    escRef.setEducationalOrgIdentity(escIdentityType);
                    
                    localEducationAgency.setEducationServiceCenterReference(escRef);
                }
            }
        }
        
        return localEducationAgency;
    }
}
