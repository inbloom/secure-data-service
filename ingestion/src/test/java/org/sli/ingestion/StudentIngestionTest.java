package org.sli.ingestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slc.sli.domain.Student;
import org.slc.sli.domain.enums.SexType;
import org.slc.sli.repository.StudentRepository;

import org.junit.Test;
import org.sli.ingestion.processors.IngestionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.xml.sax.SAXException;

public class StudentIngestionTest extends IngestionTest {

	@Autowired
	private StudentRepository studentRepository;
		
	@Test
	public void testStudentIngestionPersistence() throws IOException, SAXException {

		studentRepository.deleteAll();
		
		int numberOfStudents = 2;
		List neutralRecords = this.createStudentIngestionNeutralRecords(this.getPersistenceProcessor(), numberOfStudents);
		
		File neutralRecordsFile = this.createNeutralRecordsFile(neutralRecords);

		File ingestionPersistenceProcessorOutputFile = this.createTempFile();

		this.getPersistenceProcessor().processIngestionStream(neutralRecordsFile, ingestionPersistenceProcessorOutputFile);

		this.verifyStudents(studentRepository, numberOfStudents);
		
	}
	
	@Test
	public void testStudentInterchangeXmlParsing() throws IOException, SAXException {

		studentRepository.deleteAll();
		
		int numberOfStudents = 2;
		String xmlRecords = this.createStudentInterchangeXml(numberOfStudents);
		
		File xmlRecordsFile = this.createTestFile(xmlRecords);
		
		File ingestionEdFiXmlProcessorOutputFile = this.createTempFile();

		this.getEdFiXmlProcessor().processIngestionStream(xmlRecordsFile, ingestionEdFiXmlProcessorOutputFile);
		
		File ingestionPersistenceProcessorOutputFile = this.createTempFile();

		this.getPersistenceProcessor().processIngestionStream(ingestionEdFiXmlProcessorOutputFile, ingestionPersistenceProcessorOutputFile);

		this.verifyStudents(studentRepository, 0);
		
	}
	
	protected String createStudentInterchangeXml(int numberOfStudents) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(this.createStudentInterchangeXmlHeader());
		
		for(int index = 1; index <= numberOfStudents; index++) {
			Student student = this.createStudent(index);
			builder.append(this.createStudentXml(student));
		}
		builder.append(this.createStudentInterchangeXmlFooter());
		
		return builder.toString();
	}
	
	protected List createStudentIngestionNeutralRecords(IngestionProcessor ingestionProcessor, int numberOfStudents) {
		List list = new ArrayList();
		
		for(int index = 1; index <= numberOfStudents; index++) {
			Student student = this.createStudent(index);
			
			list.add(ingestionProcessor.mapToNeutralRecord(student));
		}
		
		return list;
	}
	
	protected String createStudentIngestionJson(IngestionProcessor ingestionProcessor, int numberOfStudents) throws IOException, SAXException {
		StringBuilder builder = new StringBuilder();
		
		for(int index = 1; index <= numberOfStudents; index++) {
			Student student = this.createStudent(index);
			builder.append(ingestionProcessor.mapToJson(student, "create"));
			builder.append(System.getProperty("line.separator"));
		}
		
		return builder.toString();
	}
	
	protected String createStudentInterchangeXmlHeader() {
		
		String interchangeXmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n";
		interchangeXmlHeader += "<InterchangeStudent xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-Student.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">" + "\n";

		return interchangeXmlHeader;
	}
	
	protected String createStudentInterchangeXmlFooter() {
		
		String interchangeXmlFooter = "</InterchangeStudent>" + "\n";

		return interchangeXmlFooter;
	}
	
	protected String createStudentXml(Student student) {
		String studentXml = "";
		
		Date birthDate = student.getBirthDate();
		SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd");
		String birthDateFormat = dateFormat.format(birthDate);
		
		studentXml += "<Student>" + "\n";
		
		// Test Version Only - allow specification of Student ID 
		studentXml += "<StudentId>" + student.getStudentId() + "</StudentId>" + "\n";
		
		studentXml += "<StudentUniqueStateId>" + student.getStudentSchoolId() + "</StudentUniqueStateId>" + "\n";
		studentXml += "<Name><FirstName>" + student.getFirstName() + "</FirstName><MiddleName>" + student.getMiddleName() + "</MiddleName><LastSurname>" + student.getLastSurname() + "</LastSurname></Name>" + "\n";
		studentXml += "<Sex>" + student.getSex() + "</Sex>" + "\n";
		studentXml += "<BirthData><BirthDate>" + birthDateFormat + "</BirthDate></BirthData>" + "\n";
		studentXml += "</Student>" + "\n";

		return studentXml;
	}

	protected Student createStudent(int studentId) {
		Student student =  new Student();
		
		student.setStudentId(studentId);
		student.setStudentSchoolId("" + studentId);
		student.setFirstName("firstName" + "_" + studentId);
		student.setMiddleName("middleName" + "_" + studentId);
		student.setLastSurname("lastSurname" + "_" + studentId);
		
		SexType sex = SexType.Male;
		if ((studentId % 2) == 0) {
			sex = SexType.Female;
		}
		
		student.setSex(sex);
		
		String testDate = this.calculateTestDate(studentId);
		
		student.setBirthDate(java.sql.Date.valueOf( testDate ));
		
		return student;
	}
	
	protected void verifyStudents(PagingAndSortingRepository repository, long numberOfStudents) {
		
		long repositorySize = repository.count();
		
		if (numberOfStudents > 0) {
			assertEquals(repositorySize, numberOfStudents);
		}
		
		for(int index = 1; index <= repositorySize; index++) {			
			Student student = (Student)repository.findOne(index);
			this.verifyStudent(index, student);
		}
		
	}
	
	protected void verifyStudent(int studentId, Student student) {
		
		assertNotNull(student);
		assertEquals("" + studentId, student.getStudentSchoolId());
		assertEquals("firstName" + "_" + studentId, student.getFirstName());
		assertEquals("middleName" + "_" + studentId, student.getMiddleName());
		assertEquals("lastSurname" + "_" + studentId, student.getLastSurname());
		
		SexType sex = SexType.Male;
		if ((studentId % 2) == 0) {
			sex = SexType.Female;
		}
		
		assertEquals(sex, student.getSex());

	}
	
}
