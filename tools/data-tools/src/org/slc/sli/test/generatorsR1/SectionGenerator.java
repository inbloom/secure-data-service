/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.test.generatorsR1;

import java.util.Random;



import org.slc.sli.test.edfi.entitiesR1.CreditType;
import org.slc.sli.test.edfi.entitiesR1.Credits;
import org.slc.sli.test.edfi.entitiesR1.EducationalEnvironmentType;
import org.slc.sli.test.edfi.entitiesR1.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entitiesR1.MediumOfInstructionType;
import org.slc.sli.test.edfi.entitiesR1.PopulationServedType;
import org.slc.sli.test.edfi.entitiesR1.Program;
import org.slc.sli.test.edfi.entitiesR1.Section;
import org.slc.sli.test.edfi.entitiesR1.Student;


public class SectionGenerator {
	static Random generator = new Random();

    public static Section generateMediumFiSliXsdRI(String sectionId, String schoolId, String courseId, String sessionId, String programId) {
 	 Section section = new Section();
//        String[] temp;
//        temp = courseId.split("-");
//        String courseTemp= temp[temp.length -1];
//        section.setUniqueSectionCode(sectionId + "-" + courseTemp);
        section.setUniqueSectionCode(sectionId);
        section.setSequenceOfCourse(1);
        section.setEducationalEnvironment(getEducationalEnvironmentType());
       // construct and add the school reference
        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
        section.setSchoolId(schoolId);
        section.setSessionId(sessionId);
        section.setCourseId(courseId);
        section.setPopulationServed(getPopulationServedType());
        section.setMediumOfInstruction(getMediumOfInstructionType());
        
        
        Credits credit = new Credits();
        credit.setCredit(12.0);
        credit.setCreditConversion(6.0);
        credit.setCreditType(getCreditType());
        
        section.setAvailableCredit(credit);
        
        Program pg = new Program();
        pg.setProgramId(programId);
        section.getProgramReference().add(programId);
        
//        Student stu = new Student();
//        stu.setStudentUniqueStateId(studentId);
        
       // section.
        
        
        

        
   
        return section;
   }
    
    public static EducationalEnvironmentType getEducationalEnvironmentType()
    {
    	int roll = generator.nextInt(12) + 1;
		switch (roll) {
			case 1:  return EducationalEnvironmentType.CLASSROOM;
			case 2: return EducationalEnvironmentType.HOMEBOUND;
			case 3: return EducationalEnvironmentType.HOSPITAL_CLASS;
			case 4: return EducationalEnvironmentType.IN_SCHOOL_SUSPENSION;
			case 5: return EducationalEnvironmentType.LABORATORY;
			case 6: return EducationalEnvironmentType.MAINSTREAM_SPECIAL_EDUCATION;
			case 7: return EducationalEnvironmentType.OFF_SCHOOL_CENTER;
			case 8: return EducationalEnvironmentType.PULL_OUT_CLASS;
			case 9: return EducationalEnvironmentType.RESOURCE_ROOM;
			case 10: return EducationalEnvironmentType.SELF_CONTAINED_SPECIAL_EDUCATION;
			case 11: return EducationalEnvironmentType.SELF_STUDY;
			default: return EducationalEnvironmentType.SHOP;
		}
    }

    public static PopulationServedType getPopulationServedType(){
    	int roll = generator.nextInt(11) + 1;
    	switch (roll) {
    		case 1: return PopulationServedType.ADULT_BASIC_EDUCATION_STUDENTS;
    		case 2: return PopulationServedType.BILINGUAL_STUDENTS;
    		case 3: return PopulationServedType.CAREER_AND_TECHNICAL_EDUCATION_STUDENTS;
    		case 4: return PopulationServedType.COMPENSATORY_REMEDIAL_EDUCATION_STUDENTS;
    		case 5: return PopulationServedType.ESL_STUDENTS;
    		case 6: return PopulationServedType.GIFTED_AND_TALENTED_STUDENTS;
    		case 7: return PopulationServedType.HONORS_STUDENTS;
    		case 8: return PopulationServedType.MIGRANT_STUDENTS;
    		case 9: return PopulationServedType.REGULAR_STUDENTS;
    		case 10: return PopulationServedType.ADULT_BASIC_EDUCATION_STUDENTS;
    		
    		default: return PopulationServedType.SPECIAL_EDUCATION_STUDENTS;
    	}
    }
    
    public static MediumOfInstructionType getMediumOfInstructionType(){
       	int roll = generator.nextInt(12) + 1;
    	switch (roll) {
    		case 1: return  MediumOfInstructionType.CENTER_BASED_INSTRUCTION;
    		case 2: return  MediumOfInstructionType.CORRESPONDENCE_INSTRUCTION;
    		case 3: return  MediumOfInstructionType.FACE_TO_FACE_INSTRUCTION;
    		case 4: return  MediumOfInstructionType.INDEPENDENT_STUDY;
    		case 5: return  MediumOfInstructionType.INTERNSHIP;
    		case 6: return  MediumOfInstructionType.OTHER;
    		case 7: return  MediumOfInstructionType.OTHER_TECHNOLOGY_BASED_INSTRUCTION;
    		case 8: return  MediumOfInstructionType.TECHNOLOGY_BASED_INSTRUCTION_IN_CLASSROOM;
    		case 9: return  MediumOfInstructionType.TELEPRESENCE_VIDEO_CONFERENCE;
    		case 10: return  MediumOfInstructionType.TELEVISED;
    		case 11: return  MediumOfInstructionType.VIDEOTAPED_PRERECORDED_VIDEO;
    	default: return MediumOfInstructionType.VIRTUAL_ON_LINE_DISTANCE_LEARNING;
    	}
    }
    
    public static CreditType getCreditType() {
    	int roll = generator.nextInt(12) + 1;
    	switch (roll) {
    		case 1: return CreditType.CARNEGIE_UNIT;
    		case 2: return CreditType.NINE_MONTH_YEAR_HOUR_CREDIT;
    		case 3: return CreditType.OTHER;
    		case 4: return CreditType.QUARTER_HOUR_CREDIT;
    		case 5: return CreditType.SEMESTER_HOUR_CREDIT;
    		case 6: return CreditType.TRIMESTER_HOUR_CREDIT;
    		default: return CreditType.TWELVE_MONTH_YEAR_HOUR_CREDIT;
    	}
    }
    
}
