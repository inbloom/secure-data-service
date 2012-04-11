package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.AccountabilityRating;
import org.slc.sli.test.edfi.entities.CharterStatusType;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationCode;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationSystemType;
import org.slc.sli.test.edfi.entities.EducationOrganization;
import org.slc.sli.test.edfi.entities.EducationOrganizationCategoriesType;
import org.slc.sli.test.edfi.entities.EducationOrganizationCategoryType;
import org.slc.sli.test.edfi.entities.EducationServiceCenter;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.InstitutionTelephone;
import org.slc.sli.test.edfi.entities.InstitutionTelephoneNumberType;
import org.slc.sli.test.edfi.entities.LEACategoryType;
import org.slc.sli.test.edfi.entities.LocalEducationAgency;
import org.slc.sli.test.edfi.entities.OperationalStatusType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;
import org.slc.sli.test.edfi.entities.StateEducationAgency;

public class EducationAgencyGenerator {

    private AddressGenerator nyAddressGen;

    public EducationAgencyGenerator() throws Exception {
        nyAddressGen = new AddressGenerator(StateAbbreviationType.NY);
    }

    public StateEducationAgency getSEA(String id) {
        StateEducationAgency agency = new StateEducationAgency();
        if(id != null) agency.setId(id); 
        EducationOrgIdentificationCode edorgCode = new EducationOrgIdentificationCode();
        edorgCode.setID("NYSEA");
        edorgCode.setIdentificationSystem(EducationOrgIdentificationSystemType.FEDERAL);
        agency.getEducationOrgIdentificationCode().add(edorgCode);
        agency.setNameOfInstitution("New York State Education Agency");
        agency.setShortNameOfInstitution("NYSEA");
        EducationOrganizationCategoriesType catType = new EducationOrganizationCategoriesType();
        catType.getOrganizationCategory().add(EducationOrganizationCategoryType.STATE_EDUCATION_AGENCY);
        agency.setOrganizationCategories(catType);
        agency.getAddress().add(nyAddressGen.getRandomAddress());
        InstitutionTelephone telephone = new InstitutionTelephone();
        telephone.setInstitutionTelephoneNumberType(InstitutionTelephoneNumberType.ADMINISTRATIVE);
        telephone.setTelephoneNumber("212-816-1213");
        agency.getTelephone().add(telephone);
        agency.setWebSite("www.nyStateBoardOfEducation.edu");
        agency.setOperationalStatus(OperationalStatusType.ACTIVE);
        AccountabilityRating rating = new AccountabilityRating();
        rating.setRating("AverageSATScore");
        rating.setRatingDate("2011-03-04");
        rating.setRatingOrganization("Federal Board Of Education");
        rating.setRatingProgram("Federa; Board Rating Program");
        rating.setRatingTitle("State SAT Rating");
        rating.setSchoolYear("2012");
        agency.getAccountabilityRatings().add(rating) ;
        //agency.getProgramReference().add(ProgramReferenceType)
        //agency.getEducationOrganizationPeerReference()
        return agency;
    }
    
    public EducationalOrgReferenceType getEducationalOrgReferenceType(EducationOrganization edOrg)
    {
    	EducationalOrgReferenceType ref = new EducationalOrgReferenceType();
    	EducationalOrgIdentityType identity = new EducationalOrgIdentityType();
    	identity.getStateOrganizationIdOrEducationOrgIdentificationCode().
        	addAll(edOrg.getEducationOrgIdentificationCode());
    	return ref;
    }
    
    public LocalEducationAgency getLEA(String id)
    {
        LocalEducationAgency agency = new LocalEducationAgency();
        agency.setStateOrganizationId("ManhattanLEA");
        agency.setId(id);
        EducationOrgIdentificationCode edorgCode = new EducationOrgIdentificationCode();
        edorgCode.setID("ManhattanLEA");
        edorgCode.setIdentificationSystem(EducationOrgIdentificationSystemType.LEA);
        agency.getEducationOrgIdentificationCode().add(edorgCode);
        agency.setNameOfInstitution("Manhattan Education Agency");
        agency.setShortNameOfInstitution("MSEA");
        EducationOrganizationCategoriesType catType = new EducationOrganizationCategoriesType();
        catType.getOrganizationCategory().add(EducationOrganizationCategoryType.LOCAL_EDUCATION_AGENCY);
        agency.setOrganizationCategories(catType);
        agency.getAddress().add(nyAddressGen.getRandomAddress());
        InstitutionTelephone telephone = new InstitutionTelephone();
        telephone.setInstitutionTelephoneNumberType(InstitutionTelephoneNumberType.ADMINISTRATIVE);
        telephone.setTelephoneNumber("212-531-3213");
        agency.getTelephone().add(telephone);
        agency.setWebSite("www.manhattanEducationBoard.edu");
        agency.setOperationalStatus(OperationalStatusType.ACTIVE);
        AccountabilityRating rating = new AccountabilityRating();
        rating.setRating("AverageSATScore");
        rating.setRatingDate("2011-03-04");
        rating.setRatingOrganization("NY Board Of Education");
        rating.setRatingProgram("NY Board Rating Program");
        rating.setRatingTitle("NY SAT Rating");
        rating.setSchoolYear("2012");
        agency.getAccountabilityRatings().add(rating) ;
        //agency.getProgramReference().add(ProgramReferenceType)
        //agency.getEducationOrganizationPeerReference()
        agency.setLEACategory(LEACategoryType.INDEPENDENT) ;
        agency.setCharterStatus(CharterStatusType.OPEN_ENROLLMENT);
        //agency.setLocalEducationAgencyReference(EducationalOrgReferenceType) ;
        //agency.setEducationServiceCenterReference(EducationalOrgReferenceType value) ;
        //agency.setStateEducationAgencyReference(EducationalOrgReferenceType value) ;
        return agency;
    }

    public EducationServiceCenter getEducationServiceCenter(String id)
    {
        EducationServiceCenter agency = new EducationServiceCenter();
        //agency.setStateOrganizationId(String value)
        agency.setId(id);
        EducationOrgIdentificationCode edorgCode = new EducationOrgIdentificationCode();
        edorgCode.setID("FOOD");
        edorgCode.setIdentificationSystem(EducationOrgIdentificationSystemType.OTHER);
        agency.getEducationOrgIdentificationCode().add(edorgCode);
        agency.setNameOfInstitution("New York Lunch Box Providers");
        agency.setShortNameOfInstitution("NYLBPROVS");
        EducationOrganizationCategoriesType catType = new EducationOrganizationCategoriesType();
        catType.getOrganizationCategory().add(EducationOrganizationCategoryType.EDUCATION_SERVICE_CENTER);
        agency.setOrganizationCategories(catType);
        agency.getAddress().add(nyAddressGen.getRandomAddress());
        InstitutionTelephone telephone = new InstitutionTelephone();
        telephone.setInstitutionTelephoneNumberType(InstitutionTelephoneNumberType.ADMINISTRATIVE);
        telephone.setTelephoneNumber("212-823-1231");
        agency.getTelephone().add(telephone);
        agency.setWebSite("www.nyLunchBoxProviders.com");
        agency.setOperationalStatus(OperationalStatusType.ACTIVE);
        AccountabilityRating rating = new AccountabilityRating();
        rating.setRating("Hygiene OK");
        rating.setRatingDate("2011-03-04");
        rating.setRatingOrganization("New York State Health Inspector");
        rating.setRatingProgram("NY School Food Rating");
        rating.setRatingTitle("Food Service Rating");
        rating.setSchoolYear("2012");
        agency.getAccountabilityRatings().add(rating) ;
        //agency.getProgramReference().add(ProgramReferenceType)
        //agency.getEducationOrganizationPeerReference()
        return agency;
    }
}
