package org.sli.ingestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slc.sli.domain.School;
import org.slc.sli.repository.SchoolRepository;

import org.junit.Test;
import org.sli.ingestion.processors.IngestionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.xml.sax.SAXException;

public class SchoolIngestionTest extends IngestionTest {

	@Autowired
	private SchoolRepository schoolRepository;
	
	@Test
	public void testSchoolIngestionPersistence() throws IOException, SAXException {

		schoolRepository.deleteAll();

		int numberOfSchools = 2;
		List neutralRecords = this.createSchoolIngestionNeutralRecords(this.getPersistenceProcessor(), numberOfSchools);
		
		File neutralRecordsFile = this.createNeutralRecordsFile(neutralRecords);

		File ingestionPersistenceProcessorOutputFile = this.createTempFile();

		this.getPersistenceProcessor().processIngestionStream(neutralRecordsFile, ingestionPersistenceProcessorOutputFile);

		this.verifySchools(schoolRepository, numberOfSchools);
		
	}

	@Test
	public void testSchoolInterchangeXmlParsing() throws IOException, SAXException {

		schoolRepository.deleteAll();
		
		int numberOfSchools = 2;
		String xmlRecords = this.createSchoolInterchangeXml(numberOfSchools);

		File xmlRecordsFile = this.createTestFile(xmlRecords);

		File ingestionEdFiXmlProcessorOutputFile = this.createTempFile();

		this.getEdFiXmlProcessor().processIngestionStream(xmlRecordsFile, ingestionEdFiXmlProcessorOutputFile);
		
		File ingestionPersistenceProcessorOutputFile = this.createTempFile();

		this.getPersistenceProcessor().processIngestionStream(ingestionEdFiXmlProcessorOutputFile, ingestionPersistenceProcessorOutputFile);

		this.verifySchools(schoolRepository, 0);
		
	}
	
	protected String createSchoolInterchangeXml(int numberOfSchools) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(this.createSchoolInterchangeXmlHeader());
		
		for(int index = 1; index <= numberOfSchools; index++) {
			School school = this.createSchool(index);
			builder.append(this.createSchoolXml(school));
		}
		builder.append(this.createSchoolInterchangeXmlFooter());
		
		return builder.toString();
	}
	
	protected String createSchoolIngestionJson(IngestionProcessor ingestionProcessor, int numberOfSchools) throws IOException, SAXException {
		StringBuilder builder = new StringBuilder();
		
		for(int index = 1; index <= numberOfSchools; index++) {
			School school = this.createSchool(index);
			builder.append(ingestionProcessor.mapToJson(school, "create"));
			builder.append(System.getProperty("line.separator"));
		}
		
		return builder.toString();
	}
	
	protected List createSchoolIngestionNeutralRecords(IngestionProcessor ingestionProcessor, int numberOfSchools) {
		List list = new ArrayList();
		
		for(int index = 1; index <= numberOfSchools; index++) {
			School school = this.createSchool(index);
			
			list.add(ingestionProcessor.mapToNeutralRecord(school));
		}
		
		return list;
	}
	
	protected String createSchoolInterchangeXmlHeader() {
		
		String interchangeXmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n";
		interchangeXmlHeader += "<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">" + "\n";

		return interchangeXmlHeader;
	}
	
	protected String createSchoolInterchangeXmlFooter() {
		
		String interchangeXmlFooter = "</InterchangeEducationOrganization>" + "\n";

		return interchangeXmlFooter;
	}
	
	protected String createSchoolXml(School school) {
		String schoolXml = "";
		
		schoolXml += "<School>" + "\n";
		
		// Test Version Only - allow specification of Student ID 
		schoolXml += "<SchoolId>" + school.getSchoolId() + "</SchoolId>" + "\n";
		
		schoolXml += "<StateOrganizationId>" + school.getStateOrganizationId() + "</StateOrganizationId>" + "\n";
		schoolXml += "<NameOfInstitution>" + school.getFullName() + "</NameOfInstitution>" + "\n";
		schoolXml += "</School>" + "\n";

		return schoolXml;
	}

	protected School createSchool(int schoolId) {
		School school =  new School();
		
		school.setSchoolId(schoolId);
		school.setStateOrganizationId("" + schoolId);
		school.setFullName("nameOfInstitution" + "_" + schoolId);
		
		return school;
	}
	
	protected void verifySchools(PagingAndSortingRepository repository, long numberOfSchools) {
		
		long repositorySize = repository.count();
		
		if (numberOfSchools > 0) {
			assertEquals(repositorySize, numberOfSchools);
		}
		
		for(int index = 1; index <= repositorySize; index++) {			
			School school = (School)repository.findOne(index);
			this.verifySchool(index, school);
		}
		
	}
	
	protected void verifySchool(int schoolId, School school) {
		
		assertNotNull(school);
		assertEquals("" + schoolId, school.getStateOrganizationId());
		assertEquals("nameOfInstitution" + "_" + schoolId, school.getFullName());

	}
	
}
