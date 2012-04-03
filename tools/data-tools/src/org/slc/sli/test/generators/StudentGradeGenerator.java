package org.slc.sli.test.generators;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.slc.sli.test.edfi.entities.AcademicHonor;
import org.slc.sli.test.edfi.entities.AcademicHonorsType;
import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.AdditionalCreditType;
import org.slc.sli.test.edfi.entities.AdditionalCredits;
import org.slc.sli.test.edfi.entities.ClassRanking;
import org.slc.sli.test.edfi.entities.CompetencyLevelDescriptor;
import org.slc.sli.test.edfi.entities.CompetencyLevelDescriptorType;
import org.slc.sli.test.edfi.entities.Course;
import org.slc.sli.test.edfi.entities.CourseAttemptResultType;
import org.slc.sli.test.edfi.entities.CourseCode;
import org.slc.sli.test.edfi.entities.CourseCodeSystemType;
import org.slc.sli.test.edfi.entities.CourseIdentityType;
import org.slc.sli.test.edfi.entities.CourseReferenceType;
import org.slc.sli.test.edfi.entities.CourseRepeatCodeType;
import org.slc.sli.test.edfi.entities.CourseTranscript;
import org.slc.sli.test.edfi.entities.CreditType;
import org.slc.sli.test.edfi.entities.Credits;
import org.slc.sli.test.edfi.entities.Diploma;
import org.slc.sli.test.edfi.entities.DiplomaLevelType;
import org.slc.sli.test.edfi.entities.DiplomaType;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationCode;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationSystemType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.Grade;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.GradeType;
import org.slc.sli.test.edfi.entities.GradebookEntry;
import org.slc.sli.test.edfi.entities.GradingPeriodIdentityType;
import org.slc.sli.test.edfi.entities.GradingPeriodReferenceType;
import org.slc.sli.test.edfi.entities.GradingPeriodType;
import org.slc.sli.test.edfi.entities.LearningObjective;
import org.slc.sli.test.edfi.entities.LearningObjectiveIdentityType;
import org.slc.sli.test.edfi.entities.LearningObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.LearningStandardId;
import org.slc.sli.test.edfi.entities.LearningStandardIdentityType;
import org.slc.sli.test.edfi.entities.LearningStandardReferenceType;
import org.slc.sli.test.edfi.entities.LocalEducationAgency;
import org.slc.sli.test.edfi.entities.MethodCreditEarnedType;
import org.slc.sli.test.edfi.entities.Name;
import org.slc.sli.test.edfi.entities.OtherName;
import org.slc.sli.test.edfi.entities.PerformanceBaseType;
import org.slc.sli.test.edfi.entities.RaceItemType;
import org.slc.sli.test.edfi.entities.RaceType;
import org.slc.sli.test.edfi.entities.Recognition;
import org.slc.sli.test.edfi.entities.RecognitionType;
import org.slc.sli.test.edfi.entities.ReferenceType;
import org.slc.sli.test.edfi.entities.ReportCard;
import org.slc.sli.test.edfi.entities.School;
import org.slc.sli.test.edfi.entities.Section;
import org.slc.sli.test.edfi.entities.SectionIdentityType;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.Session;
import org.slc.sli.test.edfi.entities.SessionIdentityType;
import org.slc.sli.test.edfi.entities.SessionReferenceType;
import org.slc.sli.test.edfi.entities.SexType;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;
import org.slc.sli.test.edfi.entities.StateEducationAgency;
import org.slc.sli.test.edfi.entities.StudentAcademicRecord;
import org.slc.sli.test.edfi.entities.StudentCompetency;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjective;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjectiveIdentityType;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.StudentGradebookEntry;
import org.slc.sli.test.edfi.entities.StudentIdentificationCode;
import org.slc.sli.test.edfi.entities.StudentIdentificationSystemType;
import org.slc.sli.test.edfi.entities.StudentIdentityType;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.StudentSectionAssociationIdentityType;
import org.slc.sli.test.edfi.entities.StudentSectionAssociationReferenceType;
import org.slc.sli.test.edfi.entities.TermType;

public class StudentGradeGenerator {

	//private Calendar thisDay    = GregorianCalendar.getInstance();
	//private Calendar oneYearAgo = GregorianCalendar.getInstance();
	//private Calendar oneYearHence = GregorianCalendar.getInstance();
	private NameGenerator nameGenerator = new NameGenerator();
	private Name name = nameGenerator.getMaleName();
	private OtherNameGenerator otherNameGenerator = new OtherNameGenerator();
	private OtherName otherName = otherNameGenerator.getName();
	private static Random rand = new Random(); 
	private String thisDay, oneYearAgo, oneYearHence;
	
	public StudentGradeGenerator() throws Exception
	{
    	DateFormat dateFormatter = new SimpleDateFormat("YYYYmmDD");
     	thisDay      = dateFormatter.format(new Date());
	    oneYearAgo   = dateFormatter.format(new Date(new Date().getTime() - 365*24*60*60*1000));
	    oneYearHence = dateFormatter.format(new Date(new Date().getTime() + 365*24*60*60*1000));
	    //oneYearAgo.roll(Calendar.YEAR, -1);
	    //oneYearHence.roll(Calendar.YEAR, 1);
	}

	private static int getRand()
	{
		int num = rand.nextInt();
		return num < 0? -1 * num:num;
	}
	
	public StudentReferenceType getStudentRef()
	{
		StudentReferenceType studentRef = new StudentReferenceType();
		StudentIdentityType identity = new StudentIdentityType();
		studentRef.setStudentIdentity(identity);
		identity.setStudentUniqueStateId("StudentUniqueStateId");
		StudentIdentificationCode idCode = new StudentIdentificationCode();
		identity.getStudentIdentificationCode().add(idCode);
		idCode.setIdentificationCode("IdentificationCode");
		idCode.setIdentificationSystem( StudentIdentificationSystemType.FEDERAL);
		idCode.setAssigningOrganizationCode( "STUDENT ASIGNING ORG CODE" );
		identity.setName(name);
		identity.getOtherName().add(otherName);
		identity.setSex(SexType.MALE);
		//#StudentIdentity.setHispanicLatinoEthnicity(new JAXBElement<Boolean>);
		RaceType race = new RaceType();
		identity.setRace(race);
		race.getRacialCategory().add(RaceItemType.WHITE);	
		return studentRef;
	}
	
	public EducationOrgIdentificationCode getEducationOrgIdentificationCode()
	{
		EducationOrgIdentificationCode edOrg = new EducationOrgIdentificationCode();	    
		edOrg.setID("ID");
		edOrg.setIdentificationSystem(EducationOrgIdentificationSystemType.FEDERAL );
		return edOrg;
	}
	
	public SessionReferenceType getSessionRef(EducationOrgIdentificationCode edOrg)
	{
		SessionReferenceType sessionRef = new SessionReferenceType();	
		SessionIdentityType sessId = new SessionIdentityType();
		sessionRef.setSessionIdentity(sessId);
		sessId.setSchoolYear("SchoolYear");
		sessId.setTerm(TermType.FALL_SEMESTER);
		sessId.setSessionName("SessionName");
		sessId.getStateOrganizationIdOrEducationOrgIdentificationCode().add(edOrg);
		return sessionRef;
	}
	
	public GradingPeriodReferenceType getGradingPeriod(EducationOrgIdentificationCode edOrg)
	{
		GradingPeriodReferenceType GradingPeriodRef = new GradingPeriodReferenceType();
		GradingPeriodIdentityType GradingPeriodIdentityType = new GradingPeriodIdentityType();
		GradingPeriodRef.setGradingPeriodIdentity(GradingPeriodIdentityType);
	    GradingPeriodIdentityType.setGradingPeriod(GradingPeriodType.END_OF_YEAR);	
        GradingPeriodIdentityType.setSchoolYear("2012"); 
        GradingPeriodIdentityType.getStateOrganizationIdOrEducationOrgIdentificationCode().add(edOrg);
	    //GradingPeriod GradingPeriod = new GradingPeriod();
		//GradingPeriod.setBeginDate(oneYearAgo);
		//GradingPeriod.setEndDate(oneYearHence);
		//GradingPeriod.setTotalInstructionalDays(100);
		//GradingPeriod.setId("GradingId-1");
		return GradingPeriodRef;
	}
	
	public StudentSectionAssociationReferenceType getStudentSectionAssociationReference()
	{
		StudentSectionAssociationReferenceType StudentSectionAssociationReference = new StudentSectionAssociationReferenceType();
		StudentSectionAssociationIdentityType StudentSectionAssociationIdentity = new StudentSectionAssociationIdentityType();
		StudentSectionAssociationReference.setStudentSectionAssociationIdentity(StudentSectionAssociationIdentity);
		StudentIdentityType StudentIdentity = new StudentIdentityType();
		StudentSectionAssociationIdentity.setStudentIdentity(StudentIdentity);
		StudentIdentity.setStudentUniqueStateId("StudentUniqueStateId");
		StudentIdentificationCode StudentIdentificationCode = new StudentIdentificationCode();
		StudentIdentity.getStudentIdentificationCode().add(StudentIdentificationCode);
		StudentIdentificationCode.setIdentificationCode("IdentificationCode");
		StudentIdentificationCode.setIdentificationSystem( StudentIdentificationSystemType.FEDERAL );
		StudentIdentificationCode.setAssigningOrganizationCode( "300" );

		StudentIdentity.setName(name);
		StudentIdentity.getOtherName().add(otherName);
		StudentIdentity.setBirthDate(thisDay);
		StudentIdentity.setSex(SexType.MALE);
		RaceType Race = new RaceType();
		StudentIdentity.setRace(Race);
		Race.getRacialCategory().add(RaceItemType.WHITE);

		SectionIdentityType SectionIdentity = new SectionIdentityType();
		StudentSectionAssociationIdentity.setSectionIdentity(SectionIdentity);
		SectionIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add("StateOrganizationId");
		EducationOrgIdentificationCode EducationOrgIdentificationCode = new EducationOrgIdentificationCode();
		EducationOrgIdentificationCode.setID("ID");
		EducationOrgIdentificationCode.setIdentificationSystem( EducationOrgIdentificationSystemType.FEDERAL );
		SectionIdentity.setSchoolYear("SchoolYear");
		SectionIdentity.setTerm(TermType.FALL_SEMESTER);
		SectionIdentity.setClassPeriodName("ClassPeriodName");
		SectionIdentity.setLocation("Location");
		
		CourseCode CourseCode = new CourseCode();
		SectionIdentity.setCourseCode(CourseCode);
		CourseCode.setID("ID");
		CourseCode.setIdentificationSystem( CourseCodeSystemType.CSSC_COURSE_CODE );
		CourseCode.setAssigningOrganizationCode( "300" );
		SectionIdentity.setLocalCourseCode("LocalCourseCode");
		SectionIdentity.setUniqueSectionCode("UniqueSectionCode");
	   
	   return StudentSectionAssociationReference;
	}
	
	public LearningObjectiveReferenceType getLearningObjectiveReferenceType()
	{
		LearningObjectiveReferenceType LearningObjectiveReference = new LearningObjectiveReferenceType();
		LearningObjectiveIdentityType LearningObjectiveIdentity = new LearningObjectiveIdentityType();
		LearningObjectiveReference.setLearningObjectiveIdentity(LearningObjectiveIdentity);
		LearningStandardId LearningObjectiveId = new LearningStandardId();
		LearningObjectiveIdentity.getLearningObjectiveIdOrObjective().add(LearningObjectiveId);
		LearningObjectiveId.setIdentificationCode("IdentificationCode");
		LearningObjectiveId.setContentStandardName( "300" );
		LearningObjectiveIdentity.getLearningObjectiveIdOrObjective().add("Objective");
		return LearningObjectiveReference;
	}
	
	public StudentAcademicRecord getStudentAcademicRecord( 
			StudentReferenceType studentRef , 
			SessionReferenceType sessionRef, 
			List<ReferenceType>reportCardRef, 
			ReferenceType diplomaRef) throws Exception
	{
		StudentAcademicRecord sar = new StudentAcademicRecord();

		Credits earned = new Credits();
		sar.setCumulativeCreditsEarned(earned);
		earned.setCredit(new BigDecimal(3));
		earned.setCreditType( CreditType.CARNEGIE_UNIT);
		earned.setCreditConversion( new BigDecimal(1) );

		Credits attempted = new Credits();
		sar.setCumulativeCreditsAttempted(attempted);
		attempted.setCredit(new BigDecimal(3));
		attempted.setCreditType( CreditType.CARNEGIE_UNIT);
		attempted.setCreditConversion( new BigDecimal(1) );

		sar.setCumulativeGradePointsEarned( new BigDecimal(getRand()%3) );
		sar.setCumulativeGradePointAverage( new BigDecimal(getRand()%3) );
		
		sar.setGradeValueQualifier("GradeValueQualifier 90-100%=A, 80-90%=B,  70-80%=C 60-70%=d");

		ClassRanking ranking = new ClassRanking();
		sar.setClassRanking(ranking);
		ranking.setClassRank(getRand()%20);
		ranking.setClassRankingDate(thisDay);
		ranking.setPercentageRanking(getRand()%70);
		ranking.setTotalNumberInClass(30);

		//TODO:add variance
		AcademicHonor honors = new AcademicHonor();
		sar.getAcademicHonors().add(honors);
		honors.setAcademicHonorsType(AcademicHonorsType.ATTENDANCE_AWARD);
		honors.setHonorsDescription("Good Attendance Honor");
		honors.setHonorAwardDate(oneYearAgo);

		//TODO:add variance
		Recognition recognitions = new Recognition();
		sar.getRecognitions().add(recognitions);
		recognitions.setRecognitionType(RecognitionType.ATHLETIC_AWARDS);
		recognitions.setRecognitionDescription("Best Athlete Recognition");
		recognitions.setRecognitionAwardDate(thisDay);

		//TODO:should depend on age
		sar.setProjectedGraduationDate(oneYearHence);

		if(studentRef != null)sar.setStudentReference(studentRef);
		if(sessionRef != null)sar.setSessionReference(sessionRef);
		if(reportCardRef != null)sar.getReportCardReference().addAll(reportCardRef);
		if(diplomaRef != null)sar.setDiplomaReference(diplomaRef);
		return sar;
	}

	public ReportCard getReportCard(StudentReferenceType studentRef, GradingPeriodReferenceType gradingPeriodRef) throws Exception
	{
		ReportCard ReportCard = new ReportCard();
		ReferenceType GradeReference = new ReferenceType();
		ReportCard.getGradeReference().add(GradeReference);
		ReferenceType StudentCompetencyReference = new ReferenceType();
		ReportCard.getStudentCompetencyReference().add(StudentCompetencyReference);
		ReportCard.setGPAGivenGradingPeriod(new BigDecimal(1));
		ReportCard.setGPACumulative(new BigDecimal(1));
		ReportCard.setNumberOfDaysAbsent(new BigDecimal(getRand()%10));
		ReportCard.setNumberOfDaysInAttendance(new BigDecimal(getRand()%100));
		ReportCard.setNumberOfDaysTardy(new Integer(getRand()%20));
		ReportCard.setStudentReference(studentRef);
		ReportCard.setGradingPeriodReference(gradingPeriodRef);
		return ReportCard;
	}

	public Grade getGrade(StudentSectionAssociationReferenceType StudentSectionAssociationReference, GradingPeriodReferenceType gradingPeriodRef) throws Exception
	{
		Grade Grade = new Grade();
		Grade.setLetterGradeEarned("A B C D E F".split(" ")[getRand()%6]); 
		Grade.setNumericGradeEarned(new BigInteger("3"));
		Grade.setDiagnosticStatement("Grade Exam Taken");
		Grade.setGradeType(GradeType.EXAM);
		Grade.setPerformanceBaseConversion(PerformanceBaseType.BASIC);
		Grade.setStudentSectionAssociationReference(StudentSectionAssociationReference);
		Grade.setGradingPeriodReference(gradingPeriodRef);
		return Grade;
	}

	public StudentCompetency getStudentCompetency(StudentSectionAssociationReferenceType StudentSectionAssociationReferenceType, LearningObjectiveReferenceType LearningObjectiveReference) throws Exception 
	{
		StudentCompetency StudentCompetency = new StudentCompetency();
		CompetencyLevelDescriptorType CompetencyLevel = new CompetencyLevelDescriptorType();
		StudentCompetency.setCompetencyLevel(CompetencyLevel);
		//CompetencyLevel.getCodeValueOrDescription().add("asd");
		StudentCompetency.setDiagnosticStatement("DiagnosticStatement");
		StudentCompetency.setStudentSectionAssociationReference(StudentSectionAssociationReferenceType);
		StudentCompetency.setLearningObjectiveReference(LearningObjectiveReference);
		StudentCompetencyObjectiveReferenceType StudentCompetencyObjectiveReference = new StudentCompetencyObjectiveReferenceType();
		StudentCompetency.setStudentCompetencyObjectiveReference(StudentCompetencyObjectiveReference);
		StudentCompetencyObjectiveIdentityType StudentCompetencyObjectiveIdentity = new StudentCompetencyObjectiveIdentityType();
		StudentCompetencyObjectiveReference.setStudentCompetencyObjectiveIdentity(StudentCompetencyObjectiveIdentity);
		//StudentCompetencyObjectiveIdentity.getStudentCompetencyObjectiveIdOrObjective().add("")
		return StudentCompetency;
	}

	public Diploma getDiploma()
	{
		Diploma Diploma = new Diploma();
		Diploma.setDiplomaAwardDate(thisDay);
		Diploma.setDiplomaLevel(DiplomaLevelType.DISTINGUISHED);
		Diploma.setDiplomaType(DiplomaType.CERTIFICATE_OF_ATTENDANCE);
		Diploma.setCTECompleter(true);
		AcademicHonor AcademicHonors = new AcademicHonor();
		Diploma.getAcademicHonors().add(AcademicHonors);
		AcademicHonors.setAcademicHonorsType(AcademicHonorsType.ATTENDANCE_AWARD);
		AcademicHonors.setHonorsDescription("HonorsDescription");
		AcademicHonors.setHonorAwardDate(thisDay);
		Recognition Recognitions = new Recognition();
		Diploma.getRecognitions().add(Recognitions);
		Recognitions.setRecognitionType(RecognitionType.AWARDING_OF_UNITS_OF_VALUE);
		Recognitions.setRecognitionDescription("RecognitionDescription");
		Recognitions.setRecognitionAwardDate(thisDay);
		EducationalOrgReferenceType SchoolReference = new EducationalOrgReferenceType();
		Diploma.setSchoolReference(SchoolReference);
		EducationalOrgIdentityType EducationalOrgIdentity = new EducationalOrgIdentityType();
		SchoolReference.setEducationalOrgIdentity(EducationalOrgIdentity);
		EducationOrgIdentificationCode EducationOrgIdentificationCode = new EducationOrgIdentificationCode();
		EducationalOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(EducationOrgIdentificationCode);
		EducationOrgIdentificationCode.setID("ID");
		EducationOrgIdentificationCode.setIdentificationSystem( EducationOrgIdentificationSystemType.FEDERAL );
		return Diploma;
	}

	public GradebookEntry getGradeBookEntry() throws Exception
	{
		GradebookEntry  GradebookEntry = new GradebookEntry();
		GradebookEntry.setGradebookEntryType("GradebookEntryType");
		GradebookEntry.setDateAssigned(thisDay);
		GradebookEntry.setDescription("Description");
		LearningStandardReferenceType LearningStandardReference = new LearningStandardReferenceType();
		GradebookEntry.getLearningStandardReference().add(LearningStandardReference);
		LearningStandardIdentityType LearningStandardIdentity = new LearningStandardIdentityType();
		LearningStandardReference.setLearningStandardIdentity(LearningStandardIdentity);
		LearningStandardId LearningStandardId = new LearningStandardId();
		LearningStandardIdentity.setLearningStandardId(LearningStandardId);
		LearningStandardId.setIdentificationCode("IdentificationCode");
		LearningStandardId.setContentStandardName( "300" );
		LearningObjectiveReferenceType LearningObjectiveReference = new LearningObjectiveReferenceType();
		GradebookEntry.getLearningObjectiveReference().add(LearningObjectiveReference);
		LearningObjectiveIdentityType LearningObjectiveIdentity = new LearningObjectiveIdentityType();
		LearningObjectiveReference.setLearningObjectiveIdentity(LearningObjectiveIdentity);
		LearningStandardId LearningObjectiveId = new LearningStandardId();
		LearningObjectiveIdentity.getLearningObjectiveIdOrObjective().add(LearningObjectiveId);
		LearningObjectiveId.setIdentificationCode("IdentificationCode");
		LearningObjectiveId.setContentStandardName( "300" );
		LearningObjectiveIdentity.getLearningObjectiveIdOrObjective().add("1dentity");
		
		SectionReferenceType SectionReference = new SectionReferenceType();
		GradebookEntry.setSectionReference(SectionReference);
		SectionIdentityType SectionIdentity = new SectionIdentityType();
		SectionReference.setSectionIdentity(SectionIdentity);
		EducationOrgIdentificationCode EducationOrgIdentificationCode = new EducationOrgIdentificationCode();
		SectionIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(EducationOrgIdentificationCode);
		EducationOrgIdentificationCode.setID("ID");
		EducationOrgIdentificationCode.setIdentificationSystem( EducationOrgIdentificationSystemType.FEDERAL );
		SectionIdentity.setSchoolYear("SchoolYear");
		SectionIdentity.setTerm(TermType.FALL_SEMESTER);
		SectionIdentity.setClassPeriodName("ClassPeriodName");
		SectionIdentity.setLocation("Location");
		CourseCode CourseCode = new CourseCode();
		SectionIdentity.setCourseCode(CourseCode);
		CourseCode.setID("ID");
		CourseCode.setIdentificationSystem( CourseCodeSystemType.CSSC_COURSE_CODE );
		CourseCode.setAssigningOrganizationCode( "300" );
		SectionIdentity.setLocalCourseCode("LocalCourseCode");
		SectionIdentity.setUniqueSectionCode("UniqueSectionCode");
		GradingPeriodReferenceType GradingPeriodReference = new GradingPeriodReferenceType();
		GradebookEntry.setGradingPeriodReference(GradingPeriodReference);
		GradingPeriodIdentityType GradingPeriodIdentity = new GradingPeriodIdentityType();
		GradingPeriodReference.setGradingPeriodIdentity(GradingPeriodIdentity);

		EducationOrgIdentificationCode EducationOrgIdentificationCode2 = new EducationOrgIdentificationCode();
		GradingPeriodIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(EducationOrgIdentificationCode2);
		EducationOrgIdentificationCode2.setID("ID");
		EducationOrgIdentificationCode.setIdentificationSystem( EducationOrgIdentificationSystemType.FEDERAL );
		GradingPeriodIdentity.setGradingPeriod(GradingPeriodType.END_OF_YEAR);
		GradingPeriodIdentity.setSchoolYear("SchoolYear");
		return GradebookEntry;
	}

	public StudentGradebookEntry getStudentGradeBookEntry(StudentSectionAssociationReferenceType StudentSectionAssociationReference ) throws Exception
	{
		StudentGradebookEntry StudentGradebookEntry = new StudentGradebookEntry();
		StudentGradebookEntry.setDateFulfilled(thisDay);
		StudentGradebookEntry.setLetterGradeEarned("A");
		StudentGradebookEntry.setNumericGradeEarned(new BigInteger("1"));
		CompetencyLevelDescriptorType CompetencyLevel = new CompetencyLevelDescriptorType();
		StudentGradebookEntry.setCompetencyLevel(CompetencyLevel);
		//CompetencyLevel.getCodeValueOrDescription("")
		StudentGradebookEntry.setDiagnosticStatement("DiagnosticStatement");
		StudentGradebookEntry.setStudentSectionAssociationReference(StudentSectionAssociationReference);
		return StudentGradebookEntry;
	}

	public CompetencyLevelDescriptor getCompetancyLevelDescriptor()
	{
		CompetencyLevelDescriptor  CompetencyLevelDescriptor = new CompetencyLevelDescriptor();
		CompetencyLevelDescriptor.setCodeValue("CodeValue");
		CompetencyLevelDescriptor.setDescription("Description");
		CompetencyLevelDescriptor.setPerformanceBaseConversion(PerformanceBaseType.ADVANCED);
		return CompetencyLevelDescriptor;
	}

	
	public CourseTranscript getCourseTranscript(){
		CourseTranscript CourseTranscript = new CourseTranscript();
		CourseTranscript.setCourseAttemptResult(CourseAttemptResultType.PASS);
		Credits CreditsAttempted = new Credits();
		CourseTranscript.setCreditsAttempted(CreditsAttempted);
		CreditsAttempted.setCredit(new BigDecimal(300));
		CreditsAttempted.setCreditType( CreditType.CARNEGIE_UNIT);
		CreditsAttempted.setCreditConversion( new BigDecimal(1) );
		Credits CreditsEarned = new Credits();
		CourseTranscript.setCreditsEarned(CreditsEarned);
		CreditsEarned.setCredit(new BigDecimal(100));
		CreditsEarned.setCreditType( CreditType.CARNEGIE_UNIT );
		CreditsEarned.setCreditConversion( new BigDecimal(5) );
		AdditionalCredits AdditionalCreditsEarned = new AdditionalCredits();
		CourseTranscript.getAdditionalCreditsEarned().add(AdditionalCreditsEarned);
		AdditionalCreditsEarned.setCredit(new BigDecimal(100));
		AdditionalCreditsEarned.setAdditionalCreditType(AdditionalCreditType.AP);
		CourseTranscript.setGradeLevelWhenTaken(GradeLevelType.NINTH_GRADE);
		CourseTranscript.setMethodCreditEarned(MethodCreditEarnedType.ADULT_EDUCATION_CREDIT);
		CourseTranscript.setFinalLetterGradeEarned("100");
		CourseTranscript.setCourseRepeatCode(CourseRepeatCodeType.REPEAT_COUNTED);
		CourseReferenceType CourseReference = new CourseReferenceType();
		CourseTranscript.setCourseReference(CourseReference);
		CourseIdentityType CourseIdentity = new CourseIdentityType();
		CourseReference.setCourseIdentity(CourseIdentity);
		CourseCode CourseCode = new CourseCode();
		CourseIdentity.getCourseCode().add(CourseCode);
		CourseCode.setID("ID");
		CourseCode.setIdentificationSystem( CourseCodeSystemType.CSSC_COURSE_CODE );
		CourseCode.setAssigningOrganizationCode( "300" );

		EducationalOrgReferenceType EducationOrganizationReference = new EducationalOrgReferenceType();
		CourseTranscript.getEducationOrganizationReference().add(EducationOrganizationReference);
		EducationalOrgIdentityType EducationalOrgIdentity = new EducationalOrgIdentityType();
		EducationOrganizationReference.setEducationalOrgIdentity(EducationalOrgIdentity);

		EducationOrgIdentificationCode EducationOrgIdentificationCode = new EducationOrgIdentificationCode();
		EducationalOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(EducationOrgIdentificationCode);
		EducationOrgIdentificationCode.setID("ID V1");
		EducationOrgIdentificationCode.setIdentificationSystem(  EducationOrgIdentificationSystemType.FEDERAL);
		ReferenceType StudentAcademicRecordReference = new ReferenceType();
		CourseTranscript.setStudentAcademicRecordReference(StudentAcademicRecordReference);
		return CourseTranscript;
	}
	
	public static void main(String [] args) throws Exception
	{
		EducationAgencyGenerator edOrgGenerator          = new EducationAgencyGenerator();
		StateEducationAgency sea                         = edOrgGenerator.getSEA("New-York-Ed-Org");
		EducationalOrgReferenceType seaRef               = edOrgGenerator.getEducationalOrgReferenceType(sea);
		LocalEducationAgency lea                         = edOrgGenerator.getLEA("Manhattan-Ed-Org");
		EducationalOrgReferenceType leaRef               = edOrgGenerator.getEducationalOrgReferenceType(lea);
		lea.setStateEducationAgencyReference(seaRef);
	    SchoolGenerator	 schoolGenerator                 = new SchoolGenerator(StateAbbreviationType.NY);
	    School school                                    = schoolGenerator.getSchool("School-Id");
		EducationalOrgReferenceType schoolRef            = edOrgGenerator.getEducationalOrgReferenceType(school);
		
		CourseGenerator courseGenerator                  = new CourseGenerator(GradeLevelType.EIGHTH_GRADE);
		int COURSE_COUNT                                 = 1;
		Course [] courses                                = new Course[COURSE_COUNT];
		CourseReferenceType [] courseRefs                = new CourseReferenceType[COURSE_COUNT];
		for(int i = 0; i < COURSE_COUNT; i++){
			courses[i]                                   = courseGenerator.getCourse("Course" + i);
			courses[i].setEducationOrganizationReference(schoolRef);
			courseRefs[i]                                = courseGenerator.getCourseReferenceType(courses[i]);
	    }
		
		SessionGenerator sessionGenerator                = new SessionGenerator();
		int SESSION_COUNT                                = 1;
		Session [] sessions                              = new Session[SESSION_COUNT];
		SessionReferenceType [] sessionRefs              = new SessionReferenceType[SESSION_COUNT];
		
		SectionGenerator sectionGenerator                = new SectionGenerator();
		int SECTION_COUNT                                = 1;
		Section [] sections                              = new Section[SECTION_COUNT];
		SectionReferenceType [] sectionRefs              = new SectionReferenceType[SECTION_COUNT];
		
		List<String> stateId = new ArrayList<String>();
		stateId.add("New-York-Ed-Org");
		for(int i = 0; i < SESSION_COUNT; i++){
			sessions[i]                                  = sessionGenerator.sessionGenerator(stateId);
			sessionRefs[i]                               = sessionGenerator.getSessionReferenceType();
		}
		
		for(int i = 0; i < SECTION_COUNT; i++){
			sections[i]                                  = sectionGenerator.generate("sectionCode " + i, 1, "School-Id");
			sectionRefs[i]                               = sectionGenerator.getSectionReference(sections[i]);
		}
		
		LearningObjectiveGenerator learningObGenerator   = new LearningObjectiveGenerator();
		int LEARNING_OBJECTIVE_COUNT                     = 2;
		LearningObjective [] learningObjectives          = new LearningObjective[LEARNING_OBJECTIVE_COUNT];
		LearningObjectiveReferenceType [] learningObjectiveRefs = new LearningObjectiveReferenceType[LEARNING_OBJECTIVE_COUNT];
		for(int i = 0; i < LEARNING_OBJECTIVE_COUNT ; i++){
			learningObjectives[i]                        = learningObGenerator.getLearningObjective("LOID" + i);
		    learningObjectiveRefs[i]                     = learningObGenerator.getLearningObjectiveReferenceType(learningObjectives[i]);	
		}
	}

}
