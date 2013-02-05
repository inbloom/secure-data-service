/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.AccountabilityRating;
import org.slc.sli.test.edfi.entities.CharterStatusType;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationCode;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationSystemType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCEducationOrganization;
import org.slc.sli.test.edfi.entities.EducationOrganizationCategoriesType;
import org.slc.sli.test.edfi.entities.EducationOrganizationCategoryType;
import org.slc.sli.test.edfi.entities.SLCEducationServiceCenter;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.FeederSchoolAssociation;
import org.slc.sli.test.edfi.entities.InstitutionTelephone;
import org.slc.sli.test.edfi.entities.InstitutionTelephoneNumberType;
import org.slc.sli.test.edfi.entities.LEACategoryType;
import org.slc.sli.test.edfi.entities.SLCLocalEducationAgency;
import org.slc.sli.test.edfi.entities.OperationalStatusType;
import org.slc.sli.test.edfi.entities.SLCStateEducationAgency;
import org.slc.sli.test.edfi.entities.meta.SchoolMeta;

public class EducationAgencyGenerator {

    public SLCStateEducationAgency getSEA(String id) {
        SLCStateEducationAgency agency = new SLCStateEducationAgency();
        if (id != null)
            agency.setId(id);
        EducationOrgIdentificationCode edorgCode = new EducationOrgIdentificationCode();
        edorgCode.setID("NYSEA");
        edorgCode.setIdentificationSystem(EducationOrgIdentificationSystemType.FEDERAL);
        agency.getEducationOrgIdentificationCode().add(edorgCode);
        agency.setNameOfInstitution("New York State Education Agency");
        agency.setShortNameOfInstitution("NYSEA");
        EducationOrganizationCategoriesType catType = new EducationOrganizationCategoriesType();
        catType.getOrganizationCategory().add(EducationOrganizationCategoryType.STATE_EDUCATION_AGENCY);
        agency.setOrganizationCategories(catType);
        agency.getAddress().add(AddressGenerator.getRandomAddress());
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
        agency.getAccountabilityRatings().add(rating);
        // agency.getProgramReference().add(ProgramReferenceType)
        // agency.getEducationOrganizationPeerReference()
        return agency;
    }

    public SLCEducationalOrgReferenceType getEducationalOrgReferenceType(SLCEducationOrganization edOrg) {
        SLCEducationalOrgReferenceType ref = new SLCEducationalOrgReferenceType();
//        SLCEducationalOrgIdentityType identity = new SLCEducationalOrgIdentityType();
//        identity.getEducationOrgIdentificationCode().addAll(edOrg.getEducationOrgIdentificationCode());
        // identity.getStateOrganizationIdOrEducationOrgIdentificationCode().
        // addAll(edOrg.getEducationOrgIdentificationCode());
        return ref;
    }

    public static SLCEducationalOrgReferenceType generateReference(String stateOrganizationId) {
        SLCEducationalOrgReferenceType ref = new SLCEducationalOrgReferenceType();
        SLCEducationalOrgIdentityType identity = new SLCEducationalOrgIdentityType();
        identity.setStateOrganizationId(stateOrganizationId);
        ref.setEducationalOrgIdentity(identity);
        return ref;

    }

    public SLCLocalEducationAgency getLEA(String id) {
        SLCLocalEducationAgency agency = new SLCLocalEducationAgency();
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
        agency.getAddress().add(AddressGenerator.getRandomAddress());
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
        agency.getAccountabilityRatings().add(rating);
        // agency.getProgramReference().add(ProgramReferenceType)
        // agency.getEducationOrganizationPeerReference()
        agency.setLEACategory(LEACategoryType.INDEPENDENT);
        agency.setCharterStatus(CharterStatusType.OPEN_ENROLLMENT);
        // agency.setLocalEducationAgencyReference(EducationalOrgReferenceType) ;
        // agency.setEducationServiceCenterReference(EducationalOrgReferenceType value) ;
        // agency.setStateEducationAgencyReference(EducationalOrgReferenceType value) ;
        return agency;
    }

    public static SLCEducationServiceCenter getEducationServiceCenter(String id, String seaId) {
        SLCEducationServiceCenter agency = new SLCEducationServiceCenter();
        agency.setStateOrganizationId(id);
        agency.setId(id);
        EducationOrgIdentificationCode edorgCode = new EducationOrgIdentificationCode();
        edorgCode.setID(id);
        edorgCode.setIdentificationSystem(EducationOrgIdentificationSystemType.OTHER);
        agency.getEducationOrgIdentificationCode().add(edorgCode);
        agency.setNameOfInstitution(id );
        agency.setShortNameOfInstitution("NYLBPROVS");
        EducationOrganizationCategoriesType catType = new EducationOrganizationCategoriesType();
        catType.getOrganizationCategory().add(EducationOrganizationCategoryType.EDUCATION_SERVICE_CENTER);
        agency.setOrganizationCategories(catType);
        agency.getAddress().add(AddressGenerator.generateLowFi());
        InstitutionTelephone telephone = new InstitutionTelephone();
        telephone.setInstitutionTelephoneNumberType(InstitutionTelephoneNumberType.ADMINISTRATIVE);
        telephone.setTelephoneNumber("212-823-1231");
        agency.getTelephone().add(telephone);
        agency.setWebSite("www.esc" + id + ".com");
        agency.setOperationalStatus(OperationalStatusType.ACTIVE);
        AccountabilityRating rating = new AccountabilityRating();
        rating.setRating("AOK");
        rating.setRatingDate("2011-03-04");
        rating.setRatingOrganization("DoE");
        rating.setRatingProgram("DoER11");
        rating.setRatingTitle("DoER11-XP");
        rating.setSchoolYear("2012-2013");
        agency.getAccountabilityRatings().add(rating);
        // agency.getProgramReference().add(ProgramReferenceType)
        // agency.getEducationOrganizationPeerReference()
        
        // construct and add the SEA reference
        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
        edOrgIdentityType.setStateOrganizationId(seaId);

        EducationalOrgReferenceType seaRef = new EducationalOrgReferenceType();
        seaRef.setEducationalOrgIdentity(edOrgIdentityType);

        agency.setStateEducationAgencyReference(seaRef);
        return agency;
    }
    
    public static FeederSchoolAssociation getFeederSchoolAssociation(SchoolMeta receiver, SchoolMeta feeder) {
        FeederSchoolAssociation feed = new FeederSchoolAssociation();
        
        EducationalOrgIdentityType feederIdentityType = new EducationalOrgIdentityType();
        feederIdentityType.setStateOrganizationId(feeder.id);
        EducationalOrgReferenceType feederRef = new EducationalOrgReferenceType();
        feederRef.setEducationalOrgIdentity(feederIdentityType);
        
        EducationalOrgIdentityType receiverIdentityType = new EducationalOrgIdentityType();
        receiverIdentityType.setStateOrganizationId(receiver.id);
        EducationalOrgReferenceType receiverRef = new EducationalOrgReferenceType();
        receiverRef.setEducationalOrgIdentity(receiverIdentityType);
        
        feed.setFeederSchoolReference(feederRef);
        feed.setReceivingSchoolReference(receiverRef);
        feed.setBeginDate(" 2012-10-10");
        feed.setEndDate("2030-10-10");
        feed.setFeederRelationshipDescription("Feeder Relationship from " + feeder.id + " to " + receiver.id);

        return feed;
    }
}
