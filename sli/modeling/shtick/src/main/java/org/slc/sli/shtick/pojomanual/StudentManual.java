package org.slc.sli.shtick.pojomanual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.shtick.Coercions;
import org.slc.sli.shtick.pojo.Address;
import org.slc.sli.shtick.pojo.BirthData;
import org.slc.sli.shtick.pojo.CohortYear;
import org.slc.sli.shtick.pojo.Disability;
import org.slc.sli.shtick.pojo.DisplacementStatusType;
import org.slc.sli.shtick.pojo.ElectronicMail;
import org.slc.sli.shtick.pojo.GradeLevelType;
import org.slc.sli.shtick.pojo.IdentificationCode;
import org.slc.sli.shtick.pojo.LanguageItemType;
import org.slc.sli.shtick.pojo.LearningStyles;
import org.slc.sli.shtick.pojo.LimitedEnglishProficiencyType;
import org.slc.sli.shtick.pojo.OldEthnicityType;
import org.slc.sli.shtick.pojo.OtherName;
import org.slc.sli.shtick.pojo.ProfileThumbnail;
import org.slc.sli.shtick.pojo.ProgramParticipation;
import org.slc.sli.shtick.pojo.RaceItemType;
import org.slc.sli.shtick.pojo.SchoolFoodServicesEligibilityType;
import org.slc.sli.shtick.pojo.Section504DisabilityItemType;
import org.slc.sli.shtick.pojo.SexType;
import org.slc.sli.shtick.pojo.StudentCharacteristic;
import org.slc.sli.shtick.pojo.StudentIdentificationCode;
import org.slc.sli.shtick.pojo.StudentIndicator;
import org.slc.sli.shtick.pojo.Telephone;
import org.slc.sli.shtick.pojo.UniqueStateIdentifier;

/**
 * This entity represents an individual for whom instruction, services and/or care are provided in
 * an early childhood, elementary or secondary educational program under the jurisdiction of a
 * school, education agency, or other institution or program. A student is a person who has been
 * enrolled in a school or other educational institution.
 */
public final class StudentManual {
    private final Map<String, Object> data;

    public StudentManual(final Map<String, Object> data) {
        this.data = data;
    }

    public String getId() {
        return (String) data.get("id");
    }

    public Map<String, Object> getUnderlying() {
        return data;
    }

    /**
     * A unique number or alphanumeric code assigned to a student by a state education agency.
     */
    public UniqueStateIdentifier getStudentUniqueStateId() {
        return new UniqueStateIdentifier((String) data.get("studentUniqueStateId"));
    }

    public void setUniqueStateIdentifier(UniqueStateIdentifier uniqueStateIdentifier) {
        this.data.put("studentUniqueStateId", uniqueStateIdentifier.getValue());
    }

    /**
     * A coding scheme that is used for identification and record-keeping purposes by schools,
     * social services, or other agencies to refer to a student.
     */
    public List<StudentIdentificationCode> getStudentIdentificationCode() {
        return null;
    }

    /**
     * Full legal name of the person.
     */
    public NameManual getName() {
        return new NameManual(Coercions.toMap(data.get("name")));
    }

    /**
     * Other names (e.g., alias, nickname, previous legal name) associated with a person.
     */
    public List<OtherName> getOtherName() {
        return null;
    }

    /**
     * A person's gender.
     */
    public SexType getSex() {
        return SexType.valueOfName((String) data.get("sex"));
    }

    public void setSex(SexType sex) {
        this.data.put("sex", sex.getName());
    }

    /**
     * The set of elements that capture relevant data regarding a person's birth, including birth
     * date and place of birth.
     */
    public BirthData getBirthData() {
        return new BirthData(Coercions.toMap(data.get("birthData")));
    }

    public void setBirthData(BirthDataManual birthData) {
        this.data.put("birthData", birthData.getUnderlying());
    }

    /**
     * The set of elements that describes an address, including the street address, city, state, and
     * ZIP code.
     */
    public List<Address> getAddress() {
        return null;
    }

    /**
     * The 10-digit telephone number, including the area code, for the person.
     */
    public List<Telephone> getTelephone() {
        return null;
    }

    /**
     * The numbers, letters, and symbols used to identify an electronic mail (e-mail) user within
     * the network to which the individual or organization belongs.
     */
    public List<ElectronicMail> getElectronicMail() {
        return null;
    }

    /**
     * Locator for the student photo.
     */
    public ProfileThumbnail getProfileThumbnail() {
        return new ProfileThumbnail((String) data.get("profileThumbnail"));
    }

    /**
     * An indication that the individual traces his or her origin or descent to Mexico, Puerto Rico,
     * Cuba, Central and South America, and other Spanish cultures, regardless of race. The term,
     * "Spanish origin," can be used in addition to "Hispanic or Latino."
     */
    public Boolean getHispanicLatinoEthnicity() {
        return (Boolean) data.get("hispanicLatinoEthnicity");
    }

    public void setHispanicLatinoEthnicity(Boolean hispanicLatinoEthnicity) {
        this.data.put("hispanicLatinoEthnicity", hispanicLatinoEthnicity);
    }

    /**
     * Previous definition of Ethnicity combining Hispanic/Latino and race: 1 - American Indian or
     * Alaskan Native 2 - Asian or Pacific Islander 3 - Black, not of Hispanic origin 4 - Hispanic 5
     * - White, not of Hispanic origin
     */
    public OldEthnicityType getOldEthnicity() {
        return OldEthnicityType.valueOfName((String) data.get("oldEthnicity"));
    }

    /**
     * The general racial category which most clearly reflects the individual's recognition of his
     * or her community or with which the individual most identifies. The way this data element is
     * listed, it must allow for multiple entries so that each individual can specify all
     * appropriate races.
     */
    public List<RaceItemType> getRace() {
        final List<RaceItemType> list = new ArrayList<RaceItemType>();
        return list;
    }

    /**
     * An indication of inadequate financial condition of an individual's family, as determined by
     * family income, number of family members/dependents, participation in public assistance
     * programs, and/or other characteristics considered relevant by federal, state, and local
     * policy.
     */
    public Boolean getEconomicDisadvantaged() {
        return (Boolean) data.get("economicDisadvantaged");
    }

    /**
     * An indication of a student's level of eligibility for breakfast, lunch, snack, supper, and
     * milk programs.
     */
    public SchoolFoodServicesEligibilityType getSchoolFoodServicesEligibility() {
        return SchoolFoodServicesEligibilityType.valueOfName((String) data.get("schoolFoodServicesEligibility"));
    }

    /**
     * Reflects important characteristics of the student's home situation: such as Displaced
     * Homemaker, Immigrant, Migratory, Military Parent, Pregnant Teen, Single Parent, Unaccompanied
     * Youth, etc.
     */
    public List<StudentCharacteristic> getStudentCharacteristics() {
        return null;
    }

    /**
     * An indication that the student has been identified as limited English proficient by the
     * Language Proficiency Assessment Committee (LPAC), or English proficient.
     */
    public LimitedEnglishProficiencyType getLimitedEnglishProficiency() {
        return LimitedEnglishProficiencyType.valueOfName((String) data.get("limitedEnglishProficiency"));
    }

    /**
     * Language(s) the individual uses to communicate
     */
    public List<LanguageItemType> getLanguages() {
        final List<LanguageItemType> list = new ArrayList<LanguageItemType>();
        return list;
    }

    /**
     * The language or dialect routinely spoken in an individual's home. This language or dialect
     * may or may not be an individual's native language.
     */
    public List<LanguageItemType> getHomeLanguages() {
        final List<LanguageItemType> list = new ArrayList<LanguageItemType>();
        return list;
    }

    /**
     * The disability condition(s) that best describes an individual's impairment.
     */
    public List<Disability> getDisabilities() {
        return null;
    }

    /**
     * A categorization of the disabilities associated with a student pursuant to Section 504.
     */
    public List<Section504DisabilityItemType> getSection504Disabilities() {
        final List<Section504DisabilityItemType> list = new ArrayList<Section504DisabilityItemType>();
        return list;
    }

    /**
     * Indicates a state health or weather related event that displaces a group of students, and may
     * require additional funding, educational, or social services.
     */
    public DisplacementStatusType getDisplacementStatus() {
        return new DisplacementStatusType((String) data.get("displacementStatus"));
    }

    /**
     * Key programs the student is participating in or receives services.
     */
    public List<ProgramParticipation> getProgramParticipations() {
        return null;
    }

    /**
     * The student's relative preference to visual, auditory and tactile learning expressed as
     * percentages.
     */
    public LearningStyles getLearningStyles() {
        return new LearningStyles(Coercions.toMap(data.get("learningStyles")));
    }

    /**
     * The type and year of a cohort (e.g., 9th grade) the student belongs to as determined by the
     * year that student entered a specific grade.
     */
    public List<CohortYear> getCohortYears() {
        return null;
    }

    /**
     * Indicator(s) or metric(s) computed for the student (e.g., at risk) to influence more
     * effective education or direct specific interventions.
     */
    public List<StudentIndicator> getStudentIndicators() {
        return null;
    }

    /**
     * The login ID for the user; used for security access control interface.
     */
    public IdentificationCode getLoginId() {
        return new IdentificationCode((String) data.get("loginId"));
    }

    /**
     * Current grade level
     */
    public GradeLevelType getGradeLevel() {
        return GradeLevelType.valueOfName((String) data.get("gradeLevel"));
    }

    /**
     * Current school
     */
    public String getSchoolId() {
        return (String) data.get("schoolId");
    }

    public void setName(NameManual name) {
        this.data.put("name", name.getUnderlying());
    }
}
