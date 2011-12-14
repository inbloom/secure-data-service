package org.slc.sli.domain;

import java.util.Date;

import org.slc.sli.domain.enums.LimitedEnglishProficiencyType;
import org.slc.sli.domain.enums.OldEthnicityType;
import org.slc.sli.domain.enums.SchoolFoodServicesEligibilityType;
import org.slc.sli.domain.enums.SexType;

/**
 * Utility class for building student objects
 * 
 */
public class StudentBuilder {
    
    public static Student buildTestStudent() {
        Student student = new Student();
        student.setFirstName("Jane");
        student.setLastSurname("Doe");
        Date birthDate = new Date(23234289);
        student.setBirthDate(birthDate);
        student.setCityOfBirth("Chicago");
        student.setCountryOfBirth("US");
        student.setDateEnteredUs(birthDate);
        student.setDisplacementStatus("some");
        student.setEconomicDisadvantaged(true);
        student.setGenerationCodeSuffix("Z");
        student.setHispanicLatinoEthnicity(true);
        student.setLimitedEnglishProficiency(LimitedEnglishProficiencyType.YES);
        student.setMaidenName("Smith");
        student.setMiddleName("Patricia");
        student.setMultipleBirthStatus(true);
        student.setOldEthnicity(OldEthnicityType.NULL);
        student.setPersonalInformationVerification("verified");
        student.setPersonalTitlePrefix("Miss");
        student.setProfileThumbnail("doej23.png");
        student.setSchoolFoodServicesEligibility(SchoolFoodServicesEligibilityType.FREE);
        student.setSex(SexType.Female);
        student.setStateOfBirthAbbreviation("IL");
        student.setStudentSchoolId("DOE-JANE-222");
        
        return student;
    }
    
    public static Student buildTestStudent2() {
        Student student = new Student();
        student.setFirstName("John");
        student.setLastSurname("Doe");
        Date birthDate = new Date(23234289);
        student.setBirthDate(birthDate);
        student.setCityOfBirth("Chicago");
        student.setCountryOfBirth("US");
        student.setDateEnteredUs(birthDate);
        student.setDisplacementStatus("some");
        student.setEconomicDisadvantaged(true);
        student.setGenerationCodeSuffix("Z");
        student.setHispanicLatinoEthnicity(true);
        student.setLimitedEnglishProficiency(LimitedEnglishProficiencyType.NO);
        student.setMaidenName("Doe");
        student.setMiddleName("Patrick");
        student.setMultipleBirthStatus(true);
        student.setOldEthnicity(OldEthnicityType.NULL);
        student.setPersonalInformationVerification("verified");
        student.setPersonalTitlePrefix("Mr");
        student.setProfileThumbnail("doej23.png");
        student.setSchoolFoodServicesEligibility(SchoolFoodServicesEligibilityType.FULL_PRICE);
        student.setSex(SexType.Male);
        student.setStateOfBirthAbbreviation("IL");
        student.setStudentSchoolId("DOE-JOHN-222");
        
        return student;
    }
    
}
