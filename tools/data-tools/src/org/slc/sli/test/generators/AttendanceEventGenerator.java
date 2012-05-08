package org.slc.sli.test.generators;

import java.util.Random;

<<<<<<< HEAD
import org.slc.sli.test.edfi.entities.AttendanceEvent;
import org.slc.sli.test.edfi.entities.AttendanceEventCategoryType;
import org.slc.sli.test.edfi.entities.AttendanceEventType;
import org.slc.sli.test.edfi.entities.EducationalEnvironmentType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.StudentReferenceType;

public class AttendanceEventGenerator {
	 private String eventDate = null;
	 private boolean includeOptionalData = true;
	 private boolean includeAllData = true;
	 Random random = new Random ();

	public AttendanceEvent getAttendanceEventGenerator (StudentReferenceType studentReferenceType, SectionReferenceType sectionReferenceType ){
		AttendanceEvent ae = new AttendanceEvent ();
		if (includeAllData){
			String EventDate = "4/4/2012";
			ae.setEventDate(EventDate);
			ae.setAttendanceEventType(getAttendanceEventType());
			ae.setAttendanceEventCategory(getAttendanceEventCategoryType());
			ae.setAttendanceEventReason("The reason for the absence or tardy !");
			ae.setEducationalEnvironment(getEducationalEnvironmentType());
			ae.setStudentReference(studentReferenceType);
		}
		else {
			System.out.println ("The required data is invalid !");
		}

		if (includeOptionalData) {
			ae.setSectionReference(sectionReferenceType);
		}

		else {
			System.out.println("The optional data is invalid !");
		}

		return ae;
	}

	public AttendanceEventType getAttendanceEventType () {
		int roll = random.nextInt(2) + 1;
		switch (roll) {
			case 1: return AttendanceEventType.DAILY_ATTENDANCE;
			case 2: return AttendanceEventType.EXTRACURRICULAR_ATTENDANCE;
			case 3: return AttendanceEventType.PROGRAM_ATTENDANCE;
			default: return AttendanceEventType.SECTION_ATTENDANCE;
		}
	}

	public AttendanceEventCategoryType getAttendanceEventCategoryType () {
		int roll = random.nextInt(3) + 1;
		switch (roll) {
			case 1: return AttendanceEventCategoryType.EARLY_DEPARTURE;
			case 2: return AttendanceEventCategoryType.EXCUSED_ABSENCE;
			case 3: return AttendanceEventCategoryType.IN_ATTENDANCE;
			case 4: return AttendanceEventCategoryType.TARDY;
			default: return AttendanceEventCategoryType.UNEXCUSED_ABSENCE;
		}
	}

	public EducationalEnvironmentType getEducationalEnvironmentType () {
		int roll = random.nextInt(9) + 1;
			switch (roll) {
				case 1: return EducationalEnvironmentType.CLASSROOM;
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

		public static void main(String args[]) throws Exception {
			System.out.print("this is attendance event generator !");


		}


}
=======
 import org.slc.sli.test.edfi.entities.AttendanceEvent;
 import org.slc.sli.test.edfi.entities.AttendanceEventCategoryType;
 import org.slc.sli.test.edfi.entities.AttendanceEventType;
 import org.slc.sli.test.edfi.entities.EducationalEnvironmentType;
 import org.slc.sli.test.edfi.entities.SectionReferenceType;
 import org.slc.sli.test.edfi.entities.StudentReferenceType;

 public class AttendanceEventGenerator {
          private String eventDate = null;
          private boolean includeOptionalData = true;
          private boolean includeAllData = true;
          Random random = new Random ();

         public AttendanceEvent getAttendanceEventGenerator (StudentReferenceType studentReferenceType, SectionReferenceType sectionReferenceType ){
                AttendanceEvent ae = new AttendanceEvent ();
                 if (includeAllData){
                         String EventDate = "4/4/2012";
                        ae.setEventDate(EventDate);
                         ae.setAttendanceEventType(getAttendanceEventType());
                         ae.setAttendanceEventCategory(getAttendanceEventCategoryType());
                         ae.setAttendanceEventReason("The reason for the absence or tardy !");
                         ae.setEducationalEnvironment(getEducationalEnvironmentType());
                         ae.setStudentReference(studentReferenceType);
                 }
                 else {
                         System.out.println ("The required data is invalid !");
                 }

                 if (includeOptionalData) {
                         ae.setSectionReference(sectionReferenceType);
                 }

                 else {
                         System.out.println("The optional data is invalid !");
                 }

                 return ae;
         }

         public AttendanceEventType getAttendanceEventType () {
        	 int roll = random.nextInt(2) + 1;
                 switch (roll) {
                         case 1: return AttendanceEventType.DAILY_ATTENDANCE;
                         case 2: return AttendanceEventType.EXTRACURRICULAR_ATTENDANCE;
                         case 3: return AttendanceEventType.PROGRAM_ATTENDANCE;
                         default: return AttendanceEventType.SECTION_ATTENDANCE;
                 }
         }

         public AttendanceEventCategoryType getAttendanceEventCategoryType () {
                 int roll = random.nextInt(3) + 1;
                 switch (roll) {
                         case 1: return AttendanceEventCategoryType.EARLY_DEPARTURE;
                         case 2: return AttendanceEventCategoryType.EXCUSED_ABSENCE;
                         case 3: return AttendanceEventCategoryType.IN_ATTENDANCE;
                         case 4: return AttendanceEventCategoryType.TARDY;
                         default: return AttendanceEventCategoryType.UNEXCUSED_ABSENCE;
                 }
         }

         public EducationalEnvironmentType getEducationalEnvironmentType () {
                 int roll = random.nextInt(9) + 1;
                         switch (roll) {
                                 case 1: return EducationalEnvironmentType.CLASSROOM;
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

         public static void main(String args[]) throws Exception {
                         System.out.print("this is attendance event generator !");
                 }
 }

>>>>>>> master
