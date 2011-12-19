package org.slc.sli.domain;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.slc.sli.domain.enums.LimitedEnglishProficiencyType;
import org.slc.sli.domain.enums.OldEthnicityType;
import org.slc.sli.domain.enums.SchoolFoodServicesEligibilityType;
import org.slc.sli.domain.enums.SexType;

/**
 * Student Entity.
 * 
 * NOTE: These strongly typed domain classes are deprecated and should no longer be used.
 * Please use see the Entity interface for their replacement.
 * 
 * Entities should try to be as vanilla as possible. Ideally, persistence annotations and
 * marshalling hints to JSON and XML libraries will be in packages other than
 * domain.
 * 
 * TODO: replace Strings with Enums, add missing data
 */

@Deprecated
@XmlRootElement(name = "student")
public class Student {
    /** Serial version */
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1318365525782L;
    
    /** The value for the studentId field */
    
    private Integer studentId;
    
    /** The value for the studentSchoolId field */
    private String studentSchoolId;
    
    /** The value for the personalTitlePrefix field */
    private String personalTitlePrefix;
    
    /** The value for the firstName field */
    private String firstName;
    
    /** The value for the middleName field */
    private String middleName;
    
    /** The value for the lastSurname field */
    private String lastSurname;
    
    /** The value for the generationCodeSuffix field */
    private String generationCodeSuffix;
    
    /** The value for the maidenName field */
    private String maidenName;
    
    /** The value for the personalInformationVerification field */
    private String personalInformationVerification;
    
    /** The value for the sex field */
    private SexType sex;
    
    /** The value for the birthDate field */
    private Date birthDate;
    
    /** The value for the cityOfBirth field */
    private String cityOfBirth;
    
    /** The value for the stateOfBirthAbbreviation field */
    private String stateOfBirthAbbreviation;
    
    /** The value for the countryOfBirth field */
    private String countryOfBirth;
    
    /** The value for the dateEnteredUs field */
    private Date dateEnteredUs;
    
    /** The value for the multipleBirthStatus field */
    private Boolean multipleBirthStatus;
    
    /** The value for the profileThumbnail field */
    private String profileThumbnail;
    
    /** The value for the hispanicLatinoEthnicity field */
    private Boolean hispanicLatinoEthnicity;
    
    /** The value for the oldEthnicity field */
    private OldEthnicityType oldEthnicity;
    
    /** The value for the economicDisadvantaged field */
    private Boolean economicDisadvantaged;
    
    /** The value for the schoolFoodServicesEligibility field */
    private SchoolFoodServicesEligibilityType schoolFoodServicesEligibility;
    
    /** The value for the limitedEnglishProficiency field */
    private LimitedEnglishProficiencyType limitedEnglishProficiency;
    
    /** The value for the displacementStatus field */
    private String displacementStatus;
    
    public Integer getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }
    
    public String getStudentSchoolId() {
        return studentSchoolId;
    }
    
    public void setStudentSchoolId(String studentSchoolId) {
        this.studentSchoolId = studentSchoolId;
    }
    
    public String getPersonalTitlePrefix() {
        return personalTitlePrefix;
    }
    
    public void setPersonalTitlePrefix(String personalTitlePrefix) {
        this.personalTitlePrefix = personalTitlePrefix;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    public String getLastSurname() {
        return lastSurname;
    }
    
    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }
    
    public String getGenerationCodeSuffix() {
        return generationCodeSuffix;
    }
    
    public void setGenerationCodeSuffix(String generationCodeSuffix) {
        this.generationCodeSuffix = generationCodeSuffix;
    }
    
    public String getMaidenName() {
        return maidenName;
    }
    
    public void setMaidenName(String maidenName) {
        this.maidenName = maidenName;
    }
    
    public String getPersonalInformationVerification() {
        return personalInformationVerification;
    }
    
    public void setPersonalInformationVerification(String personalInformationVerification) {
        this.personalInformationVerification = personalInformationVerification;
    }
    
    public SexType getSex() {
        return sex;
    }
    
    public void setSex(SexType sex) {
        this.sex = sex;
    }
    
    public Date getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    
    public String getCityOfBirth() {
        return cityOfBirth;
    }
    
    public void setCityOfBirth(String cityOfBirth) {
        this.cityOfBirth = cityOfBirth;
    }
    
    public String getStateOfBirthAbbreviation() {
        return stateOfBirthAbbreviation;
    }
    
    public void setStateOfBirthAbbreviation(String stateOfBirthAbbreviation) {
        this.stateOfBirthAbbreviation = stateOfBirthAbbreviation;
    }
    
    public String getCountryOfBirth() {
        return countryOfBirth;
    }
    
    public void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }
    
    public Date getDateEnteredUs() {
        return dateEnteredUs;
    }
    
    public void setDateEnteredUs(Date dateEnteredUs) {
        this.dateEnteredUs = dateEnteredUs;
    }
    
    public String getProfileThumbnail() {
        return profileThumbnail;
    }
    
    public void setProfileThumbnail(String profileThumbnail) {
        this.profileThumbnail = profileThumbnail;
    }
    
    /**
     * @return the multipleBirthStatus
     */
    public Boolean getMultipleBirthStatus() {
        return multipleBirthStatus;
    }
    
    /**
     * @param multipleBirthStatus
     *            the multipleBirthStatus to set
     */
    public void setMultipleBirthStatus(Boolean multipleBirthStatus) {
        this.multipleBirthStatus = multipleBirthStatus;
    }
    
    /**
     * @return the hispanicLatinoEthnicity
     */
    public Boolean getHispanicLatinoEthnicity() {
        return hispanicLatinoEthnicity;
    }
    
    /**
     * @param hispanicLatinoEthnicity
     *            the hispanicLatinoEthnicity to set
     */
    public void setHispanicLatinoEthnicity(Boolean hispanicLatinoEthnicity) {
        this.hispanicLatinoEthnicity = hispanicLatinoEthnicity;
    }
    
    public OldEthnicityType getOldEthnicity() {
        return oldEthnicity;
    }
    
    public void setOldEthnicity(OldEthnicityType oldEthnicity) {
        this.oldEthnicity = oldEthnicity;
    }
    
    /**
     * @return the economicDisadvantaged
     */
    public Boolean getEconomicDisadvantaged() {
        return economicDisadvantaged;
    }
    
    /**
     * @param economicDisadvantaged
     *            the economicDisadvantaged to set
     */
    public void setEconomicDisadvantaged(Boolean economicDisadvantaged) {
        this.economicDisadvantaged = economicDisadvantaged;
    }
    
    public SchoolFoodServicesEligibilityType getSchoolFoodServicesEligibility() {
        return schoolFoodServicesEligibility;
    }
    
    public void setSchoolFoodServicesEligibility(SchoolFoodServicesEligibilityType schoolFoodServicesEligibility) {
        this.schoolFoodServicesEligibility = schoolFoodServicesEligibility;
    }
    
    public LimitedEnglishProficiencyType getLimitedEnglishProficiency() {
        return limitedEnglishProficiency;
    }
    
    public void setLimitedEnglishProficiency(LimitedEnglishProficiencyType limitedEnglishProficiency) {
        this.limitedEnglishProficiency = limitedEnglishProficiency;
    }
    
    public String getDisplacementStatus() {
        return displacementStatus;
    }
    
    public void setDisplacementStatus(String displacementStatus) {
        this.displacementStatus = displacementStatus;
    }
    
    // TODO need to confirm this is a valid approach.
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Student [studentId=");
        builder.append(studentId);
        builder.append(", studentSchoolId=");
        builder.append(studentSchoolId);
        builder.append(", personalTitlePrefix=");
        builder.append(personalTitlePrefix);
        builder.append(", firstName=");
        builder.append(firstName);
        builder.append(", middleName=");
        builder.append(middleName);
        builder.append(", lastSurname=");
        builder.append(lastSurname);
        builder.append(", generationCodeSuffix=");
        builder.append(generationCodeSuffix);
        builder.append(", maidenName=");
        builder.append(maidenName);
        builder.append(", personalInformationVerification=");
        builder.append(personalInformationVerification);
        builder.append(", sex=");
        builder.append(sex);
        builder.append(", birthDate=");
        builder.append(birthDate);
        builder.append(", cityOfBirth=");
        builder.append(cityOfBirth);
        builder.append(", stateOfBirthAbbreviation=");
        builder.append(stateOfBirthAbbreviation);
        builder.append(", countryOfBirth=");
        builder.append(countryOfBirth);
        builder.append(", dateEnteredUs=");
        builder.append(dateEnteredUs);
        builder.append(", multipleBirthStatus=");
        builder.append(multipleBirthStatus);
        builder.append(", profileThumbnail=");
        builder.append(profileThumbnail);
        builder.append(", hispanicLatinoEthnicity=");
        builder.append(hispanicLatinoEthnicity);
        builder.append(", oldEthnicity=");
        builder.append(oldEthnicity);
        builder.append(", economicDisadvantaged=");
        builder.append(economicDisadvantaged);
        builder.append(", schoolFoodServicesEligibility=");
        builder.append(schoolFoodServicesEligibility);
        builder.append(", limitedEnglishProficiency=");
        builder.append(limitedEnglishProficiency);
        builder.append(", displacementStatus=");
        builder.append(displacementStatus);
        builder.append("]");
        return builder.toString();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((studentId == null) ? 0 : studentId.hashCode());
        return result;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Student other = (Student) obj;
        if (studentId == null) {
            if (other.studentId != null)
                return false;
        } else if (!studentId.equals(other.studentId))
            return false;
        return true;
    }
    
}
