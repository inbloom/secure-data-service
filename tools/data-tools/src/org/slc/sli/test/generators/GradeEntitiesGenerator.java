package org.slc.sli.test.generators;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.xml.bind.JAXBElement;

import org.slc.sli.test.edfi.entities.CompetencyLevelDescriptor;
import org.slc.sli.test.edfi.entities.CompetencyLevelDescriptorType;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationCode;
import org.slc.sli.test.edfi.entities.GradebookEntry;
import org.slc.sli.test.edfi.entities.GradingPeriod;
import org.slc.sli.test.edfi.entities.GradingPeriodIdentityType;
import org.slc.sli.test.edfi.entities.GradingPeriodReferenceType;
import org.slc.sli.test.edfi.entities.GradingPeriodType;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.SectionReferenceType;
import org.slc.sli.test.edfi.entities.StudentGradebookEntry;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.StudentSectionAssociationIdentityType;
import org.slc.sli.test.edfi.entities.StudentSectionAssociationReferenceType;

public class GradeEntitiesGenerator {
	
	private DateFormat dateFormatter = new SimpleDateFormat("YYYYmmDD");
 	private String thisDay         = dateFormatter.format(new Date());
 	private String oneYearAgo      = dateFormatter.format(new Date(new Date().getTime() - 365*24*60*60*1000));
 	private Random rand            = new Random();
 	private int idCount            = 0; 
 	private ObjectFactory factory  = new ObjectFactory();

	public GradebookEntry getGradeBooEntry(GradingPeriodReferenceType gradingPeriodRef, SectionReferenceType sectionRef)
	{
		idCount++;
		GradebookEntry gbe = new GradebookEntry();
		gbe.setDateAssigned(thisDay);
		gbe.setDescription("Grade Book Entry Description " + idCount);
		gbe.setGradebookEntryType("Grade Book Entry Type " + idCount);
		gbe.setGradingPeriodReference(gradingPeriodRef);
		gbe.setSectionReference(sectionRef);
		return gbe;
	}
	
	public GradingPeriod getGradingPeriod()
	{
		GradingPeriod period = new GradingPeriod();
		period.setBeginDate(oneYearAgo);
		period.setEndDate(thisDay);
		period.setGradingPeriod(GradingPeriodType.END_OF_YEAR);
		period.setTotalInstructionalDays(92);
		return period;
	}
	
	public GradingPeriodReferenceType getGradingPeriodReferenceType(GradingPeriod period, EducationOrgIdentificationCode edOrg, String stateId)
	{
		GradingPeriodReferenceType ref = new GradingPeriodReferenceType();
		GradingPeriodIdentityType identity = new GradingPeriodIdentityType();
		ref.setGradingPeriodIdentity(identity);
		identity.setGradingPeriod(period.getGradingPeriod());
		identity.setSchoolYear(period.getBeginDate() + "-" + period.getEndDate());
		if(edOrg != null) identity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(edOrg);
		if(stateId != null) identity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(stateId);
		return ref;
	}
	
	public StudentGradebookEntry getStudentGradebookEntry(SectionReferenceType section, StudentReferenceType student)
	{
		StudentGradebookEntry sgbe = new StudentGradebookEntry();
		sgbe.setDateFulfilled(thisDay);
		sgbe.setLetterGradeEarned("A B C D E F".split(" ")[rand.nextInt(6)]);
		sgbe.setNumericGradeEarned(BigInteger.ONE);
		
		
		CompetencyLevelDescriptor oid = factory.createCompetencyLevelDescriptor();
		
		CompetencyLevelDescriptorType cld = new CompetencyLevelDescriptorType();
		//cld.getCodeValueOrDescription().add(e)
		sgbe.setCompetencyLevel(cld);
		sgbe.setDiagnosticStatement("Diagnostic Statement");
		
		StudentSectionAssociationReferenceType ssRef = new StudentSectionAssociationReferenceType();
		StudentSectionAssociationIdentityType ssIdentity = new StudentSectionAssociationIdentityType();
		ssIdentity.setSectionIdentity(section.getSectionIdentity());
		ssIdentity.setStudentIdentity(student.getStudentIdentity());
		
		//sgbe.setStudentSectionAssociationReference(StudentSectionAssociationReferenceType value);
		return null;

	}
}
