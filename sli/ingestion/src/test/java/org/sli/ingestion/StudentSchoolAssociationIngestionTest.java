package org.sli.ingestion;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slc.sli.repository.SchoolRepository;
import org.slc.sli.repository.StudentRepository;
import org.slc.sli.repository.StudentSchoolAssociationRepository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

public class StudentSchoolAssociationIngestionTest extends IngestionTest {

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private SchoolRepository schoolRepository;
	
	@Autowired
	private StudentSchoolAssociationRepository studentSchoolAssociationRepository;
	
	@Test
	public void testStudentSchoolAssociationInterchangeXmlParsing() throws IOException, SAXException {

		int numberOfStudentSchoolAssociations = 2;

		this.createStudents(numberOfStudentSchoolAssociations);
		this.createSchools(numberOfStudentSchoolAssociations);
		
		studentSchoolAssociationRepository.deleteAll();
		
		String xmlRecords = this.createStudentSchoolAssociationInterchangeXml(numberOfStudentSchoolAssociations);
		
		File xmlRecordsFile = this.createTestFile(xmlRecords);
		
		File ingestionEdFiXmlProcessorOutputFile = this.createTempFile();

		this.getEdFiXmlProcessor().processIngestionStream(xmlRecordsFile, ingestionEdFiXmlProcessorOutputFile);
		
		File ingestionPersistenceProcessorOutputFile = this.createTempFile();

		this.getPersistenceProcessor().processIngestionStream(ingestionEdFiXmlProcessorOutputFile, ingestionPersistenceProcessorOutputFile);

	}
	
	protected void createStudents(int numberOfStudents) throws IOException, SAXException {

		studentRepository.deleteAll();
		
		StudentIngestionTest studentIngestionTest = new StudentIngestionTest();
		
		List neutralRecords = studentIngestionTest.createStudentIngestionNeutralRecords(this.getPersistenceProcessor(), numberOfStudents);

		File neutralRecordsFile = this.createNeutralRecordsFile(neutralRecords);

		File ingestionPersistenceProcessorOutputFile = this.createTempFile();

		this.getPersistenceProcessor().processIngestionStream(neutralRecordsFile, ingestionPersistenceProcessorOutputFile);

	}

	protected void createSchools(int numberOfSchools) throws IOException, SAXException {

		schoolRepository.deleteAll();

		SchoolIngestionTest schoolIngestionTest = new SchoolIngestionTest();
		
		List neutralRecords = schoolIngestionTest.createSchoolIngestionNeutralRecords(this.getPersistenceProcessor(), numberOfSchools);
		
		File neutralRecordsFile = this.createNeutralRecordsFile(neutralRecords);

		File ingestionPersistenceProcessorOutputFile = this.createTempFile();

		this.getPersistenceProcessor().processIngestionStream(neutralRecordsFile, ingestionPersistenceProcessorOutputFile);

	}

	protected String createStudentSchoolAssociationInterchangeXml(int numberOfAssociations) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(this.createStudentSchoolAssociationInterchangeXmlHeader());
		
		for(int index = 1; index <= numberOfAssociations; index++) {
			StudentSchoolAssociationInterchange studentSchoolAssociation = this.createStudentSchoolAssociation(index);
			builder.append(this.createStudentSchoolAssociationXml(studentSchoolAssociation));
		}
		builder.append(this.createStudentSchoolAssociationInterchangeXmlFooter());
		
		return builder.toString();
	}
	
	protected String createStudentSchoolAssociationInterchangeXmlHeader() {
		
		String interchangeXmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n";
		interchangeXmlHeader += "<InterchangeStudentEnrollment xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentEnrollment.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">" + "\n";

		return interchangeXmlHeader;
	}
	
	protected String createStudentSchoolAssociationInterchangeXmlFooter() {
		
		String interchangeXmlFooter = "</InterchangeStudentEnrollment>" + "\n";

		return interchangeXmlFooter;
	}
	
	protected String createStudentSchoolAssociationXml(StudentSchoolAssociationInterchange studentSchoolAssociation) {
		String studentSchoolAssociationXml = "";
		
		studentSchoolAssociationXml += "<StudentSchoolAssociation>" + "\n";

		studentSchoolAssociationXml += "<StudentReference><StudentIdentity>" + "\n";
		studentSchoolAssociationXml += "<StudentUniqueStateId>";
		
		studentSchoolAssociationXml += studentSchoolAssociation.getStudentUniqueStateId();
		
		studentSchoolAssociationXml += "</StudentUniqueStateId>" + "\n";
		studentSchoolAssociationXml += "</StudentIdentity></StudentReference>" + "\n";

		studentSchoolAssociationXml += "<SchoolReference><EducationalOrgIdentity>" + "\n";
		studentSchoolAssociationXml += "<StateOrganizationId>";
		
		studentSchoolAssociationXml += studentSchoolAssociation.getStateOrganizationId();
		
		studentSchoolAssociationXml += "</StateOrganizationId>" + "\n";
		studentSchoolAssociationXml += "</EducationalOrgIdentity></SchoolReference>" + "\n";

		studentSchoolAssociationXml += "</StudentSchoolAssociation>" + "\n";

		return studentSchoolAssociationXml;
	}

	protected StudentSchoolAssociationInterchange createStudentSchoolAssociation(int associationId) {
		StudentSchoolAssociationInterchange studentSchoolAssociation =  new StudentSchoolAssociationInterchange();
		
		studentSchoolAssociation.setAssociationId(associationId);
		studentSchoolAssociation.setStudentUniqueStateId("" + associationId);
		studentSchoolAssociation.setStateOrganizationId("" + associationId);
		
		return studentSchoolAssociation;
	}
	
}
