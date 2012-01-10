package org.slc.sli.ingestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.xml.sax.SAXException;

import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.util.MD5;

/**
 * Tests for Student entity
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class StudentIngestionTest {

    @Autowired
    private EdFiProcessor edFiProcessor;

    @Autowired
    private PersistenceProcessor persistenceProcessor;

    @Autowired
    private EntityRepository studentRepository;

    private static String studentEntityType = "student";

    @Test
    public void testStudentIngestionPersistence() throws IOException, SAXException {

        studentRepository.deleteAll(studentEntityType);

        int numberOfStudents = 2;
        List<NeutralRecord> neutralRecords = createStudentIngestionNeutralRecords(numberOfStudents);

        File neutralRecordsFile = IngestionTest.createNeutralRecordsFile(neutralRecords);

        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();

        persistenceProcessor.processIngestionStream(neutralRecordsFile, ingestionPersistenceProcessorOutputFile);

        verifyStudents(studentRepository, numberOfStudents);

    }

    @Test
    public void testStudentInterchangeXmlParsing() throws IOException, SAXException {

        studentRepository.deleteAll(studentEntityType);

        int numberOfStudents = 2;
        String xmlRecords = createStudentInterchangeXml(numberOfStudents);

        File inputFile = IngestionTest.createTestFile(IngestionTest.INGESTION_FILE_PREFIX,
                IngestionTest.INGESTION_XML_FILE_SUFFIX, xmlRecords);

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        edFiProcessor.processFileEntry(inputFileEntry);

        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();

        persistenceProcessor.processIngestionStream(inputFileEntry.getNeutralRecordFile(),
                ingestionPersistenceProcessorOutputFile);

        verifyStudents(studentRepository, numberOfStudents);

    }

    @Test
    public void testStudentInterchangeCsvParsing() throws IOException, SAXException {

        studentRepository.deleteAll(studentEntityType);

        int numberOfStudents = 2;
        String csvRecords = createStudentInterchangeCsv(numberOfStudents);

        File inputFile = IngestionTest.createTestFile(IngestionTest.INGESTION_FILE_PREFIX,
                IngestionTest.INGESTION_CSV_FILE_SUFFIX, csvRecords);

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.CSV, FileType.CSV_STUDENT,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        edFiProcessor.processFileEntry(inputFileEntry);

        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();

        persistenceProcessor.processIngestionStream(inputFileEntry.getNeutralRecordFile(),
                ingestionPersistenceProcessorOutputFile);

        verifyStudents(studentRepository, numberOfStudents);

    }

    @Test
    public void testStudentInterchangeCsvFileParsing() throws IOException, SAXException {

        studentRepository.deleteAll(studentEntityType);

        File inputFile = IngestionTest.getFile("smooks/InterchangeStudent.csv");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.CSV, FileType.CSV_STUDENT,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        edFiProcessor.processFileEntry(inputFileEntry);

        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();

        persistenceProcessor.processIngestionStream(inputFileEntry.getNeutralRecordFile(),
                ingestionPersistenceProcessorOutputFile);

        assertEquals(100, IngestionTest.getTotalCountOfEntityInRepository(studentRepository, studentEntityType));

    }

    // Static Methods
    public static String createStudentInterchangeXml(int numberOfStudents) {
        StringBuilder builder = new StringBuilder();

        builder.append(createStudentInterchangeXmlHeader());

        for (int index = 1; index <= numberOfStudents; index++) {
            Entity student = createStudent(index);
            builder.append(createStudentXml(student));
        }
        builder.append(createStudentInterchangeXmlFooter());

        return builder.toString();
    }

    public static String createStudentInterchangeCsv(int numberOfStudents) {
        StringBuilder builder = new StringBuilder();

        for (int index = 1; index <= numberOfStudents; index++) {
            Entity student = createStudent(index);
            builder.append(createStudentCsv(student));
            builder.append(System.getProperty("line.separator"));
        }

        return builder.toString();
    }

    public static List<NeutralRecord> createStudentIngestionNeutralRecords(int numberOfStudents) {
        List<NeutralRecord> list = new ArrayList<NeutralRecord>();

        for (int index = 1; index <= numberOfStudents; index++) {
            Entity student = createStudent(index);

            list.add(Translator.mapToNeutralRecord(student));
        }

        return list;
    }

    public static String createStudentIngestionJson(int numberOfStudents) throws IOException, SAXException {
        StringBuilder builder = new StringBuilder();

        for (int index = 1; index <= numberOfStudents; index++) {
            Entity student = createStudent(index);
            builder.append(Translator.mapToJson(student, "create"));
            builder.append(System.getProperty("line.separator"));
        }

        return builder.toString();
    }

    public static String createStudentInterchangeXmlHeader() {

        String interchangeXmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n";
        interchangeXmlHeader += "<InterchangeStudent xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-Student.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "\n";

        return interchangeXmlHeader;
    }

    public static String createStudentInterchangeXmlFooter() {

        String interchangeXmlFooter = "</InterchangeStudent>" + "\n";

        return interchangeXmlFooter;
    }

    public static String createStudentXml(Entity student) {
        String studentXml = "";

        studentXml += "<Student>" + "\n";

        // Test Version Only - allow specification of Student ID
        studentXml += "<StudentId>" + student.getBody().get("studentId") + "</StudentId>" + "\n";

        studentXml += "<StudentUniqueStateId>" + student.getBody().get("studentUniqueStateId")
                + "</StudentUniqueStateId>" + "\n";
        studentXml += "<Name><FirstName>" + student.getBody().get("firstName") + "</FirstName><LastSurname>"
                + student.getBody().get("lastSurname") + "</LastSurname></Name>" + "\n";
        studentXml += "<BirthData><BirthDate>" + student.getBody().get("birthDate") + "</BirthDate></BirthData>" + "\n";
        studentXml += "</Student>" + "\n";

        return studentXml;
    }

    public static String createStudentCsv(Entity student) {
        String studentCsv = "";

        // Test Version Only - allow specification of Student ID
        studentCsv += student.getBody().get("studentId") + ",";

        studentCsv += student.getBody().get("studentUniqueStateId") + ",";

        // Skip 5 fields for now
        studentCsv += ",,,,,";

        studentCsv += student.getBody().get("firstName") + ",";
        studentCsv += ",";
        studentCsv += student.getBody().get("lastSurname") + ",";

        // Skip 8 fields for now
        studentCsv += ",,,,,,,,";

        studentCsv += student.getBody().get("sex") + ",";

        studentCsv += student.getBody().get("birthDate");

        return studentCsv;
    }

    public static Entity createStudent(int studentId) {

        Map<String, Object> body = new HashMap<String, Object>();

        body.put("studentId", studentId);
        body.put("studentUniqueStateId", Integer.toString(studentId));
        body.put("firstName", "firstName" + "_" + studentId);
        body.put("lastSurname", "lastSurname" + "_" + studentId);
        body.put("sex", "Female");

        Date birthDate = new Timestamp(23234000);

        body.put("birthDate", birthDate);

        return new MongoEntity(studentEntityType, null, body, null);
    }

    public static void verifyStudents(EntityRepository repository, long numberOfStudents) {

        long repositorySize = IngestionTest.getTotalCountOfEntityInRepository(repository, studentEntityType);

        if (numberOfStudents > 0) {
            assertEquals(numberOfStudents, repositorySize);
        }

        for (int index = 1; index <= repositorySize; index++) {
            Map<String, String> queryMap = new HashMap<String, String>();
            queryMap.put("StudentId", Integer.toString(index));

            Iterator<Entity> students = (repository.findByFields(studentEntityType, queryMap)).iterator();

            if (students.hasNext())
                verifyStudent(index, students.next());
        }

    }

    public static void verifyStudent(int studentId, Entity student) {

        assertNotNull(student);
        assertEquals("" + studentId, student.getBody().get("studentUniqueStateId"));
        assertEquals("firstName" + "_" + studentId, student.getBody().get("firstName"));
        assertEquals("lastSurname" + "_" + studentId, student.getBody().get("lastSurname"));

    }

    public static String calculateTestDate(int studentId) {
        String testDate = "";

        int yearId = studentId % 10000;
        testDate = "" + yearId;
        if (yearId < 10)
            testDate = "000" + testDate;
        else if (yearId < 100)
            testDate = "00" + testDate;
        else if (yearId < 1000)
            testDate = "0" + testDate;

        testDate = testDate + "-01-01";
        return testDate;
    }

}
