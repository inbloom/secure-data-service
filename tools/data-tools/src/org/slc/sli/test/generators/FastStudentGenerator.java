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

import java.util.Calendar;
import java.util.Random;

import org.slc.sli.test.edfi.entities.Address;
import org.slc.sli.test.edfi.entities.CohortYear;
import org.slc.sli.test.edfi.entities.CohortYearType;
import org.slc.sli.test.edfi.entities.Disability;
import org.slc.sli.test.edfi.entities.DisabilityType;
import org.slc.sli.test.edfi.entities.ElectronicMail;
import org.slc.sli.test.edfi.entities.ElectronicMailAddressType;
import org.slc.sli.test.edfi.entities.LanguageItemType;
import org.slc.sli.test.edfi.entities.LanguagesType;
import org.slc.sli.test.edfi.entities.LearningStyles;
import org.slc.sli.test.edfi.entities.LimitedEnglishProficiencyType;
import org.slc.sli.test.edfi.entities.OldEthnicityType;
import org.slc.sli.test.edfi.entities.OtherName;
import org.slc.sli.test.edfi.entities.ProgramParticipation;
import org.slc.sli.test.edfi.entities.ProgramType;
import org.slc.sli.test.edfi.entities.RaceItemType;
import org.slc.sli.test.edfi.entities.RaceType;
import org.slc.sli.test.edfi.entities.SchoolFoodServicesEligibilityType;
import org.slc.sli.test.edfi.entities.Section504DisabilitiesType;
import org.slc.sli.test.edfi.entities.Section504DisabilityItemType;
import org.slc.sli.test.edfi.entities.SexType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;
import org.slc.sli.test.edfi.entities.Student;
import org.slc.sli.test.edfi.entities.StudentCharacteristic;
import org.slc.sli.test.edfi.entities.StudentCharacteristicType;
import org.slc.sli.test.edfi.entities.StudentIndicator;
import org.slc.sli.test.edfi.entities.Telephone;
import org.slc.sli.test.generators.NameGenerator;


/**
 * Dumb, fast student generator for high volume data.
 *
 * @author dduran
 *
 */
public class FastStudentGenerator {

    public static final Calendar RIGHT_NOW = Calendar.getInstance();
    
    FastStudentGenerator () throws Exception {    
    }
    
    public static AddressGenerator ag;
    
   // ag = new AddressGenerator(StateAbbreviationType.NJ);

    public static Student generateMediumFi(String studentId) throws Exception {
        NameGenerator nameGenerator = new NameGenerator(); //to be research?
        Random random = new Random(31);

        Student student = new Student();

        student.setStudentUniqueStateId(studentId);

        student.setSex(random.nextBoolean() ? SexType.MALE : SexType.FEMALE);

        // Name
        if (student.getSex().equals(SexType.MALE)) {
            student.setName(nameGenerator.getMaleName());
            student.getOtherName().add(nameGenerator.getMaleOtherName());
            student.getOtherName().add(nameGenerator.getMaleOtherName());

        } else {
            student.setName(nameGenerator.getFemaleName());
            student.getOtherName().add(nameGenerator.getFemaleOtherName());
            student.getOtherName().add(nameGenerator.getFemaleOtherName());

        }

//        Address address = AddressGenerator.generateLowFi();
//
//        student.getAddress().add(address);
//        student.getAddress().add(address);
      
          student.getAddress().add(ag.getRandomAddress());
//        if (random.nextBoolean())
//            student.getAddress().add(ag.getRandomAddress());
       
        //setNameAndOtherNames(student);

        student.setHispanicLatinoEthnicity(random.nextBoolean());

        setRace(student);

        student.setBirthData(BirthDataGenerator.generateFastBirthData());

        //setAddresses(student);

        setTelephones(student);

        setEmailAdresses(student);

        student.setProfileThumbnail("StudentPicture.jpg");

        student.setOldEthnicity(OldEthnicityType.WHITE_NOT_OF_HISPANIC_ORIGIN);

        student.setEconomicDisadvantaged(random.nextBoolean());

        student.setSchoolFoodServicesEligibility(SchoolFoodServicesEligibilityType.FULL_PRICE);

        setStudentCharacteristics(student);

        student.setLimitedEnglishProficiency(LimitedEnglishProficiencyType.NOT_LIMITED);

        setLanguagesAndHomeLanguage(student);

        setDisabilities(student);

        setSection504(student);

        student.setDisplacementStatus("Military Deployment");

        setProgramParticipation(student);

        setLearningStyles(student);

        setCohortYears(student);

        setStudentIndicators(student);

        student.setLoginId("StudentLoginID");

        return student;
    }
    
    public static Student generateLowFi(String studentId) {

        Random random = new Random(31);

        Student student = new Student();

        student.setStudentUniqueStateId(studentId);

        student.setSex(random.nextBoolean() ? SexType.MALE : SexType.FEMALE);

        setNameAndOtherNames(student);

        student.setHispanicLatinoEthnicity(random.nextBoolean());

        setRace(student);

        student.setBirthData(BirthDataGenerator.generateFastBirthData());

        setAddresses(student);

        setTelephones(student);

        setEmailAdresses(student);

        student.setProfileThumbnail("StudentPicture.jpg");

        student.setOldEthnicity(OldEthnicityType.WHITE_NOT_OF_HISPANIC_ORIGIN);

        student.setEconomicDisadvantaged(random.nextBoolean());

        student.setSchoolFoodServicesEligibility(SchoolFoodServicesEligibilityType.FULL_PRICE);

        setStudentCharacteristics(student);

        student.setLimitedEnglishProficiency(LimitedEnglishProficiencyType.NOT_LIMITED);

        setLanguagesAndHomeLanguage(student);

        setDisabilities(student);

        setSection504(student);

        student.setDisplacementStatus("Military Deployment");

        setProgramParticipation(student);

        setLearningStyles(student);

        setCohortYears(student);

        setStudentIndicators(student);

        student.setLoginId("StudentLoginID");

        return student;
    }
    
    



    private static void setAddresses(Student student) {
        Address address = AddressGenerator.generateLowFi();

        student.getAddress().add(address);
        student.getAddress().add(address);
    }

    private static void setTelephones(Student student) {
        Telephone telephone = TelephoneGenerator.getFastTelephone();

        student.getTelephone().add(telephone);
        student.getTelephone().add(telephone);
    }

    private static void setStudentIndicators(Student student) {
        StudentIndicator si = new StudentIndicator();
        si.setBeginDate("2011-03-04");
        si.setEndDate("2012-03-04");
        si.setIndicator("This is a student indicator");
        si.setIndicatorName("IndicatorName");

        student.getStudentIndicators().add(si);
        student.getStudentIndicators().add(si);
    }

    private static void setCohortYears(Student student) {
        CohortYear ch = new CohortYear();
        ch.setSchoolYear("2011-2012");
        ch.setCohortYearType(CohortYearType.ELEVENTH_GRADE);

        student.getCohortYears().add(ch);
        student.getCohortYears().add(ch);
    }

    private static void setLearningStyles(Student student) {
        LearningStyles ls = new LearningStyles();
        ls.setVisualLearning(50);
        ls.setTactileLearning(25);
        ls.setAuditoryLearning(25);

        student.setLearningStyles(ls);
    }

    private static void setProgramParticipation(Student student) {
        ProgramParticipation pp = new ProgramParticipation();
        pp.setBeginDate("2011-03-04");
        pp.setEndDate("2012-03-04");
        pp.setProgram(ProgramType.ATHLETICS);

        student.getProgramParticipations().add(pp);
        student.getProgramParticipations().add(pp);
    }

    private static void setSection504(Student student) {
        Section504DisabilitiesType sec504 = new Section504DisabilitiesType();
        sec504.getSection504Disability().add(Section504DisabilityItemType.MEDICAL_CONDITION);

        student.setSection504Disabilities(sec504);
    }

    private static void setDisabilities(Student student) {
        Disability disability = new Disability();
        disability.setDisability(DisabilityType.DEVELOPMENTAL_DELAY);

        student.getDisabilities().add(disability);
        student.getDisabilities().add(disability);
    }

    private static void setLanguagesAndHomeLanguage(Student student) {
        LanguagesType lt = new LanguagesType();
        student.setLanguages(lt);

        student.getLanguages().getLanguage().add(LanguageItemType.ENGLISH);
        student.getLanguages().getLanguage().add(LanguageItemType.ARABIC);

        student.setHomeLanguages(lt);
    }

    private static void setStudentCharacteristics(Student student) {
        StudentCharacteristic sc = new StudentCharacteristic();
        sc.setBeginDate("2011-03-04");
        sc.setEndDate("2011-03-04");
        sc.setCharacteristic(StudentCharacteristicType.FOSTER_CARE);

        student.getStudentCharacteristics().add(sc);
        student.getStudentCharacteristics().add(sc);
    }

    private static void setEmailAdresses(Student student) {
        ElectronicMail em = new ElectronicMail();
        em.setEmailAddress("test@gmail.com");
        em.setEmailAddressType(ElectronicMailAddressType.HOME_PERSONAL);

        student.getElectronicMail().add(em);
        student.getElectronicMail().add(em);
    }

    private static void setRace(Student student) {
        RaceType rt = new RaceType();
        rt.getRacialCategory().add(RaceItemType.WHITE);
        rt.getRacialCategory().add(RaceItemType.BLACK_AFRICAN_AMERICAN);

        student.setRace(rt);
    }

    private static void setNameAndOtherNames(Student student) {

        student.setName(NameGenerator.getFastName());

        OtherName otherName = NameGenerator.getFastOtherName();
        student.getOtherName().add(otherName);
        student.getOtherName().add(otherName);
    }
}
