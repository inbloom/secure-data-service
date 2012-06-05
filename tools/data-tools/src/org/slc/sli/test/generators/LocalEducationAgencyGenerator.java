package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.EducationOrganizationCategoriesType;
import org.slc.sli.test.edfi.entities.EducationOrganizationCategoryType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.LEACategoryType;
import org.slc.sli.test.edfi.entities.LocalEducationAgency;
import org.slc.sli.test.edfi.entities.OperationalStatusType;
import org.slc.sli.test.edfi.entities.ProgramReferenceType;
import org.slc.sli.test.edfi.entities.Ref;
import org.slc.sli.test.edfi.entities.meta.LeaMeta;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;

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

        return localEducationAgency;
    }
    
    
    public static LocalEducationAgency generateMedFi(String id, String seaId, LeaMeta leaMeta) {
    	
    	
        LocalEducationAgency localEducationAgency = new LocalEducationAgency();
        localEducationAgency.setId(id);
        localEducationAgency.setStateOrganizationId(id);
        
        //grammar, middle, high, indenpend study programm
        //localEducationAgency.setNameOfInstitution("Institution name " + id);
        //localEducationAgency.setShortNameOfInstitution("Institution " + id);

        localEducationAgency.setNameOfInstitution(id);
        localEducationAgency.setShortNameOfInstitution(id);

        EducationOrganizationCategoriesType category = new EducationOrganizationCategoriesType();
        category.getOrganizationCategory().add(EducationOrganizationCategoryType.LOCAL_EDUCATION_AGENCY);
        localEducationAgency.setOrganizationCategories(category);
        localEducationAgency.setOperationalStatus(OperationalStatusType.ACTIVE);

        localEducationAgency.getAddress().add(AddressGenerator.generateLowFi());

        localEducationAgency.setLEACategory(LEACategoryType.CHARTER);

        // construct and add the SEA reference
        /*
        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
        edOrgIdentityType.setStateOrganizationId(seaId);

        EducationalOrgReferenceType seaRef = new EducationalOrgReferenceType();
        seaRef.setEducationalOrgIdentity(edOrgIdentityType);

        localEducationAgency.setStateEducationAgencyReference(seaRef);
        
        */
        
//        	Ref leaRef = new Ref(leaMeta.id);
//        	EducationalOrgReferenceType eortype = new EducationalOrgReferenceType();
//        	eortype.setRef(leaRef);
//        	localEducationAgency.setLocalEducationAgencyReference(eortype);
        
        	Ref seaRef = new Ref(seaId);
        	EducationalOrgReferenceType eort = new EducationalOrgReferenceType();
        	eort.setRef(seaRef);
        	localEducationAgency.setStateEducationAgencyReference(eort);
        	
        
        for (String pid:leaMeta.programs.keySet()){
        	
     	   ProgramMeta pm = leaMeta.programs.get(pid); 
     	   Ref programRef = new Ref(pm.id);
     	   ProgramReferenceType prt = new ProgramReferenceType();
     	   prt.setRef(programRef);
     	  localEducationAgency.getProgramReference().add(prt);  	  
        }

        return localEducationAgency;
    }
    
}
