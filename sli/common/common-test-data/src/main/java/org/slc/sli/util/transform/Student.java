package org.slc.sli.util.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @author dwilliams
 *
 */
public class Student implements MongoDataEmitter {
    private StudentName name = null;
    private BirthData birthData = null;
    private String stateStudentId = null;
    private String sex = "Male";	// default
    private String econDis = "false";	// default
    private School school = null;
    private String generatedUuid = null;
    private String generatedStudentSchoolAssociationUuid = null;
    private String gradeLevel = null;
    private HashMap<String, Section> sections = new HashMap<String, Section>();
    private String schoolFoodServicesEligibility = null;
    private String studentCharacteristics = null;
    private String limitedEnglishProficiency = null;
    private String section504Disabilities = null;
    private ArrayList<String> programParticipations = new ArrayList<String>();
    private boolean hispanicLatinoEthnicity = false;
    private String racialCategory = null;
    private String displacementStatus = null;
    private String disability = null;
    
    public static final String typeCode = "aaaf";
    public static final String schoolAssocTypeCode = "aaba";
    public static final String sectionAssocTypeCode = "aaae";
    
    public Student(StudentName name, BirthData birth, String stateId, String sex, String econDis, School school,
            String grade, String sfse, String stuChar, String lep, String sect504Dis, String progPartic,
            String displacement, String disability) {
        this.name = name;
        this.birthData = birth;
        this.stateStudentId = stateId;
        this.sex = sex;
        this.econDis = econDis;
        this.school = school;
        
        generatedUuid = Base64.nextUuid(typeCode);
        generatedStudentSchoolAssociationUuid = Base64.nextUuid(schoolAssocTypeCode);
        
        if (grade.equalsIgnoreCase("K")) {
            gradeLevel = "Kindergarten";
        } else if (grade.equalsIgnoreCase("1")) {
            gradeLevel = "First grade";
        } else if (grade.equalsIgnoreCase("2")) {
            gradeLevel = "Second grade";
        } else if (grade.equalsIgnoreCase("3")) {
            gradeLevel = "Third grade";
        } else if (grade.equalsIgnoreCase("4")) {
            gradeLevel = "Fourth grade";
        } else if (grade.equalsIgnoreCase("5")) {
            gradeLevel = "Fifth grade";
        } else if (grade.equalsIgnoreCase("6")) {
            gradeLevel = "Sixth grade";
        } else if (grade.equalsIgnoreCase("7")) {
            gradeLevel = "Seventh grade";
        } else if (grade.equalsIgnoreCase("8")) {
            gradeLevel = "Eighth grade";
        } else if (grade.equalsIgnoreCase("9")) {
            gradeLevel = "Ninth grade";
        } else if (grade.equalsIgnoreCase("10")) {
            gradeLevel = "Tenth grade";
        } else if (grade.equalsIgnoreCase("11")) {
            gradeLevel = "Eleventh grade";
        } else if (grade.equalsIgnoreCase("12")) {
            gradeLevel = "Twelfth grade";
        } else {
            gradeLevel = grade;
        }
        
        this.schoolFoodServicesEligibility = sfse;
        this.studentCharacteristics = stuChar;
        this.limitedEnglishProficiency = lep;
        this.section504Disabilities = sect504Dis;
        this.programParticipations.add(progPartic);
        this.displacementStatus = displacement;
        this.disability = disability;
    }
    
    public String getUuid() {
        return generatedUuid;
    }
    
    public void setSchool(School school) {
        this.school = school;
    }
    
    public String emitXml() {
        StringBuffer answer = new StringBuffer();
        answer.append("<Student>\n");
        answer.append("   <StudentUniqueStateId>").append(stateStudentId).append("</StudentUniqueStateId>\n");
        answer.append("   <Name>\n");
        if (name != null) {
            answer.append("      <FirstName>").append(name.getFirstName()).append("</FirstName>\n");
            if (name.getMiddleName() != null) {
                answer.append("      <MiddleName>").append(name.getMiddleName()).append("</MiddleName>\n");
            }
            answer.append("      <LastSurname>").append(name.getLastSurname()).append("</LastSurname>\n");
            if (name.getSuffix() != null) {
                answer.append("      <GenerationCodeSuffix>").append(name.getSuffix())
                        .append("</GenerationCodeSuffix>\n");
            }
        }
        answer.append("   </Name>\n");
        answer.append("   <Sex>").append(sex).append("</Sex>\n");
        answer.append("   <BirthData>\n");
        if (birthData != null) {
            answer.append("      <BirthDate>").append(birthData.getBirthDate()).append("</BirthDate>\n");
        }
        answer.append("   </BirthData>\n");
        answer.append("   <Telephone TelephoneNumberType=\"Mobile\" PrimaryTelephoneNumberIndicator=\"true\">\n");
        answer.append("      <TelephoneNumber>410-555-1212</TelephoneNumber>\n");
        answer.append("   </Telephone>\n");
        answer.append("   <ElectronicMail EmailAddressType=\"Home/Personal\">\n");
        answer.append("      <TelephoneNumber>").append(name.getFirstName()).append(".").append(name.getLastSurname())
                .append("@example.com").append("</TelephoneNumber>\n");
        answer.append("   </ElectronicMail>\n");
        answer.append("   <HispanicLatinoEthnicity>").append(hispanicLatinoEthnicity)
                .append("</HispanicLatinoEthnicity>\n");
        answer.append("   <Race>\n");
        if (racialCategory != null) {
            answer.append("      <RacialCategory>").append(racialCategory).append("</RacialCategory>\n");
        }
        answer.append("   </Race>\n");
        answer.append("   <EconomicDisadvantaged>").append(econDis).append("</EconomicDisadvantaged>\n");
        answer.append("   <SchoolFoodServicesEligibility>").append(schoolFoodServicesEligibility)
                .append("</SchoolFoodServicesEligibility>\n");
        answer.append("   <StudentCharacteristics>\n");
        if (studentCharacteristics != null && studentCharacteristics.length() > 0) {
            answer.append("      <Characteristic>").append(studentCharacteristics).append("</Characteristic>\n");
            answer.append("      <BeginDate>2005-01-11</BeginDate>\n");
            answer.append("      <EndDate>2005-01-11</EndDate>\n");
            answer.append("      <DesignatedBy>School</DesignatedBy>\n");
        }
        answer.append("   </StudentCharacteristics>\n");
        answer.append("   <Disabilities>\n");
        if (disability != null && disability.length() > 0) {
            answer.append("      <Disability>").append(disability).append("</Disability>\n");
            answer.append("      <DisabilityDiagnosis>").append(disability).append("</DisabilityDiagnosis>\n");
            answer.append("      <OrderOfDisability>3</OrderOfDisability>\n");
        }
        answer.append("   </Disabilities>\n");
        answer.append("   <Section504Disabilities>\n");
        if (section504Disabilities != null && section504Disabilities.length() > 0) {
            answer.append("      <Section504Disability>").append(section504Disabilities)
                    .append("</Section504Disability>\n");
        }
        answer.append("   </Section504Disabilities>\n");
        answer.append("   <DisplacementStatus>").append(displacementStatus).append("</DisplacementStatus>\n");
        answer.append("   <ProgramParticipations>\n");
        for (String partic : programParticipations) {
            if (partic.length() == 0) {
                continue;
            }
            answer.append("      <Program>").append(partic).append("</Program>\n");
            answer.append("      <BeginDate>2011-01-11</BeginDate>\n");
            answer.append("      <EndDate>2011-01-11</EndDate>\n");
            answer.append("      <DesignatedBy>School</DesignatedBy>\n");
        }
        answer.append("   </ProgramParticipations>\n");
        answer.append("   <LearningStyles>\n");
        answer.append("      <VisualLearning>1</VisualLearning>\n");
        answer.append("      <AuditoryLearning>1</AuditoryLearning>\n");
        answer.append("      <TactileLearning>1</TactileLearning>\n");
        answer.append("   </LearningStyles>\n");
        answer.append("   <StudentIndicators>\n");
        answer.append("      <IndicatorName>student</IndicatorName>\n");
        answer.append("      <Indicator>student</Indicator>\n");
        answer.append("      <BeginDate>2011-01-11</BeginDate>\n");
        answer.append("      <EndDate>2011-01-12</EndDate>\n");
        answer.append("      <DesignatedBy>School</DesignatedBy>\n");
        answer.append("   </StudentIndicators>\n");
        answer.append("</Student>\n");
        /*
         * <Student>
         * <StudentUniqueStateId>231101422</StudentUniqueStateId>
         * <Name>
         * <PersonalTitlePrefix>Mr</PersonalTitlePrefix>
         * <FirstName>Alfonso</FirstName>
         * <MiddleName>Ora</MiddleName>
         * <LastSurname>Steele</LastSurname>
         * <GenerationCodeSuffix>III</GenerationCodeSuffix>
         * <MaidenName>Jimenez</MaidenName>
         * </Name>
         * <Sex>Male</Sex>
         * <BirthData>
         * <BirthDate>1999-07-12</BirthDate>
         * <CityOfBirth>Baltimore</CityOfBirth>
         * <StateOfBirthAbbreviation>MD</StateOfBirthAbbreviation>
         * <CountryOfBirthCode>US</CountryOfBirthCode>
         * <MultipleBirthStatus>false</MultipleBirthStatus>
         * </BirthData>
         * <Address AddressType="Home">
         * <StreetNumberName>555 Main Street</StreetNumberName>
         * <City>Baltimore</City>
         * <StateAbbreviation>MD</StateAbbreviation>
         * <PostalCode>21218</PostalCode>
         * </Address>
         * <Telephone TelephoneNumberType="Mobile" PrimaryTelephoneNumberIndicator="true">
         * <TelephoneNumber>410-555-0248</TelephoneNumber>
         * </Telephone>
         * <ElectronicMail EmailAddressType="Home/Personal">
         * <EmailAddress>asteele@email.com</EmailAddress>
         * </ElectronicMail>
         * <HispanicLatinoEthnicity>true</HispanicLatinoEthnicity>
         * <OldEthnicity>Hispanic</OldEthnicity>
         * <Race>
         * <RacialCategory>White</RacialCategory>
         * </Race>
         * <EconomicDisadvantaged>true</EconomicDisadvantaged>
         * <SchoolFoodServicesEligibility>Free</SchoolFoodServicesEligibility>
         * <StudentCharacteristics>
         * <Characteristic>Immigrant</Characteristic>
         * <BeginDate>2005-01-11</BeginDate>
         * <EndDate>2005-01-30</EndDate>
         * <DesignatedBy>School</DesignatedBy>
         * </StudentCharacteristics>
         * <Disabilities>
         * <Disability>Deafness</Disability>
         * <DisabilityDiagnosis>Deaf</DisabilityDiagnosis>
         * <OrderOfDisability>3</OrderOfDisability>
         * </Disabilities>
         * <Section504Disabilities>
         * <Section504Disability>Other</Section504Disability>
         * </Section504Disabilities>
         * <DisplacementStatus>Other</DisplacementStatus>
         * <ProgramParticipations>
         * <Program>Bilingual</Program>
         * <BeginDate>2011-01-11</BeginDate>
         * <EndDate>2011-01-11</EndDate>
         * <DesignatedBy>School</DesignatedBy>
         * </ProgramParticipations>
         * <LearningStyles>
         * <VisualLearning>1</VisualLearning>
         * <AuditoryLearning>1</AuditoryLearning>
         * <TactileLearning>1</TactileLearning>
         * </LearningStyles>
         * <StudentIndicators>
         * <IndicatorName>student</IndicatorName>
         * <Indicator>student</Indicator>
         * <BeginDate>2011-01-11</BeginDate>
         * <EndDate>2011-01-12</EndDate>
         * <DesignatedBy>School</DesignatedBy>
         * </StudentIndicators>
         * </Student>
         */
        return answer.toString();
    }
    
    @Override
    public String emit() {
        if (Configuration.getOutputType().equals(Configuration.OutputType.EdFiXml)) {
            return emitXml();
        }
        // {"_id":{"$binary":"gUWAH6emhnpQtrkok0o3sA==","$type":"03"},"type":"student",
        // "body":{"studentUniqueStateId":775598719,"name":{"firstName":"Gil","middleName":"","lastSurname":"Prince"},"sex":"Male",
        // "economicDisadvantaged":false,"birthData":{"birthDate":"2000-01-01"}}}
        
        StringBuffer answer = new StringBuffer();
        answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(generatedUuid))
                .append("\",\"$type\":\"03\"},\"type\":\"student\",\"body\":{\"studentUniqueStateId\":\"")
                .append(stateStudentId).append("\",").append(name.emit()).append(",\"sex\":\"").append(sex)
                .append("\",\"economicDisadvantaged\":").append(econDis)
                .append(",\"schoolFoodServicesEligibility\":\"").append(schoolFoodServicesEligibility)
                .append("\",\"hispanicLatinoEthnicity\":").append(hispanicLatinoEthnicity).append(",\"race\":\"")
                .append(racialCategory).append("\",");
        if (studentCharacteristics != null && studentCharacteristics.length() > 0) {
            answer.append("\"studentCharacteristics\":[{\"characteristic\":\"").append(studentCharacteristics)
                    .append("\"}],");
        }
        if (disability != null && disability.length() > 0) {
            answer.append("\"disabilities\":[{\"disability\":\"").append(disability).append("\"}],");
        }
        answer.append("\"limitedEnglishProficiency\":\"").append(limitedEnglishProficiency)
                .append("\",\"section504Disabilities\":\"").append(section504Disabilities)
                .append("\",\"displacementStatus\":\"").append(displacementStatus)
                .append("\",\"programParticipations\":[");
        
        boolean gotRealParticipation = false;
        for (int i = 0; programParticipations != null && i < programParticipations.size(); i++) {
            String pp = programParticipations.get(i);
            if (pp.length() == 0) {
                continue;
            }
            if (gotRealParticipation) {
                answer.append(",");
            }
            answer.append("\"").append(pp).append("\"");
            gotRealParticipation = true;
        }
        answer.append("],").append(birthData.emit()).append("}}\n");
        return answer.toString();
    }
    
    public String emitSchoolAssociation() {
        // {"_id":{"$binary":"Zkc34g40KhJyZXhnLU3jmA==","$type":"03"},"type":"studentSchoolAssociation",
        // "body":{"entryGradeLevel":"First_grade","studentId":"714c1304-8a04-4e23-b043-4ad80eb60992","schoolId":"eb3b8c35-f582-df23-e406-6947249a19f2"}}
        
        StringBuffer answer = new StringBuffer();
        
        if (Configuration.getOutputType().equals(Configuration.OutputType.Fixture)) {
            answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(generatedStudentSchoolAssociationUuid))
                    .append("\",\"$type\":\"03\"},\"type\":\"studentSchoolAssociation\",\"")
                    .append("body\":{\"entryGradeLevel\":\"").append(gradeLevel).append("\",\"studentId\":\"")
                    .append(generatedUuid).append("\",\"schoolId\":\"").append(school.getUuid()).append("\"}}\n");
        } else {
            /*
             * <StudentSchoolAssociation>
             * <StudentReference>
             * <StudentIdentity>
             * <StudentUniqueStateId>900000001</StudentUniqueStateId>
             * <Name>
             * <FirstName>MARY</FirstName>
             * <LastSurname>SMITH</LastSurname>
             * </Name>
             * </StudentIdentity>
             * </StudentReference>
             * <SchoolReference>
             * <EducationalOrgIdentity>
             * <StateOrganizationId>990000001</StateOrganizationId>
             * </EducationalOrgIdentity>
             * </SchoolReference>
             * <EntryDate>2012-01-17</EntryDate>
             * <EntryGradeLevel>Eighth grade</EntryGradeLevel>
             * <GraduationPlan>Distinguished</GraduationPlan>
             * <EducationalPlans>
             * <EducationalPlan>Full Time Employment</EducationalPlan>
             * </EducationalPlans>
             * </StudentSchoolAssociation>
             */
            answer.append("<StudentSchoolAssociation>\n");
            answer.append("   <StudentReference>\n");
            answer.append("      <StudentIdentity>\n");
            answer.append("      <StudentUniqueStateId>").append(stateStudentId).append("</StudentUniqueStateId>\n");
            answer.append("      <Name>\n");
            if (name != null) {
                answer.append("         <FirstName>").append(name.getFirstName()).append("</FirstName>\n");
                if (name.getMiddleName() != null) {
                    answer.append("         <MiddleName>").append(name.getMiddleName()).append("</MiddleName>\n");
                }
                answer.append("         <LastSurname>").append(name.getLastSurname()).append("</LastSurname>\n");
                if (name.getSuffix() != null) {
                    answer.append("         <GenerationCodeSuffix>").append(name.getSuffix())
                            .append("</GenerationCodeSuffix>\n");
                }
            }
            answer.append("      </Name>\n");
            answer.append("      </StudentIdentity>\n");
            answer.append("   </StudentReference>\n");
            answer.append("   <SchoolReference>\n");
            answer.append("      <EducationalOrgIdentity>\n");
            answer.append("         <StateOrganizationId>").append(school.getStateUniqueId())
                    .append("</StateOrganizationId>\n");
            answer.append("      </EducationalOrgIdentity>\n");
            answer.append("   </SchoolReference>\n");
            answer.append("   <EntryDate>2012-01-17</EntryDate>\n");
            answer.append("   <GraduationPlan>Distinguished</GraduationPlan>\n");
            answer.append("   <EducationalPlans>\n");
            answer.append("      <EducationalPlan>Full Time Employment</EducationalPlan>\n");
            answer.append("   </EducationalPlans>\n");
            answer.append("</StudentSchoolAssociation>\n");
        }
        return answer.toString();
    }
    
    public void addSection(Section s) {
        sections.put(s.getUuid(), s);
    }
    
    public String emitSectionAssociations() {
        // {"_id":{"$binary":"ContextStud+/Class303Q==","$type":"03"},"type":"studentSectionAssociation",
        // "body":{"repeatIdentifier":"Not_repeated","studentId":"eb4d7e1b-7bed-890a-e974-5d8aa9fbfc2d","sectionId":"eb4d7e1b-7bed-890a-dd74-cdb25a29fc2d"},"tenantId":"Zork"}
        
        StringBuffer answer = new StringBuffer();
        Iterator<String> iter = sections.keySet().iterator();
        while (iter.hasNext()) {
            String sectionId = iter.next();
            // Section section = sections.get(sectionId);
            answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(Base64.nextUuid(sectionAssocTypeCode)))
                    .append("\",\"$type\":\"03\"},\"type\":\"studentSectionAssociation\",\"")
                    .append("body\":{\"repeatIdentifier\":\"Not repeated\",\"studentId\":\"").append(generatedUuid)
                    .append("\",\"sectionId\":\"").append(sectionId).append("\"},\"tenantId\":\"Zork\"}\n");
        }
        
        return answer.toString();
    }
    
    public School getSchool() {
        return school;
    }
    
    public void setUuid(String id) {
        generatedUuid = id;	// okay, so it isn't really generated at this point. This helps to keep
                            // dashboard IDs predictable.
    }
    
    public void setHispanicLatinoEthnicity(boolean hispanicLatinoEthnicity) {
        this.hispanicLatinoEthnicity = hispanicLatinoEthnicity;
    }
    
    public void setRacialCategory(String racialCategory) {
        this.racialCategory = racialCategory;
    }
    
    public void addProgram(String prog) {
        if (programParticipations.contains(prog)) {
            return;
        }
        programParticipations.add(prog);
    }
}
