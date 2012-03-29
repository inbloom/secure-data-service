package org.slc.sli.test.generators;

import java.util.Calendar;
import java.util.Random;

import org.slc.sli.test.edfi.entities.*;

public class StudentGenerator {
    AddressGenerator ag;
    NameGenerator nameGenerator;

    public StudentGenerator(StateAbbreviationType state) {
    	this.setState(state);
    }
    
    public void setState(StateAbbreviationType state) {
        try {
            ag = new AddressGenerator(state);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public Student generate(String studentId) {
        Student s = new Student();
        Random random = new Random();
		try {
			nameGenerator = new NameGenerator();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       
        s.setStudentUniqueStateId(studentId);

        s.setSex(random.nextBoolean() ? SexType.MALE : SexType.FEMALE);

        if (s.getSex().equals(SexType.MALE)) {
        	s.setName(nameGenerator.getMaleName());
            s.getOtherName().add(nameGenerator.getMaleOtherName());
            s.getOtherName().add(nameGenerator.getMaleOtherName());
        } else {
        	s.setName(nameGenerator.getFemaleName());
            s.getOtherName().add(nameGenerator.getFemaleOtherName());
            s.getOtherName().add(nameGenerator.getFemaleOtherName());
        }

        s.setHispanicLatinoEthnicity(random.nextBoolean());

        RaceType rt = new RaceType();
        s.setRace(rt);
        s.getRace().getRacialCategory().add(RaceItemType.WHITE);

        BirthDataGenerator bdg = new BirthDataGenerator();
        s.setBirthData(bdg.generate(6+random.nextInt(11)));

        s.getAddress().add(ag.getRandomAddress());
        if (random.nextBoolean())
            s.getAddress().add(ag.getRandomAddress());
       
        TelephoneGenerator telephonegen = new TelephoneGenerator();
        try {
			s.getTelephone().add(telephonegen.getTelephone());
	        s.getTelephone().add(telephonegen.getTelephone());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        ElectronicMail em = new ElectronicMail();
        em.setEmailAddress("test@gmail.com");
        em.setEmailAddressType(ElectronicMailAddressType.HOME_PERSONAL);
        s.getElectronicMail().add(em);
        
        s.setProfileThumbnail("StudentPicture.jpg");
        
        s.setOldEthnicity(OldEthnicityType.WHITE_NOT_OF_HISPANIC_ORIGIN);
        
        s.setEconomicDisadvantaged(random.nextBoolean());

        s.setSchoolFoodServicesEligibility(SchoolFoodServicesEligibilityType.FULL_PRICE);
        
        Calendar rightNow = Calendar.getInstance();
        Calendar nextWeek = Calendar.getInstance();
        nextWeek.roll(Calendar.WEEK_OF_YEAR, 1);

        StudentCharacteristic sc = new StudentCharacteristic();
        sc.setBeginDate(rightNow);
        sc.setEndDate(nextWeek);
        sc.setCharacteristic(StudentCharacteristicType.FOSTER_CARE);
        s.getStudentCharacteristics().add(sc);

        s.setLimitedEnglishProficiency(LimitedEnglishProficiencyType.NOT_LIMITED);

        LanguagesType lt = new LanguagesType();
        s.setLanguages(lt);
        s.getLanguages().getLanguage().add(LanguageItemType.ENGLISH);
        s.getLanguages().getLanguage().add(LanguageItemType.ARABIC);
      
        s.setHomeLanguages(lt);
        
        Disability disability = new Disability();
        disability.setDisability(DisabilityType.DEVELOPMENTAL_DELAY);
        s.getDisabilities().add(disability);
        
        Section504DisabilitiesType sec504 = new Section504DisabilitiesType();
        sec504.getSection504Disability().add(Section504DisabilityItemType.MEDICAL_CONDITION);
        s.setSection504Disabilities(sec504);
        
        s.setDisplacementStatus("Military Deployment");
        
        ProgramParticipation pp = new ProgramParticipation();
        pp.setBeginDate(rightNow);
        pp.setEndDate(nextWeek);
        pp.setProgram(ProgramType.ATHLETICS);
        s.getProgramParticipations().add(pp);

        LearningStyles ls = new LearningStyles();
        ls.setVisualLearning(50);
        ls.setTactileLearning(25);
        ls.setAuditoryLearning(25);        
        s.setLearningStyles(ls);
        
        CohortYear ch = new CohortYear();
        ch.setSchoolYear("2011-2012");
        ch.setCohortYearType(CohortYearType.ELEVENTH_GRADE);
        s.getCohortYears().add(ch);
        
        StudentIndicator si = new StudentIndicator();
        si.setBeginDate(rightNow);
        si.setEndDate(nextWeek);
        si.setIndicator("This is a student indicator");
        si.setIndicatorName("IndicatorName");
        s.getStudentIndicators().add(si);
        
        s.setLoginId("StudentLoginID");
        
        return s;
    }
}
