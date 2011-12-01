package org.slc.sli.ingestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.IngestionProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.xml.sax.SAXException;

import org.slc.sli.domain.Student;
import org.slc.sli.domain.enums.SexType;
import org.slc.sli.repository.StudentRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ 
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })

public class StudentIngestionTest {

    @Autowired
    private EdFiProcessor edFiProcessor;

    @Autowired
    private PersistenceProcessor persistenceProcessor;

	@Autowired
	private StudentRepository studentRepository;
		
	@Test
	public void testStudentIngestionPersistence() throws IOException, SAXException {

		studentRepository.deleteAll();
		
		int numberOfStudents = 2;
		List neutralRecords = createStudentIngestionNeutralRecords(persistenceProcessor, numberOfStudents);
		
		File neutralRecordsFile = IngestionTest.createNeutralRecordsFile(neutralRecords);

		File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();

		persistenceProcessor.processIngestionStream(neutralRecordsFile, ingestionPersistenceProcessorOutputFile);

		verifyStudents(studentRepository, numberOfStudents);
		
	}
	
	@Test
	public void testStudentInterchangeXmlParsing() throws IOException, SAXException {

		studentRepository.deleteAll();
		
		int numberOfStudents = 2;
		String xmlRecords = createStudentInterchangeXml(numberOfStudents);
		
		File xmlRecordsFile =IngestionTest. createTestFile(IngestionTest.INGESTION_FILE_PREFIX, IngestionTest.INGESTION_XML_FILE_SUFFIX, xmlRecords);
		
		File ingestionEdFiProcessorOutputFile = IngestionTest.createTempFile();

		edFiProcessor.processIngestionStream(xmlRecordsFile, ingestionEdFiProcessorOutputFile);
		
		File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();

		persistenceProcessor.processIngestionStream(ingestionEdFiProcessorOutputFile, ingestionPersistenceProcessorOutputFile);

		verifyStudents(studentRepository, 0);
		
	}
	
	@Test
	public void testStudentInterchangeCsvParsing() throws IOException, SAXException {

        studentRepository.deleteAll();
          
        int numberOfStudents = 2;
        String csvRecords = createStudentInterchangeCsv(numberOfStudents);
          
        File csvRecordsFile = IngestionTest.createTestFile(IngestionTest.INGESTION_FILE_PREFIX, IngestionTest.INGESTION_CSV_FILE_SUFFIX, csvRecords);
          
        File ingestionEdFiProcessorOutputFile = IngestionTest.createTempFile();
        
        edFiProcessor.processIngestionStream(csvRecordsFile, ingestionEdFiProcessorOutputFile);
          
        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();
        
        persistenceProcessor.processIngestionStream(ingestionEdFiProcessorOutputFile, ingestionPersistenceProcessorOutputFile);
        
        verifyStudents(studentRepository, 0);
      
	}
   
    @Test
    public void testStudentInterchangeCsvFileParsing() throws IOException, SAXException {

        studentRepository.deleteAll();
          
        File csvRecordsFile = IngestionTest.getFile("smooks/InterchangeStudent.csv");
          
        File ingestionEdFiProcessorOutputFile = IngestionTest.createTempFile();
        
        edFiProcessor.processIngestionStream(csvRecordsFile, ingestionEdFiProcessorOutputFile);
          
        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();
        
        persistenceProcessor.processIngestionStream(ingestionEdFiProcessorOutputFile, ingestionPersistenceProcessorOutputFile);
        
        assertEquals(100, studentRepository.count());
     
    }
   
	
	// Static Methods
	public static String createStudentInterchangeXml(int numberOfStudents) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(createStudentInterchangeXmlHeader());
		
		for(int index = 1; index <= numberOfStudents; index++) {
			Student student = createStudent(index);
			builder.append(createStudentXml(student));
		}
		builder.append(createStudentInterchangeXmlFooter());
		
		return builder.toString();
	}
	
	public static String createStudentInterchangeCsv(int numberOfStudents) {
        StringBuilder builder = new StringBuilder();
          
        for(int index = 1; index <= numberOfStudents; index++) {
            Student student = createStudent(index);
            builder.append(createStudentCsv(student));
            builder.append(System.getProperty("line.separator"));
        }
          
        return builder.toString();
	}
   
	public static List createStudentIngestionNeutralRecords(IngestionProcessor ingestionProcessor, int numberOfStudents) {
		List list = new ArrayList();
		
		for(int index = 1; index <= numberOfStudents; index++) {
			Student student = createStudent(index);
			
			list.add(Translator.mapToNeutralRecord(student));
		}
		
		return list;
	}
	
	public static String createStudentIngestionJson(IngestionProcessor ingestionProcessor, int numberOfStudents) throws IOException, SAXException {
		StringBuilder builder = new StringBuilder();
		
		for(int index = 1; index <= numberOfStudents; index++) {
			Student student = createStudent(index);
			builder.append(Translator.mapToJson(student, "create"));
			builder.append(System.getProperty("line.separator"));
		}
		
		return builder.toString();
	}
	
	public static String createStudentInterchangeXmlHeader() {
		
		String interchangeXmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n";
		interchangeXmlHeader += "<InterchangeStudent xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-Student.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">" + "\n";

		return interchangeXmlHeader;
	}
	
	public static String createStudentInterchangeXmlFooter() {
		
		String interchangeXmlFooter = "</InterchangeStudent>" + "\n";

		return interchangeXmlFooter;
	}
	
	public static String createStudentXml(Student student) {
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

	public static String createStudentCsv(Student student) {
        String studentCsv = "";
          
        Date birthDate = student.getBirthDate();
        SimpleDateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd");
        String birthDateFormat = dateFormat.format(birthDate);
          
        // Test Version Only - allow specification of Student ID 
        studentCsv += student.getStudentId() + ",";
          
        studentCsv += student.getStudentSchoolId() + ",";
          
        // Skip 5 fields for now
        studentCsv += ",,,,,";
        
        studentCsv += student.getFirstName() + ",";
        studentCsv += student.getMiddleName() + ",";
        studentCsv += student.getLastSurname() + ",";
        
        // Skip 8 fields for now
        studentCsv += ",,,,,,,,";
        
        studentCsv += student.getSex() + ",";
        studentCsv += birthDateFormat;
          
        return studentCsv;
	}

	public static Student createStudent(int studentId) {
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
		
		String testDate = calculateTestDate(studentId);
		
		student.setBirthDate(java.sql.Date.valueOf( testDate ));
		
		return student;
	}
	
	public static void verifyStudents(PagingAndSortingRepository repository, long numberOfStudents) {
		
		long repositorySize = repository.count();
		
		if (numberOfStudents > 0) {
			assertEquals(repositorySize, numberOfStudents);
		}
		
		Assert.assertTrue((repositorySize > 0));
		
		for(int index = 1; index <= repositorySize; index++) {			
			Student student = (Student)repository.findOne(index);
			verifyStudent(index, student);
		}
		
	}
	
	public static void verifyStudent(int studentId, Student student) {
		
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
	
    public static String calculateTestDate(int studentId) {
        String testDate = "";
        
        int yearId = studentId % 10000;
        testDate = "" + yearId;
        if (yearId < 10) testDate = "000" + testDate;
        else if (yearId < 100) testDate = "00" + testDate;
        else if (yearId < 1000) testDate = "0" + testDate;
        
        testDate = testDate + "-01-01";
        return testDate;
    }
    
}
