package org.slc.sli.test.generators;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.CareerPathwayType;
import org.slc.sli.test.edfi.entities.Course;
import org.slc.sli.test.edfi.entities.CourseCode;
import org.slc.sli.test.edfi.entities.CourseCodeSystemType;
import org.slc.sli.test.edfi.entities.CourseDefinedByType;
import org.slc.sli.test.edfi.entities.CourseGPAApplicabilityType;
import org.slc.sli.test.edfi.entities.CourseIdentityType;
import org.slc.sli.test.edfi.entities.CourseLevelCharacteristicItemType;
import org.slc.sli.test.edfi.entities.CourseLevelCharacteristicsType;
import org.slc.sli.test.edfi.entities.CourseLevelType;
import org.slc.sli.test.edfi.entities.CourseReferenceType;
import org.slc.sli.test.edfi.entities.CreditType;
import org.slc.sli.test.edfi.entities.Credits;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.GradeLevelsType;

public class CourseGenerator {

    private static final Logger log = Logger.getLogger(CourseGenerator.class);
    private static String file_course = "database/course/course.csv";
    private static Random rand = new Random();
    private List<Course> courses = null;
    private static boolean loaded = false;
    private int courseCount = 0;
   //private Course course;
    private static int counter =0;
    private static CourseGenerator cg;

    public CourseGenerator(GradeLevelType grade) throws Exception {
        if (!loaded) {
            loaded = true;
            load();
        }
        if (grade == GradeLevelType.FIRST_GRADE)
            courses = new ArrayList<Course>(grade1Courses);
        else if (grade == GradeLevelType.SECOND_GRADE)
            courses = new ArrayList<Course>(grade2Courses);
        else if (grade == GradeLevelType.THIRD_GRADE)
            courses = new ArrayList<Course>(grade3Courses);
        else if (grade == GradeLevelType.FOURTH_GRADE)
            courses = new ArrayList<Course>(grade4Courses);
        else if (grade == GradeLevelType.FIFTH_GRADE)
            courses = new ArrayList<Course>(grade5Courses);
        else if (grade == GradeLevelType.SIXTH_GRADE)
            courses = new ArrayList<Course>(grade6Courses);
        else if (grade == GradeLevelType.SEVENTH_GRADE)
            courses = new ArrayList<Course>(grade7Courses);
        else if (grade == GradeLevelType.EIGHTH_GRADE)
            courses = new ArrayList<Course>(grade8Courses);
        else if (grade == GradeLevelType.NINTH_GRADE)
            courses = new ArrayList<Course>(grade9Courses);
        else if (grade == GradeLevelType.TENTH_GRADE)
            courses = new ArrayList<Course>(grade10Courses);
        else if (grade == GradeLevelType.ELEVENTH_GRADE)
            courses = new ArrayList<Course>(grade11Courses);
        else if (grade == GradeLevelType.TWELFTH_GRADE)
            courses = new ArrayList<Course>(grade12Courses);

        courseCount = courses.size();

    }

    static List<Course> grade1Courses = new ArrayList<Course>();
    static List<Course> grade2Courses = new ArrayList<Course>();
    static List<Course> grade3Courses = new ArrayList<Course>();
    static List<Course> grade4Courses = new ArrayList<Course>();
    static List<Course> grade5Courses = new ArrayList<Course>();
    static List<Course> grade6Courses = new ArrayList<Course>();
    static List<Course> grade7Courses = new ArrayList<Course>();
    static List<Course> grade8Courses = new ArrayList<Course>();
    static List<Course> grade9Courses = new ArrayList<Course>();
    static List<Course> grade10Courses = new ArrayList<Course>();
    static List<Course> grade11Courses = new ArrayList<Course>();
    static List<Course> grade12Courses = new ArrayList<Course>();

    private static int getRand() {
        int num = rand.nextInt();
        return num < 0 ? -1 * num : num;
    }

    private static void load() throws Exception {
        BufferedReader courseReader = new BufferedReader(new InputStreamReader(new FileInputStream(file_course)));
        // #Grade 10;Advanced Placement;Individually Paced;AP Calculus AB;M
        String courseLine;
        while ((courseLine = courseReader.readLine()) != null) {
            String[] courseParts = courseLine.split(";");
            if (courseParts.length < 6) {
                String grade = courseParts[0];
                String topic = courseParts[1];
                String pacing = courseParts[2];
                String name = courseParts[3];
                String marks = courseParts[4];

                Course course = new Course();
                course.setId((grade + topic + name).replace("[ ,]", "-"));
                course.setCourseTitle(name);
                course.setNumberOfParts(getRand() % 3 + 1);
                course.setCourseLevel(CourseLevelType.GENERAL_OR_REGULAR);
                course.setCourseLevelCharacteristics(new CourseLevelCharacteristicsType());
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.ADVANCED);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.ADVANCED_PLACEMENT);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.CORE_SUBJECT);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.CORRESPONDENCE);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.CTE);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.DISTANCE_LEARNING);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.DUAL_CREDIT);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.GRADUATION_CREDIT);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.HONORS);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.IB_COURSE);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.MAGNET);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.PRE_AP);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.PRE_IB);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.REMEDIAL);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.STUDENTS_WITH_DISABILITIES);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.BASIC);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.GENERAL);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.GIFTED_AND_TALENTED);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.COLLEGE_LEVEL);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.ENGLISH_LANGUAGE_LEARNER);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.UNTRACKED);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.ACCEPTED_AS_HIGH_SCHOOL_EQUIVALENT);
                course.getCourseLevelCharacteristics().getCourseLevelCharacteristic()
                        .add(CourseLevelCharacteristicItemType.OTHER);

                course.setGradesOffered(new GradeLevelsType());
                if (grade.trim().equals("Grade 1")) {
                    course.getGradesOffered().getGradeLevel().add(GradeLevelType.FIRST_GRADE);
                    grade1Courses.add(course);
                } else if (grade.trim().equals("Grade 2")) {
                    course.getGradesOffered().getGradeLevel().add(GradeLevelType.SECOND_GRADE);
                    grade2Courses.add(course);
                } else if (grade.trim().equals("Grade 3")) {
                    course.getGradesOffered().getGradeLevel().add(GradeLevelType.THIRD_GRADE);
                    grade3Courses.add(course);
                } else if (grade.trim().equals("Grade 4")) {
                    course.getGradesOffered().getGradeLevel().add(GradeLevelType.FOURTH_GRADE);
                    grade4Courses.add(course);
                } else if (grade.trim().equals("Grade 5")) {
                    course.getGradesOffered().getGradeLevel().add(GradeLevelType.FIFTH_GRADE);
                    grade5Courses.add(course);
                } else if (grade.trim().equals("Grade 6")) {
                    course.getGradesOffered().getGradeLevel().add(GradeLevelType.SIXTH_GRADE);
                    grade6Courses.add(course);
                } else if (grade.trim().equals("Grade 7")) {
                    course.getGradesOffered().getGradeLevel().add(GradeLevelType.SEVENTH_GRADE);
                    grade7Courses.add(course);
                } else if (grade.trim().equals("Grade 8")) {
                    course.getGradesOffered().getGradeLevel().add(GradeLevelType.EIGHTH_GRADE);
                    grade8Courses.add(course);
                } else if (grade.trim().equals("Grade 9")) {
                    course.getGradesOffered().getGradeLevel().add(GradeLevelType.NINTH_GRADE);
                    grade9Courses.add(course);
                } else if (grade.trim().equals("Grade 10")) {
                    course.getGradesOffered().getGradeLevel().add(GradeLevelType.TENTH_GRADE);
                    grade10Courses.add(course);
                } else if (grade.trim().equals("Grade 11")) {
                    course.getGradesOffered().getGradeLevel().add(GradeLevelType.ELEVENTH_GRADE);
                    grade11Courses.add(course);
                } else if (grade.trim().equals("Grade 12")) {
                    course.getGradesOffered().getGradeLevel().add(GradeLevelType.TWELFTH_GRADE);
                    grade12Courses.add(course);
                }

                course.setSubjectArea(AcademicSubjectType.OTHER);
                course.setCourseDescription(name);

                course.setDateCourseAdopted("2011-03-04");

                course.setHighSchoolCourseRequirement(rand.nextBoolean());
                course.setCourseGPAApplicability(CourseGPAApplicabilityType.APPLICABLE);
                int defBy = getRand() % 3;
                if (defBy == 0)
                    course.setCourseDefinedBy(CourseDefinedByType.NATIONAL_ORGANIZATION);
                else if (defBy == 1)
                    course.setCourseDefinedBy(CourseDefinedByType.LEA);
                else
                    course.setCourseDefinedBy(CourseDefinedByType.SEA);

                Credits credits = new Credits();
                credits.setCredit(new BigDecimal(getRand() % 5));
                credits.setCreditConversion(new BigDecimal(getRand() % 5));
                int cType = getRand() % 7;
                if (cType == 0)
                    credits.setCreditType(CreditType.CARNEGIE_UNIT);
                else if (cType == 1)
                    credits.setCreditType(CreditType.NINE_MONTH_YEAR_HOUR_CREDIT);
                else if (cType == 2)
                    credits.setCreditType(CreditType.OTHER);
                else if (cType == 3)
                    credits.setCreditType(CreditType.QUARTER_HOUR_CREDIT);
                else if (cType == 4)
                    credits.setCreditType(CreditType.SEMESTER_HOUR_CREDIT);
                else if (cType == 5)
                    credits.setCreditType(CreditType.TRIMESTER_HOUR_CREDIT);
                else if (cType == 6)
                    credits.setCreditType(CreditType.TWELVE_MONTH_YEAR_HOUR_CREDIT);

                course.setMinimumAvailableCredit(credits);
                course.setMaximumAvailableCredit(credits);
                int pType = getRand() % 3;
                if (pType == 0)
                    course.setCareerPathway(CareerPathwayType.ARTS_A_V_TECHNOLOGY_AND_COMMUNICATIONS);
                else if (pType == 1)
                    course.setCareerPathway(CareerPathwayType.BUSINESS_MANAGEMENT_AND_ADMINISTRATION);
                else if (pType == 2)
                    course.setCareerPathway(CareerPathwayType.EDUCATION_AND_TRAINING);

                // course.setEducationOrganizationReference(EducationalOrgReferenceType);
                // course.getLearningObjectiveReference().add(LearningObjectiveReferenceType);
                // course.getCompetencyLevels().add(CompetencyLevelDescriptorType)
            } else {
                log.warn(file_course + ": Invalid line [" + courseLine + "]. Less than 5 components.");
                System.out.println("Ignoring line[" + courseLine + "].");
            }
        }
    }

    public Course getCourse(String courseId, String schoolId) {
    	Course course = null;
    	//if(courseCount >= 1) {
    	if (courses.size() > 0) {
	    	course = courses.remove(0);
	    	
	        courseCount--;
	        course.setId(courseId);
	        CourseCode cc = new CourseCode();
	        cc.setID(courseId + courseCount);
	        //cc.setID(course.getId() + courseCount);
	        
			cc.setAssigningOrganizationCode( "200" );
			cc.setIdentificationSystem(CourseCodeSystemType.CSSC_COURSE_CODE);
	        course.getCourseCode().add(cc);
	
	        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
	        edOrgIdentityType.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
	
	        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
	        schoolRef.setEducationalOrgIdentity(edOrgIdentityType);
	        course.setEducationOrganizationReference(schoolRef);
    	}
       // courses.add(course);        
        return course;
    }

    public int getCourseCount() {
        return courseCount;
    }
    
    
    public static CourseReferenceType getCourseReferenceType(Course course)
    {
    	CourseReferenceType crt = new CourseReferenceType();
    	CourseIdentityType ci = new CourseIdentityType();
    	crt.setCourseIdentity(ci) ;
    	ci.getCourseCode().addAll(course.getCourseCode());
    	return crt;
    }
    
  
    
    
    public static Course generateLowFi(String id, String schoolId) throws Exception {
   
    	
        Course course = new Course();
        course.setCourseTitle(id);
        course.setNumberOfParts(1);
        
		CourseCode CourseCode = new CourseCode();
		CourseCode.setID(id);
		CourseCode.setIdentificationSystem( CourseCodeSystemType.CSSC_COURSE_CODE );
		CourseCode.setAssigningOrganizationCode( "200" );
		course.getCourseCode().add(CourseCode);

        // construct and add the school reference
        EducationalOrgIdentityType edOrgIdentityType = new EducationalOrgIdentityType();
        edOrgIdentityType.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);

        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentityType);

        course.setEducationOrganizationReference(schoolRef);

        return course;
    }

    /*
    public static void main(String[] args) throws Exception {
        CourseGenerator generator = new CourseGenerator(GradeLevelType.THIRD_GRADE);
        System.out.println("Count is " + generator.getCourseCount());
        int cId = 10;
        while (generator.getCourseCount() > 0) {
            Course course = generator.getCourse("MyCourseId " + cId++);
            String courseDesc = "\n\nId : " + course.getId() + ",\n" + "CourseTitle : " + course.getCourseTitle()
                    + ",\n" + "NumberOfParts : " + course.getNumberOfParts() + ",\n" + "CourseLevel : "
                    + course.getCourseLevel() + ",\n" + "CourseLevelCharacteristics : "
                    + course.getCourseLevelCharacteristics() + ",\n" + "GradesOffered : " + course.getGradesOffered()
                    + ",\n"
                    + "SubjectArea : "
                    + course.getSubjectArea()
                    + ",\n"
                    + "CourseDescription : "
                    + course.getCourseDescription()
                    + ",\n"
                    + "DateCourseAdopted : "
                    + course.getDateCourseAdopted()
                    + ",\n"
                    +
                    // "HighSchoolCourseRequirement : " + course.getHighSchoolCourseRequirement() +
                    // ",\n" +
                    "CourseGPAApplicability : " + course.getCourseGPAApplicability() + ",\n" + "CourseDefinedBy : "
                    + course.getCourseDefinedBy() + ",\n" + "MinimumAvailableCredit : "
                    + course.getMinimumAvailableCredit() + ",\n" + "MaximumAvailableCredit : "
                    + course.getMaximumAvailableCredit() + ",\n" + "CareerPathway : " + course.getCareerPathway()
                    + ",\n" + "EducationOrganizationReference : " + course.getEducationOrganizationReference();

            log.info(courseDesc);
            System.out.println(courseDesc);
        }
    }
    */
}
