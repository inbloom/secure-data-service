package org.slc.sli.ingestion;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.xml.sax.SAXException;

import org.slc.sli.repository.SchoolRepository;
import org.slc.sli.repository.StudentRepository;
import org.slc.sli.repository.StudentSchoolAssociationRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ 
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })

public class StudentSchoolAssociationIngestionTest {

    @Autowired
    private EdFiProcessor edFiProcessor;

    @Autowired
    private PersistenceProcessor persistenceProcessor;

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
		
		String xmlRecords = createStudentSchoolAssociationInterchangeXml(numberOfStudentSchoolAssociations);
		
		File xmlRecordsFile = IngestionTest.createTestFile(xmlRecords);
		
		File ingestionEdFiProcessorOutputFile = IngestionTest.createTempFile();

		edFiProcessor.processIngestionStream(xmlRecordsFile, ingestionEdFiProcessorOutputFile);
		
		File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();

		persistenceProcessor.processIngestionStream(ingestionEdFiProcessorOutputFile, ingestionPersistenceProcessorOutputFile);

	}
	
	private void createStudents(int numberOfStudents) throws IOException, SAXException {

		studentRepository.deleteAll();
		
		StudentIngestionTest studentIngestionTest = new StudentIngestionTest();
		
		List neutralRecords = StudentIngestionTest.createStudentIngestionNeutralRecords(persistenceProcessor, numberOfStudents);

		File neutralRecordsFile = IngestionTest.createNeutralRecordsFile(neutralRecords);

		File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();

		persistenceProcessor.processIngestionStream(neutralRecordsFile, ingestionPersistenceProcessorOutputFile);

	}

	private void createSchools(int numberOfSchools) throws IOException, SAXException {

		schoolRepository.deleteAll();

		SchoolIngestionTest schoolIngestionTest = new SchoolIngestionTest();
		
		List neutralRecords = SchoolIngestionTest.createSchoolIngestionNeutralRecords(persistenceProcessor, numberOfSchools);
		
		File neutralRecordsFile = IngestionTest.createNeutralRecordsFile(neutralRecords);

		File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();

		persistenceProcessor.processIngestionStream(neutralRecordsFile, ingestionPersistenceProcessorOutputFile);

	}

	public static String createStudentSchoolAssociationInterchangeXml(int numberOfAssociations) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(createStudentSchoolAssociationInterchangeXmlHeader());
		
		for(int index = 1; index <= numberOfAssociations; index++) {
			StudentSchoolAssociationInterchange studentSchoolAssociation = createStudentSchoolAssociation(index);
			builder.append(createStudentSchoolAssociationXml(studentSchoolAssociation));
		}
		builder.append(createStudentSchoolAssociationInterchangeXmlFooter());
		
		return builder.toString();
	}
	
	public static String createStudentSchoolAssociationInterchangeXmlHeader() {
		
		String interchangeXmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n";
		interchangeXmlHeader += "<InterchangeStudentEnrollment xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentEnrollment.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">" + "\n";

		return interchangeXmlHeader;
	}
	
	public static String createStudentSchoolAssociationInterchangeXmlFooter() {
		
		String interchangeXmlFooter = "</InterchangeStudentEnrollment>" + "\n";

		return interchangeXmlFooter;
	}
	
	public static String createStudentSchoolAssociationXml(StudentSchoolAssociationInterchange studentSchoolAssociation) {
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

	public static StudentSchoolAssociationInterchange createStudentSchoolAssociation(int associationId) {
		StudentSchoolAssociationInterchange studentSchoolAssociation =  new StudentSchoolAssociationInterchange();
		
		studentSchoolAssociation.setAssociationId(associationId);
		studentSchoolAssociation.setStudentUniqueStateId("" + associationId);
		studentSchoolAssociation.setStateOrganizationId("" + associationId);
		
		return studentSchoolAssociation;
	}
	
}
