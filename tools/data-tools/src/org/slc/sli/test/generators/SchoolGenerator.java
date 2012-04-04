package org.slc.sli.test.generators;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.AdministrativeFundingControlType;
import org.slc.sli.test.edfi.entities.CharterStatusType;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationCode;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationSystemType;
import org.slc.sli.test.edfi.entities.EducationOrganizationCategoriesType;
import org.slc.sli.test.edfi.entities.EducationOrganizationCategoryType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.GradeLevelsType;
import org.slc.sli.test.edfi.entities.MagnetSpecialProgramEmphasisSchoolType;
import org.slc.sli.test.edfi.entities.OperationalStatusType;
import org.slc.sli.test.edfi.entities.School;
import org.slc.sli.test.edfi.entities.SchoolCategoriesType;
import org.slc.sli.test.edfi.entities.SchoolCategoryItemType;
import org.slc.sli.test.edfi.entities.SchoolType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;
import org.slc.sli.test.edfi.entities.TitleIPartASchoolDesignationType;

public class SchoolGenerator {

    private static final Logger log = Logger.getLogger(SchoolGenerator.class);
    private List<School> schools = null;
    private int schoolPtr = 0;
    private int schoolCount = 0;
    private AddressGenerator ag;
    private static AddressGenerator staticAg = null;

    private String[] schoolNames = { "Academy for Language and Technology ",
            "Academy for Scholarship and Entrepreneurship: A College Board School ", "Academy of Mount Saint Ursula ",
            "Alfred E. Smith Career and Technical Education High School", "All Hallows High School ",
            "Aquinas High School ", "Astor Collegiate Academy", "Astor Collegiate Academy",
            "Banana Kelly High School ", "Belmont Preparatory High School", "Cardinal Hayes High School ",
            "Cardinal Spellman High School ", "Christopher Columbus High School",
            "Community School for Social Justice ", "Crotona Academy High School ", "DeWitt Clinton High School ",
            "Discovery High School ", "DreamYard Preparatory School", "Eagle Academy for Young Men ",
            "East Madison Academy for the Future ", "Eximius College Preparatory Academy: A College Board School ",
            "Explorations Academy ", "Fannie Lou Hamer Freedom High School ", "Fordham High School for the Arts",
            "Fordham Leadership Academy for Business and Technology", "Fordham Preparatory School ",
            "Foreign Language Academy of Global Studies (FLAGS) ", "Frederick Douglass Academy III Secondary School ",
            "Gateway School for Environmental Research and Technology",
            "Gateway School for Environmental Research and Technology", "Global Enterprise High School",
            "Global Enterprise High School", "Grace Dodge Career and Technical Education High School ",
            "Harry S Truman High School ", "Health Opportunities High School ", "Herbert H. Lehman High School",
            "High School for Contemporary Arts", "High School for Contemporary Arts", "High School for Teaching a",
            "High School of Computers and Technology", "Madison Academy High School ",
            "Madison Academy of Health Careers", "Madison Academy of Health Careers", "Madison Academy of Letters ",
            "Madison Aerospace High School", "Madison Aerospace High School", "Madison Charter School for Excellence ",
            "Madison Coalition Community High School", "Madison Community High School ",
            "Madison Engineering and Technology Academy", "Madison Expeditionary Learning High School",
            "Madison Haven High School", "Madison Haven High School ",
            "Madison High School for Law and Community Service ", "Madison High School for Medical Science",
            "Madison High School for Writing and Communication Arts",
            "Madison High School for Writing and Communication Arts", "Madison High School for the Visual Arts ",
            "Madison High School of Business", "Madison High School of Science ", "Madison International High School",
            "Madison Lab School", "Madison Leadership Academy High School ",
            "Madison Leadership Academy II High School", "Madison Preparatory Charter School ",
            "Madison Regional High School ", "Madison School for Law, Government and Justice ",
            "Madison School of Law and Finance", "Madison Studio School for Writers and Artists ",
            "Madison Theatre High School", "Millennium Art Academy",
            "Pablo Neruda Academy for Architecture and World Studies", "Pelham Preparatory Academy",
            "Renaissance High School for Musical Theater & Technology", "School for Community Research and Learning",
            "The Celia Cruz Madison High School of Music ", "The Cinema School", "The Fieldston School",
            "The Madisonwood Preparatory Academy ", };

    public SchoolGenerator(StateAbbreviationType state) {
        schools = getNYSchools();
        schoolCount = schools.size();
        try {
            ag = new AddressGenerator(state);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static EducationalOrgReferenceType getEducationalOrgReferenceType(String schoolId) {
        EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();
        eoit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        EducationalOrgReferenceType eor = new EducationalOrgReferenceType();
        eor.setEducationalOrgIdentity(eoit);
        return eor;
    }

    private List<School> getNYSchools() {
        try {
            if (ag == null)
                ag = new AddressGenerator(StateAbbreviationType.NY);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        List<School> schools = new ArrayList<School>();
        for (String name : schoolNames) {

            School school = new School();
            school.setId(name.replaceAll(" ", "") + schoolPtr++);
            school.setStateOrganizationId("New York School Board");
            school.setNameOfInstitution(name);
            school.setShortNameOfInstitution(name.replaceAll("[a-z]", ""));
            school.setWebSite("http://www." + name.replaceAll("[ a-z:]", "") + "School.edu");
            school.getAddress().add(ag.getRandomAddress());

            EducationOrganizationCategoriesType category = new EducationOrganizationCategoriesType();
            category.getOrganizationCategory().add(EducationOrganizationCategoryType.SCHOOL);
            school.setOrganizationCategories(category);
            school.setOperationalStatus(OperationalStatusType.ACTIVE);

            GradeLevelsType grades = new GradeLevelsType();
            grades.getGradeLevel().add(GradeLevelType.ADULT_EDUCATION);
            grades.getGradeLevel().add(GradeLevelType.EARLY_EDUCATION);
            grades.getGradeLevel().add(GradeLevelType.EIGHTH_GRADE);
            grades.getGradeLevel().add(GradeLevelType.ELEVENTH_GRADE);
            grades.getGradeLevel().add(GradeLevelType.FIFTH_GRADE);
            grades.getGradeLevel().add(GradeLevelType.FIRST_GRADE);
            grades.getGradeLevel().add(GradeLevelType.FOURTH_GRADE);
            grades.getGradeLevel().add(GradeLevelType.GRADE_13);
            grades.getGradeLevel().add(GradeLevelType.INFANT_TODDLER);
            grades.getGradeLevel().add(GradeLevelType.KINDERGARTEN);
            grades.getGradeLevel().add(GradeLevelType.NINTH_GRADE);
            grades.getGradeLevel().add(GradeLevelType.OTHER);
            grades.getGradeLevel().add(GradeLevelType.POSTSECONDARY);
            grades.getGradeLevel().add(GradeLevelType.PRESCHOOL_PREKINDERGARTEN);
            grades.getGradeLevel().add(GradeLevelType.SECOND_GRADE);
            grades.getGradeLevel().add(GradeLevelType.SEVENTH_GRADE);
            grades.getGradeLevel().add(GradeLevelType.SIXTH_GRADE);
            grades.getGradeLevel().add(GradeLevelType.TENTH_GRADE);
            grades.getGradeLevel().add(GradeLevelType.THIRD_GRADE);
            grades.getGradeLevel().add(GradeLevelType.TRANSITIONAL_KINDERGARTEN);
            grades.getGradeLevel().add(GradeLevelType.TWELFTH_GRADE);
            grades.getGradeLevel().add(GradeLevelType.UNGRADED);
            school.setGradesOffered(grades);

            SchoolCategoriesType schoolCat = new SchoolCategoriesType();
            schoolCat.getSchoolCategory().add(SchoolCategoryItemType.ELEMENTARY_SECONDARY_SCHOOL);
            schoolCat.getSchoolCategory().add(SchoolCategoryItemType.ELEMENTARY_SCHOOL);
            schoolCat.getSchoolCategory().add(SchoolCategoryItemType.HIGH_SCHOOL);
            schoolCat.getSchoolCategory().add(SchoolCategoryItemType.MIDDLE_SCHOOL);
            schoolCat.getSchoolCategory().add(SchoolCategoryItemType.JUNIOR_HIGH_SCHOOL);
            schoolCat.getSchoolCategory().add(SchoolCategoryItemType.SECONDARY_SCHOOL);
            schoolCat.getSchoolCategory().add(SchoolCategoryItemType.UNGRADED);
            schoolCat.getSchoolCategory().add(SchoolCategoryItemType.ADULT_SCHOOL);
            schoolCat.getSchoolCategory().add(SchoolCategoryItemType.INFANT_TODDLER_SCHOOL);
            schoolCat.getSchoolCategory().add(SchoolCategoryItemType.PRESCHOOL_EARLY_CHILDHOOD);
            schoolCat.getSchoolCategory().add(SchoolCategoryItemType.PRIMARY_SCHOOL);
            schoolCat.getSchoolCategory().add(SchoolCategoryItemType.INTERMEDIATE_SCHOOL);
            school.setSchoolCategories(schoolCat);

            school.setSchoolType(SchoolType.REGULAR);
            school.setCharterStatus(CharterStatusType.OPEN_ENROLLMENT);
            school.setTitleIPartASchoolDesignation(TitleIPartASchoolDesignationType.NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL);
            school.setMagnetSpecialProgramEmphasisSchool(MagnetSpecialProgramEmphasisSchoolType.ALL_STUDENTS_PARTICIPATE);
            school.setAdministrativeFundingControl(AdministrativeFundingControlType.PUBLIC_SCHOOL);

            EducationalOrgIdentityType edOrgIdenType = new EducationalOrgIdentityType();
            // TODO remove hardcoded story data value used for testing
            EducationOrgIdentificationCode code = new EducationOrgIdentificationCode();
            code.setIdentificationSystem(EducationOrgIdentificationSystemType.FEDERAL);
            code.setID("SchoolId");
            edOrgIdenType.getStateOrganizationIdOrEducationOrgIdentificationCode().add(code);
            
            EducationalOrgReferenceType edOrgRef = new EducationalOrgReferenceType();
            edOrgRef.setEducationalOrgIdentity(edOrgIdenType);
            school.setLocalEducationAgencyReference(edOrgRef);
            schools.add(school);
        }
        return schools;
    }
    
    public static EducationalOrgReferenceType getEducationalOrgReferenceType(School school)
    {
        EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();
        eoit.getStateOrganizationIdOrEducationOrgIdentificationCode().addAll(school.getEducationOrgIdentificationCode());
        EducationalOrgReferenceType eor = new EducationalOrgReferenceType();
        eor.setEducationalOrgIdentity(eoit);
    	return eor;
    }

    public School getSchool(String schoolId) {
        School school = schools.get(schoolPtr++ % schoolCount);
        school.setId(schoolId);
        school.setStateOrganizationId(schoolId);
        return school;
    }

    public static School generateLowFi(String schoolId, String leaId) {
        School school = new School();
        school.setId(schoolId);

        school.setStateOrganizationId(schoolId);
        school.setNameOfInstitution(schoolId);
        school.setShortNameOfInstitution(schoolId.replaceAll("[a-z]", ""));
        school.setWebSite("http://www." + schoolId.replaceAll("[ a-z:]", "") + "School.edu");
        school.getAddress().add(AddressGenerator.generateLowFi());

        EducationOrganizationCategoriesType category = new EducationOrganizationCategoriesType();
        category.getOrganizationCategory().add(EducationOrganizationCategoryType.SCHOOL);
        school.setOrganizationCategories(category);
        school.setOperationalStatus(OperationalStatusType.ACTIVE);

        GradeLevelsType grades = new GradeLevelsType();
        grades.getGradeLevel().add(GradeLevelType.ADULT_EDUCATION);
        grades.getGradeLevel().add(GradeLevelType.EARLY_EDUCATION);
        grades.getGradeLevel().add(GradeLevelType.EIGHTH_GRADE);
        grades.getGradeLevel().add(GradeLevelType.ELEVENTH_GRADE);
        grades.getGradeLevel().add(GradeLevelType.FIFTH_GRADE);
        grades.getGradeLevel().add(GradeLevelType.FIRST_GRADE);
        grades.getGradeLevel().add(GradeLevelType.FOURTH_GRADE);
        grades.getGradeLevel().add(GradeLevelType.GRADE_13);
        grades.getGradeLevel().add(GradeLevelType.INFANT_TODDLER);
        grades.getGradeLevel().add(GradeLevelType.KINDERGARTEN);
        grades.getGradeLevel().add(GradeLevelType.NINTH_GRADE);
        grades.getGradeLevel().add(GradeLevelType.OTHER);
        grades.getGradeLevel().add(GradeLevelType.POSTSECONDARY);
        grades.getGradeLevel().add(GradeLevelType.PRESCHOOL_PREKINDERGARTEN);
        grades.getGradeLevel().add(GradeLevelType.SECOND_GRADE);
        grades.getGradeLevel().add(GradeLevelType.SEVENTH_GRADE);
        grades.getGradeLevel().add(GradeLevelType.SIXTH_GRADE);
        grades.getGradeLevel().add(GradeLevelType.TENTH_GRADE);
        grades.getGradeLevel().add(GradeLevelType.THIRD_GRADE);
        grades.getGradeLevel().add(GradeLevelType.TRANSITIONAL_KINDERGARTEN);
        grades.getGradeLevel().add(GradeLevelType.TWELFTH_GRADE);
        grades.getGradeLevel().add(GradeLevelType.UNGRADED);
        school.setGradesOffered(grades);

        SchoolCategoriesType schoolCat = new SchoolCategoriesType();
        schoolCat.getSchoolCategory().add(SchoolCategoryItemType.ELEMENTARY_SECONDARY_SCHOOL);
        schoolCat.getSchoolCategory().add(SchoolCategoryItemType.ELEMENTARY_SCHOOL);
        schoolCat.getSchoolCategory().add(SchoolCategoryItemType.HIGH_SCHOOL);
        schoolCat.getSchoolCategory().add(SchoolCategoryItemType.MIDDLE_SCHOOL);
        schoolCat.getSchoolCategory().add(SchoolCategoryItemType.JUNIOR_HIGH_SCHOOL);
        schoolCat.getSchoolCategory().add(SchoolCategoryItemType.SECONDARY_SCHOOL);
        schoolCat.getSchoolCategory().add(SchoolCategoryItemType.UNGRADED);
        schoolCat.getSchoolCategory().add(SchoolCategoryItemType.ADULT_SCHOOL);
        schoolCat.getSchoolCategory().add(SchoolCategoryItemType.INFANT_TODDLER_SCHOOL);
        schoolCat.getSchoolCategory().add(SchoolCategoryItemType.PRESCHOOL_EARLY_CHILDHOOD);
        schoolCat.getSchoolCategory().add(SchoolCategoryItemType.PRIMARY_SCHOOL);
        schoolCat.getSchoolCategory().add(SchoolCategoryItemType.INTERMEDIATE_SCHOOL);
        school.setSchoolCategories(schoolCat);

        school.setSchoolType(SchoolType.REGULAR);
        school.setCharterStatus(CharterStatusType.OPEN_ENROLLMENT);
        school.setTitleIPartASchoolDesignation(TitleIPartASchoolDesignationType.NOT_DESIGNATED_AS_A_TITLE_I_PART_A_SCHOOL);
        school.setMagnetSpecialProgramEmphasisSchool(MagnetSpecialProgramEmphasisSchoolType.ALL_STUDENTS_PARTICIPATE);
        school.setAdministrativeFundingControl(AdministrativeFundingControlType.PUBLIC_SCHOOL);

        // construct and add the SEA reference
        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
        edOrgIdentityType.getStateOrganizationIdOrEducationOrgIdentificationCode().add(leaId);

        EducationalOrgReferenceType leaRef = new EducationalOrgReferenceType();
        leaRef.setEducationalOrgIdentity(edOrgIdentityType);

        school.setLocalEducationAgencyReference(leaRef);

        return school;
    }

    public static void main(String[] args) {
        SchoolGenerator factory = new SchoolGenerator(StateAbbreviationType.NY);
        List<School> schools = factory.getNYSchools().subList(0, 5);
        for (School school : schools) {
            String schoolDesc = "\n\nId: " + school.getId() + ",\n" + "StateOrganizationId: "
                    + school.getStateOrganizationId() + ",\n" + "NameOfInstitution: " + school.getNameOfInstitution()
                    + ",\n" + "ShortNameOfInstitution: " + school.getShortNameOfInstitution() + ",\n" + "WebSite: "
                    + school.getWebSite() + ",\n" + "OrganizationCategories: " + school.getOrganizationCategories()
                    + ",\n" + "OperationalStatus: " + school.getOperationalStatus() + ",\n" + "GradesOffered: "
                    + school.getGradesOffered() + ",\n" + "SchoolCategories: " + school.getSchoolCategories() + ",\n"
                    + "SchoolType: " + school.getSchoolType() + ",\n" + "CharterStatus: " + school.getCharterStatus()
                    + ",\n" + "TitleIPartASchoolDesignation: " + school.getTitleIPartASchoolDesignation() + ",\n"
                    + "MagnetSpecialProgramEmphasisSchool: " + school.getMagnetSpecialProgramEmphasisSchool() + ",\n"
                    + "AdministrativeFundingControl: " + school.getAdministrativeFundingControl() + ",\n"
                    + "LocalEducationAgencyReference: " + school.getLocalEducationAgencyReference();
            log.info(schoolDesc);
            System.out.println(schoolDesc);
        }

        School school = factory.getSchool("schoolId-1");
        school = factory.getSchool("schoolId-2");
        String schoolDesc = "\n\nId: " + school.getId() + ",\n" + "StateOrganizationId: "
                + school.getStateOrganizationId() + ",\n" + "NameOfInstitution: " + school.getNameOfInstitution()
                + ",\n" + "ShortNameOfInstitution: " + school.getShortNameOfInstitution() + ",\n" + "WebSite: "
                + school.getWebSite() + ",\n" + "OrganizationCategories: " + school.getOrganizationCategories() + ",\n"
                + "OperationalStatus: " + school.getOperationalStatus() + ",\n" + "GradesOffered: "
                + school.getGradesOffered() + ",\n" + "SchoolCategories: " + school.getSchoolCategories() + ",\n"
                + "SchoolType: " + school.getSchoolType() + ",\n" + "CharterStatus: " + school.getCharterStatus()
                + ",\n" + "TitleIPartASchoolDesignation: " + school.getTitleIPartASchoolDesignation() + ",\n"
                + "MagnetSpecialProgramEmphasisSchool: " + school.getMagnetSpecialProgramEmphasisSchool() + ",\n"
                + "AdministrativeFundingControl: " + school.getAdministrativeFundingControl() + ",\n"
                + "LocalEducationAgencyReference: " + school.getLocalEducationAgencyReference();
        log.info(schoolDesc);
        System.out.println(schoolDesc);
    }
}
