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


package org.slc.sli.test.generatorsR1;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;


import org.slc.sli.test.edfi.entitiesR1.ClassroomPositionType;
import org.slc.sli.test.edfi.entitiesR1.GradebookEntry;
import org.slc.sli.test.edfi.entitiesR1.RepeatIdentifierType;
import org.slc.sli.test.edfi.entitiesR1.CreditType;
import org.slc.sli.test.edfi.entitiesR1.Credits;
import org.slc.sli.test.edfi.entitiesR1.EducationalEnvironmentType;
import org.slc.sli.test.edfi.entitiesR1.MediumOfInstructionType;
import org.slc.sli.test.edfi.entitiesR1.PopulationServedType;
import org.slc.sli.test.edfi.entitiesR1.Program;
import org.slc.sli.test.edfi.entitiesR1.Section;
import org.slc.sli.test.edfi.entitiesR1.StudentGradebookEntry;
import org.slc.sli.test.edfi.entitiesR1.StudentSectionAssociation;
import org.slc.sli.test.edfi.entitiesR1.TeacherSectionAssociation;



public class SectionGenerator {
	static Random generator = new Random(31);
	private static int idCount = 0;
	private static final String[] GRADES = {"A", "B", "C", "D", "E", "F"};
	private static String thisDay, oneYearAgo, oneYearHence;
	
	 static {
	        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
	        thisDay = dateFormatter.format(new Date());
	        oneYearAgo = dateFormatter.format(new Date(new Date().getTime() - 365 * 24 * 60 * 60 * 1000));
	        oneYearHence = dateFormatter.format(new Date(new Date().getTime() + 365 * 24 * 60 * 60 * 1000));
	    }
    
	public static Section generateMediumFiSliXsdRI(String sectionId, String schoolId, String courseId, 
    		String sessionId, String programId, List<String> studentIds, List<String> teacherIds) {
 	 Section section = new Section();
//        String[] temp;
//        temp = courseId.split("-");
//        String courseTemp= temp[temp.length -1];
//        section.setUniqueSectionCode(sectionId + "-" + courseTemp);
        section.setUniqueSectionCode(sectionId);
        section.setSequenceOfCourse(1);
        section.setEducationalEnvironment(getEducationalEnvironmentType());
       
     // construct and add the school reference
//        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
//        edOrgIdentityType.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
       // edOrgIdentityType.setStateOrganizationId(schoolId);
//        SessionIdentityType sessionIdentity = new SessionIdentityType();
//        sessionIdentity.setSessionName(sessionId);
        
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
        
       
        //StudentSectionAssociation stu = new StudentSectionAssociation();
        for (int i = 0; i < studentIds.size(); i++) {
        	StudentSectionAssociation stu = new StudentSectionAssociation();
        	stu.setStudentId(studentIds.get(i));
        	stu.setBeginDate("2012-01-25");
        	stu.setEndDate("2012-05-20");
        	stu.setHomeroomIndicator(true);
        	stu.setRepeatIdentifier(getRepeatIdentifierType());
        	//section.getStudentAssociations().add(stu);
        	
        }
        
        
        for (int i = 0; i < teacherIds.size(); i++) {
        	TeacherSectionAssociation tsa = new TeacherSectionAssociation();
        	tsa.setTeacherId(teacherIds.get(i));
        	tsa.setEndDate("2012-01-25");        	
        	tsa.setEndDate("2012-05-20");
        	tsa.setHighlyQualifiedTeacher(true);
        	tsa.setClassroomPosition(getClassroomPositionType());
        	section.getTeacherAssociations().add(tsa);

        	
        }
    
        
        for (int i = 0; i < org.slc.sli.test.edfi.entities.meta.relations.MetaRelations.GRADEBOOKENTRY_PER_SECTION; i++) {
            idCount++;
            GradebookEntry gbe = new GradebookEntry();
            gbe.setDateAssigned(thisDay);

            String gradeBookEntry = "Quiz Test".split(" ")[generator.nextInt(2)] + " " + idCount;
            gbe.setDescription(gradeBookEntry);
            gbe.setGradebookEntryType(gradeBookEntry);
            
            for(int k = 0; k < studentIds.size(); k++ ) {
            	StudentGradebookEntry sgbe = new StudentGradebookEntry();
            	//sgbe.setStudentId(studentIds.get(k));
                sgbe.setDateFulfilled(thisDay);
                sgbe.setLetterGradeEarned(GRADES[generator.nextInt(GRADES.length)]);
                sgbe.setNumericGradeEarned(BigInteger.ONE);
                gbe.getStudentGradebookEntries().add(sgbe);

            }
            section.getGradeBookEntries().add(gbe);
        }
        return section;
   }
    
 
    public static ClassroomPositionType getClassroomPositionType() {
    	int roll = generator.nextInt(2) + 1;
		switch (roll) {
		case 1:return ClassroomPositionType.ASSISTANT_TEACHER;
		case 2:return ClassroomPositionType.SUBSTITUTE_TEACHER;
		case 3:return ClassroomPositionType.SUPPORT_TEACHER;
		
		
		default: return ClassroomPositionType.TEACHER_OF_RECORD;
	
	}
    	
    }
    public static RepeatIdentifierType getRepeatIdentifierType() {
    	int roll = generator.nextInt(2) + 1;
		switch (roll) {
			case 1:return RepeatIdentifierType.REPEATED_COUNTED_IN_GRADE_POINT_AVERAGE;
			case 2:return RepeatIdentifierType.REPEATED_NOT_COUNTED_IN_GRADE_POINT_AVERAGE;
			case 3:return RepeatIdentifierType.NOT_REPEATED;
			
			
			default: return RepeatIdentifierType.OTHER;
		
		}
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
