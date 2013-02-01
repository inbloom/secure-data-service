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

import java.util.Random;
import java.lang.Math;

import org.slc.sli.test.edfi.entities.*;

public class StudentGenerator {
    AddressGenerator ag;
    NameGenerator nameGenerator;
    BirthDataGenerator bdg;
    TelephoneGenerator telephonegen;

    private boolean includeOptionalData = true;

    // includeAllData is only and option if includeOptionalData is true,
    // otherwise this will be ignored...
    private boolean includeAllData = true;
    private boolean randomizeNumbers = true;

    private int numStudentIdentificationCodes = 1;
    private int maxStudentIdentificationCodes = 4;

    private int numOtherNames = 0;
    private int maxOtherNames = 3;

    private int numAddresses = 1;
    private int maxAddresses = 3;

    private int numTelephones = 1;
    private int maxTelephones = 4;

    private int numElectronicMailAddresses = 0;
    private int maxElectronicMailAddresses = 2;

    private int numStudentCharacteristics = 0;
    private int maxStudentCharacteristics = 3;

    private int numLanguages = 1;
    private int maxLanguages = 3;

    private int numHomeLanguages = 1;
    private int maxHomeLanguages = 4;

    private int numDisabilities = 0;
    private int maxDisabilities = 4;

    private int numSection504 = 0;
    private int maxSection504 = 2;

    private int percentDisplaced = 10;

    private int numProgramParticipation = 0;
    private int maxProgramParticipation = 3;

    private int percentVisualLearning = 50;
    private int minVisualLearning = 0;
    private int maxVisualLearning = 100;

    private int percentAuditoryLearning = 25;
    private int minAuditoryLearning = 0;
    private int maxAuditoryLearning = 100;

    private int percentTactileLearning = 25;

    private int numCohortYears = 0;
    private int maxCohortYears = 4;

    private int numStudentIndicators = 0;
    private int maxStudentIndicators = 2;

    public StudentGenerator(StateAbbreviationType state, boolean includeOptional, boolean randomize) {
        this.setState(state);
        this.includeOptionalData = includeOptional;
        this.randomizeNumbers = randomize;
    }

    public void setState(StateAbbreviationType state) {
        try {
            ag = new AddressGenerator(state);
            nameGenerator = new NameGenerator();
            bdg = new BirthDataGenerator();
            telephonegen = new TelephoneGenerator();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static SLCStudentReferenceType getStudentReferenceType(String studentId)
    {
        SLCStudentReferenceType srt = new SLCStudentReferenceType();
        SLCStudentIdentityType sit = new SLCStudentIdentityType();
        srt.setStudentIdentity(sit);
        sit.setStudentUniqueStateId(studentId);
        return srt;
    }

    public static StudentReferenceType getEdFiStudentReferenceType(String studentId)
    {
        StudentReferenceType srt = new StudentReferenceType();
        StudentIdentityType sit = new StudentIdentityType();
        srt.setStudentIdentity(sit);
        sit.setStudentUniqueStateId(studentId);
        return srt;
    }

    public Student generate(String studentId) {
        Student s = new Student();
        Random random = new Random(31);

        // Set required student fields first

        // Student ID
        s.setStudentUniqueStateId(studentId);

        // Sex
        s.setSex(random.nextBoolean() ? SexType.MALE : SexType.FEMALE);

        // Name
        if (s.getSex().equals(SexType.MALE)) {
            s.setName(nameGenerator.getMaleName());
        } else {
            s.setName(nameGenerator.getFemaleName());
        }

        // Birth Data
        s.setBirthData(bdg.generate(6+random.nextInt(11)));

        // Hispanic/Latino Ethnicity
        s.setHispanicLatinoEthnicity(random.nextBoolean());

        // Race
        RaceType rt = new RaceType();
        s.setRace(rt);
        int randomInt5 = random.nextInt(5);
        RaceItemType riType = null;
             if ( randomInt5 == 0 ) riType = RaceItemType.AMERICAN_INDIAN_ALASKAN_NATIVE;
        else if ( randomInt5 == 1 ) riType = RaceItemType.ASIAN;
        else if ( randomInt5 == 2 ) riType = RaceItemType.BLACK_AFRICAN_AMERICAN;
        else if ( randomInt5 == 3 ) riType = RaceItemType.NATIVE_HAWAIIAN_PACIFIC_ISLANDER;
        else if ( randomInt5 == 4 ) riType = RaceItemType.WHITE;
        s.getRace().getRacialCategory().add(riType);

        if (includeOptionalData) {

            // Student Identification Codes
            if (randomizeNumbers) {
                numStudentIdentificationCodes = random.nextInt(maxStudentIdentificationCodes+1);
            }
            if (includeAllData) {
                numStudentIdentificationCodes = Math.max(numStudentIdentificationCodes, 1);
            }
            for (int iSic = 0; iSic < numStudentIdentificationCodes; iSic++) {
                StudentIdentificationCode sic = new StudentIdentificationCode();
                sic.setIdentificationCode("StudentIDCode"+iSic);
                int randomInt11 = random.nextInt(11);
                StudentIdentificationSystemType sisType = null;
                     if ( randomInt11 == 0 ) sisType = StudentIdentificationSystemType.CANADIAN_SIN;
                else if ( randomInt11 == 1 ) sisType = StudentIdentificationSystemType.DISTRICT;
                else if ( randomInt11 == 2 ) sisType = StudentIdentificationSystemType.FAMILY;
                else if ( randomInt11 == 3 ) sisType = StudentIdentificationSystemType.FEDERAL;
                else if ( randomInt11 == 4 ) sisType = StudentIdentificationSystemType.LOCAL;
                else if ( randomInt11 == 5 ) sisType = StudentIdentificationSystemType.NATIONAL_MIGRANT;
                else if ( randomInt11 == 6 ) sisType = StudentIdentificationSystemType.OTHER;
                else if ( randomInt11 == 7 ) sisType = StudentIdentificationSystemType.SCHOOL;
                else if ( randomInt11 == 8 ) sisType = StudentIdentificationSystemType.SSN;
                else if ( randomInt11 == 9 ) sisType = StudentIdentificationSystemType.STATE;
                else if ( randomInt11 == 10 ) sisType = StudentIdentificationSystemType.STATE_MIGRANT;
                sic.setIdentificationSystem(sisType);
                s.getStudentIdentificationCode().add(sic);
            }

            // Other Names
            if (randomizeNumbers) {
                numOtherNames = random.nextInt(maxOtherNames+1);
            }
            if (includeAllData) {
                numOtherNames = Math.max(numOtherNames, 1);
            }
            for (int iName = 0; iName < numOtherNames; iName++) {
                if (s.getSex().equals(SexType.MALE)) {
                    s.getOtherName().add(nameGenerator.getMaleOtherName());
                } else {
                    s.getOtherName().add(nameGenerator.getFemaleOtherName());
                }
            }

            // Address
            if (randomizeNumbers) {
                numAddresses = random.nextInt(maxAddresses+1);
            }
            if (includeAllData) {
                numAddresses = Math.max(numAddresses, 1);
            }
            for (int iAddress = 0; iAddress < numAddresses; iAddress++) {
                s.getAddress().add(ag.getRandomAddress());
            }

            // Telephone
            if (randomizeNumbers) {
                numTelephones = random.nextInt(maxTelephones+1);
            }
            if (includeAllData) {
                numTelephones = Math.max(numTelephones, 1);
            }
            for (int iPhone = 0; iPhone < numTelephones; iPhone++) {
                try {
                    s.getTelephone().add(telephonegen.getTelephone());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            // E-mail
            if (randomizeNumbers) {
                numElectronicMailAddresses = random.nextInt(maxElectronicMailAddresses+1);
            }
            if (includeAllData) {
                numElectronicMailAddresses = Math.max(numElectronicMailAddresses, 1);
            }
            for (int iMail=0; iMail<numElectronicMailAddresses; iMail++) {
                ElectronicMail em = new ElectronicMail();
                em.setEmailAddress(s.getName().getLastSurname()+iMail+"@gmail.com");
                int randomInt4 = random.nextInt(4);
                ElectronicMailAddressType eMailType = null;
                     if ( randomInt4 == 0 ) eMailType = ElectronicMailAddressType.HOME_PERSONAL;
                else if ( randomInt4 == 1 ) eMailType = ElectronicMailAddressType.ORGANIZATION;
                else if ( randomInt4 == 2 ) eMailType = ElectronicMailAddressType.OTHER;
                else if ( randomInt4 == 3 ) eMailType = ElectronicMailAddressType.WORK;
                em.setEmailAddressType(eMailType);
                s.getElectronicMail().add(em);
            }

            // Profile Thumbnail
            s.setProfileThumbnail(s.getName().getFirstName()+"."+s.getName().getLastSurname()+".jpg");

            // Old Ethnicity
            randomInt5 = random.nextInt(5);
            OldEthnicityType oldEthType = null;
                 if ( randomInt5 == 0 ) oldEthType = OldEthnicityType.AMERICAN_INDIAN_OR_ALASKAN_NATIVE;
            else if ( randomInt5 == 1 ) oldEthType = OldEthnicityType.ASIAN_OR_PACIFIC_ISLANDER;
            else if ( randomInt5 == 2 ) oldEthType = OldEthnicityType.BLACK_NOT_OF_HISPANIC_ORIGIN;
            else if ( randomInt5 == 3 ) oldEthType = OldEthnicityType.HISPANIC;
            else if ( randomInt5 == 4 ) oldEthType = OldEthnicityType.WHITE_NOT_OF_HISPANIC_ORIGIN;
            s.setOldEthnicity(oldEthType);

            // Economically Disadvantaged
            s.setEconomicDisadvantaged(random.nextBoolean());

            // Food Services Eligibility
            int randomInt4 = random.nextInt(4);
            SchoolFoodServicesEligibilityType fseType = null;
                 if ( randomInt4 == 0 ) fseType = SchoolFoodServicesEligibilityType.FREE;
            else if ( randomInt4 == 1 ) fseType = SchoolFoodServicesEligibilityType.FULL_PRICE;
            else if ( randomInt4 == 2 ) fseType = SchoolFoodServicesEligibilityType.REDUCED_PRICE;
            else if ( randomInt4 == 3 ) fseType = SchoolFoodServicesEligibilityType.UNKNOWN;
            s.setSchoolFoodServicesEligibility(fseType);

            // All further items that require a start and end date
            // will use today and 10 weeks from today.
            String rightNow = "2011-03-04";
            String nextWeek = "2011-03-11";

            // Student Characteristics
            if (randomizeNumbers) {
                numStudentCharacteristics = random.nextInt(maxStudentCharacteristics+1);
            }
            if (includeAllData) {
                numStudentCharacteristics = Math.max(numStudentCharacteristics, 1);
            }
            for (int iSC=0; iSC<numStudentCharacteristics; iSC++) {
                    StudentCharacteristic sc = new StudentCharacteristic();
                    sc.setBeginDate(rightNow);
                    sc.setEndDate(nextWeek);
                    sc.setDesignatedBy(nameGenerator.getName().getLastSurname());
                    int randomInt11 = random.nextInt(11);
                    StudentCharacteristicType scType = null;
                         if ( randomInt11 == 0 ) scType = StudentCharacteristicType.DISPLACED_HOMEMAKER;
                    else if ( randomInt11 == 1 ) scType = StudentCharacteristicType.FOSTER_CARE;
                    else if ( randomInt11 == 2 ) scType = StudentCharacteristicType.HOMELESS;
                    else if ( randomInt11 == 3 ) scType = StudentCharacteristicType.IMMIGRANT;
                    else if ( randomInt11 == 4 ) scType = StudentCharacteristicType.MIGRATORY;
                    else if ( randomInt11 == 5 ) scType = StudentCharacteristicType.PARENT_IN_MILITARY;
                    else if ( randomInt11 == 6 ) scType = StudentCharacteristicType.PREGNANT;
                    else if ( randomInt11 == 7 ) scType = StudentCharacteristicType.SINGLE_PARENT;
                    else if ( randomInt11 == 8 ) scType = StudentCharacteristicType.UNACCOMPANIED_YOUTH;
                    else if ( randomInt11 == 9 ) scType = StudentCharacteristicType.UNSCHOOLED_ASYLEE;
                    else if ( randomInt11 == 10 ) scType = StudentCharacteristicType.UNSCHOOLED_REFUGEE;
                    sc.setCharacteristic(scType);
                    s.getStudentCharacteristics().add(sc);
            }

            // Limited English Proficiency
            randomInt4 = random.nextInt(4);
            LimitedEnglishProficiencyType lepType = null;
                 if ( randomInt4 == 0 ) lepType = LimitedEnglishProficiencyType.LIMITED;
            else if ( randomInt4 == 1 ) lepType = LimitedEnglishProficiencyType.LIMITED_MONITORED_1;
            else if ( randomInt4 == 2 ) lepType = LimitedEnglishProficiencyType.LIMITED_MONITORED_2;
            else if ( randomInt4 == 3 ) lepType = LimitedEnglishProficiencyType.NOT_LIMITED;
            s.setLimitedEnglishProficiency(lepType);

            // Languages
            if (randomizeNumbers) {
                numLanguages = random.nextInt(maxLanguages+1);
            }
            if (includeAllData) {
                numLanguages = Math.max(numLanguages, 1);
            }
            LanguagesType lt1 = new LanguagesType();
            s.setLanguages(lt1);
            for (int iLanguage = 0; iLanguage<numLanguages; iLanguage++) {
                int randomInt11 = random.nextInt(11);
                LanguageItemType liType = null;
                     if ( randomInt11 == 0 ) liType = LanguageItemType.ENGLISH;
                else if ( randomInt11 == 1 ) liType = LanguageItemType.SPANISH;
                else if ( randomInt11 == 2 ) liType = LanguageItemType.FRENCH;
                else if ( randomInt11 == 3 ) liType = LanguageItemType.MANDARIN_CHINESE;
                else if ( randomInt11 == 4 ) liType = LanguageItemType.GERMAN;
                else if ( randomInt11 == 5 ) liType = LanguageItemType.ITALIAN;
                else if ( randomInt11 == 6 ) liType = LanguageItemType.ARABIC;
                else if ( randomInt11 == 7 ) liType = LanguageItemType.FARSI_PERSIAN;
                else if ( randomInt11 == 8 ) liType = LanguageItemType.HINDI;
                else if ( randomInt11 == 9 ) liType = LanguageItemType.JAPANESE;
                else if ( randomInt11 == 10 ) liType = LanguageItemType.CZECH;
                s.getLanguages().getLanguage().add(liType);
            }

            // Home Languages
            if (randomizeNumbers) {
                numHomeLanguages = random.nextInt(maxHomeLanguages+1);
            }
            if (includeAllData) {
                numHomeLanguages = Math.max(numHomeLanguages, 1);
            }
            LanguagesType lt2 = new LanguagesType();
            s.setHomeLanguages(lt2);
            for (int iLanguage = 0; iLanguage<numLanguages; iLanguage++) {
                int randomInt11 = random.nextInt(11);
                LanguageItemType liType = null;
                     if ( randomInt11 == 0 ) liType = LanguageItemType.ENGLISH;
                else if ( randomInt11 == 1 ) liType = LanguageItemType.SPANISH;
                else if ( randomInt11 == 2 ) liType = LanguageItemType.FRENCH;
                else if ( randomInt11 == 3 ) liType = LanguageItemType.MANDARIN_CHINESE;
                else if ( randomInt11 == 4 ) liType = LanguageItemType.GERMAN;
                else if ( randomInt11 == 5 ) liType = LanguageItemType.ITALIAN;
                else if ( randomInt11 == 6 ) liType = LanguageItemType.ARABIC;
                else if ( randomInt11 == 7 ) liType = LanguageItemType.FARSI_PERSIAN;
                else if ( randomInt11 == 8 ) liType = LanguageItemType.HINDI;
                else if ( randomInt11 == 9 ) liType = LanguageItemType.JAPANESE;
                else if ( randomInt11 == 10 ) liType = LanguageItemType.CZECH;
                s.getHomeLanguages().getLanguage().add(liType);
            }

            // Disabilities
            if (randomizeNumbers) {
                numDisabilities = random.nextInt(maxDisabilities+1);
            }
            if (includeAllData) {
                numDisabilities = Math.max(numDisabilities, 1);
            }
            for (int iDisability=0; iDisability<numDisabilities; iDisability++) {
                Disability disability = new Disability();
                int randomInt15 = random.nextInt(15);
                DisabilityType disType = null;
                     if ( randomInt15 == 0 ) disType = DisabilityType.AUTISTIC_AUTISM;
                else if ( randomInt15 == 1 ) disType = DisabilityType.DEAF_BLINDNESS;
                else if ( randomInt15 == 2 ) disType = DisabilityType.DEAFNESS;
                else if ( randomInt15 == 3 ) disType = DisabilityType.DEVELOPMENTAL_DELAY;
                else if ( randomInt15 == 4 ) disType = DisabilityType.EMOTIONAL_DISTURBANCE;
                else if ( randomInt15 == 5 ) disType = DisabilityType.HEARING_AUDITORY_IMPAIRMENT;
                else if ( randomInt15 == 6 ) disType = DisabilityType.INFANTS_AND_TODDLERS_WITH_DISABILITIES;
                else if ( randomInt15 == 7 ) disType = DisabilityType.MENTAL_RETARDATION;
                else if ( randomInt15 == 8 ) disType = DisabilityType.MULTIPLE_DISABILITIES;
                else if ( randomInt15 == 9 ) disType = DisabilityType.ORTHOPEDIC_IMPAIRMENT;
                else if ( randomInt15 == 10 ) disType = DisabilityType.OTHER_HEALTH_IMPAIRMENT;
                else if ( randomInt15 == 11 ) disType = DisabilityType.SPECIFIC_LEARNING_DISABILITY;
                else if ( randomInt15 == 12 ) disType = DisabilityType.SPEECH_OR_LANGUAGE_IMPAIRMENT;
                else if ( randomInt15 == 13 ) disType = DisabilityType.TRAUMATIC_BRAIN_DELAY;
                else if ( randomInt15 == 14 ) disType = DisabilityType.VISUAL_IMPAIRMENT;
                disability.setDisability(disType);
                s.getDisabilities().add(disability);
            }

            // Section 504 Disabilities
            if (randomizeNumbers) {
                numSection504 = random.nextInt(maxSection504+1);
            }
            if (includeAllData) {
                numSection504 = Math.max(numSection504, 1);
            }
            Section504DisabilitiesType sec504 = new Section504DisabilitiesType();
            s.setSection504Disabilities(sec504);
            for (int i504=0; i504<numSection504; i504++) {
                randomInt5 = random.nextInt(5);
                Section504DisabilityItemType sec504diType = null;
                     if ( randomInt5 == 0 ) sec504diType = Section504DisabilityItemType.ATTENTION_DEFICIT_HYPERACTIVITY_DISORDER_ADHD;
                else if ( randomInt5 == 1 ) sec504diType = Section504DisabilityItemType.MEDICAL_CONDITION;
                else if ( randomInt5 == 2 ) sec504diType = Section504DisabilityItemType.MOTOR_IMPAIRMENT;
                else if ( randomInt5 == 3 ) sec504diType = Section504DisabilityItemType.OTHER;
                else if ( randomInt5 == 4 ) sec504diType = Section504DisabilityItemType.SENSORY_IMPAIRMENT;
                sec504.getSection504Disability().add(sec504diType);
            }

            // Displacement Status
            if (random.nextInt(100)<percentDisplaced) {
                s.setDisplacementStatus(random.nextBoolean() ? "Military Deployment" : "Natural Disaster");
            }

            // Program Participation
            if (randomizeNumbers) {
                numProgramParticipation = random.nextInt(maxProgramParticipation+1);
            }
            if (includeAllData) {
                numProgramParticipation = Math.max(numProgramParticipation, 1);
            }
            for (int iPP=0; iPP<numProgramParticipation; iPP++) {
                ProgramParticipation pp = new ProgramParticipation();
                pp.setBeginDate(rightNow);
                pp.setEndDate(nextWeek);
                pp.setDesignatedBy(nameGenerator.getName().getLastSurname());
                int randomInt20 = random.nextInt(20);
                ProgramType programType = null;
                     if ( randomInt20 == 0 ) programType = ProgramType.ADULT_CONTINUING_EDUCATION;
                  else if ( randomInt20 == 1 ) programType = ProgramType.ATHLETICS;
                   else if ( randomInt20 == 2 ) programType = ProgramType.BILINGUAL_SUMMER;
                   else if ( randomInt20 == 3 ) programType = ProgramType.COCURRICULAR_PROGRAMS;
                   else if ( randomInt20 == 4 ) programType = ProgramType.COMMUNITY_JUNIOR_COLLEGE_EDUCATION_PROGRAM;
                   else if ( randomInt20 == 5 ) programType = ProgramType.COMPENSATORY_SERVICES_FOR_DISADVANTAGED_STUDENTS;
                   else if ( randomInt20 == 6 ) programType = ProgramType.ENGLISH_AS_A_SECOND_LANGUAGE_ESL;
                   else if ( randomInt20 == 7 ) programType = ProgramType.EXTENDED_DAY_CHILD_CARE_SERVICES;
                   else if ( randomInt20 == 8 ) programType = ProgramType.HEAD_START;
                   else if ( randomInt20 == 9 ) programType = ProgramType.HIGH_SCHOOL_EQUIVALENCY_PROGRAM_HSEP;
                   else if ( randomInt20 == 10 ) programType = ProgramType.IMMIGRANT_EDUCATION;
                   else if ( randomInt20 == 11 ) programType = ProgramType.INTERNATIONAL_BACCALAUREATE;
                   else if ( randomInt20 == 12 ) programType = ProgramType.MAGNET_SPECIAL_PROGRAM_EMPHASIS;
                   else if ( randomInt20 == 13 ) programType = ProgramType.NEGLECTED_AND_DELINQUENT_PROGRAM;
                   else if ( randomInt20 == 14 ) programType = ProgramType.OTHER;
                   else if ( randomInt20 == 15 ) programType = ProgramType.REMEDIAL_EDUCATION;
                   else if ( randomInt20 == 16 ) programType = ProgramType.SECTION_504_PLACEMENT;
                   else if ( randomInt20 == 17 ) programType = ProgramType.SPECIAL_EDUCATION;
                   else if ( randomInt20 == 18 ) programType = ProgramType.SUBSTANCE_ABUSE_EDUCATION_PREVENTION;
                   else if ( randomInt20 == 19 ) programType = ProgramType.VOCATIONAL_EDUCATION;
                pp.setProgram(programType);
                s.getProgramParticipations().add(pp);
            }

            // Learning Styles
            if (randomizeNumbers) {
                percentVisualLearning = random.nextInt(maxVisualLearning-minVisualLearning+1);
                int originalMax = maxAuditoryLearning;
                maxAuditoryLearning = Math.min(maxAuditoryLearning, 100-percentVisualLearning);
                while (maxAuditoryLearning < 0) {
                    percentVisualLearning = random.nextInt(maxVisualLearning-minVisualLearning+1);
                    maxAuditoryLearning = Math.min(originalMax, 100-percentVisualLearning);
                }
                percentAuditoryLearning = random.nextInt(maxAuditoryLearning-minAuditoryLearning+1);
                percentTactileLearning = 100 - percentVisualLearning - percentAuditoryLearning;
            }
            LearningStyles ls = new LearningStyles();
            ls.setVisualLearning(percentVisualLearning);
            ls.setTactileLearning(percentTactileLearning);
            ls.setAuditoryLearning(percentAuditoryLearning);
            s.setLearningStyles(ls);

            // Cohort Years
            if (randomizeNumbers) {
                numCohortYears = random.nextInt(maxCohortYears+1);
            }
            if (includeAllData) {
                numCohortYears = Math.max(numCohortYears, 1);
            }
            for (int iCY=0; iCY<numCohortYears; iCY++) {
                CohortYear cy = new CohortYear();
        		cy.setSchoolYear("2011-2012");
    			int randomInt12 = random.nextInt(12);
    			CohortYearType cyType = null;
    			if ( randomInt12 == 0 ) {
    				cyType = CohortYearType.FIRST_GRADE;
   		        	cy.setSchoolYear("2011-2012");
    			}
  	            else if ( randomInt12 == 1 ) {
  	            	cyType = CohortYearType.SECOND_GRADE;
   		        	cy.setSchoolYear("2012-2013");
  	            }
   	            else if ( randomInt12 == 2 ) {
   	            	cyType = CohortYearType.THIRD_GRADE;
   		        	cy.setSchoolYear("2013-2014");
   	            }
   	            else if ( randomInt12 == 3 ) {
   	            	cyType = CohortYearType.FOURTH_GRADE;
   		        	cy.setSchoolYear("2014-2015");
   	            }
   	            else if ( randomInt12 == 4 ) {
   	            	cyType = CohortYearType.FIFTH_GRADE;
   		        	cy.setSchoolYear("2015-2016");
   	            }
   	            else if ( randomInt12 == 5 ) {
   	            	cyType = CohortYearType.SIXTH_GRADE;
   		        	cy.setSchoolYear("2016-2017");
   	            }
   	            else if ( randomInt12 == 6 ) {
   	            	cyType = CohortYearType.SEVENTH_GRADE;
   		        	cy.setSchoolYear("2017-2018");
   	            }
   	            else if ( randomInt12 == 7 ) {
   	            	cyType = CohortYearType.EIGHTH_GRADE;
   		        	cy.setSchoolYear("2018-2019");
   	            }
   	            else if ( randomInt12 == 8 ) {
   	            	cyType = CohortYearType.NINTH_GRADE;
   		        	cy.setSchoolYear("2019-2020");
   	            }
   	            else if ( randomInt12 == 9 ) {
   	            	cyType = CohortYearType.TENTH_GRADE;
   		        	cy.setSchoolYear("2020-2021");
   	            }
   	            else if ( randomInt12 == 10 ) {
   	            	cyType = CohortYearType.ELEVENTH_GRADE;
   		        	cy.setSchoolYear("2021-2022");
   	            }
   	            else if ( randomInt12 == 11 ) {
   	            	cyType = CohortYearType.TWELFTH_GRADE;
   		        	cy.setSchoolYear("2022-2023");
   	            }
        		cy.setCohortYearType(cyType);
                s.getCohortYears().add(cy);
    		}

    		// Student Indicators
        	if (randomizeNumbers) {
        		numStudentIndicators = random.nextInt(maxStudentIndicators+1);
        	}
    		if (includeAllData) {
    			numStudentIndicators = Math.max(numStudentIndicators, 1);
    		}
    		for (int iSI=0; iSI<numStudentIndicators; iSI++) {
                StudentIndicator si = new StudentIndicator();
                si.setBeginDate(rightNow);
                si.setEndDate(nextWeek);
                si.setIndicator("StudentIndicator"+iSI);
                si.setIndicatorName("IndicatorName"+iSI);
                si.setIndicatorGroup("IndicatorGroup"+iSI);
                si.setDesignatedBy(nameGenerator.getName().getLastSurname());
                s.getStudentIndicators().add(si);
            }

            // Student Login ID
            s.setLoginId(s.getName().getLastSurname()+"LoginID");

        }  // End optional student elements.

        return s;
    }
}
