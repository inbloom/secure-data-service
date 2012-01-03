package org.slc.sli.ingestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.domain.School;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.util.MD5;
import org.slc.sli.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.xml.sax.SAXException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class SchoolIngestionTest {
    
    @Autowired
    private EdFiProcessor edFiProcessor;
    
    @Autowired
    private PersistenceProcessor persistenceProcessor;
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    @Test
    public void testSchoolIngestionPersistence() throws IOException, SAXException {
        
        schoolRepository.deleteAll();
        
        int numberOfSchools = 2;
        List<NeutralRecord> neutralRecords = createSchoolIngestionNeutralRecords(numberOfSchools);
        
        File neutralRecordsFile = IngestionTest.createNeutralRecordsFile(neutralRecords);
        
        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();
        
        persistenceProcessor.processIngestionStream(neutralRecordsFile, ingestionPersistenceProcessorOutputFile);
        
        verifySchools(schoolRepository, numberOfSchools);
        
    }
    
    @Test
    public void testSchoolInterchangeXmlParsing() throws IOException, SAXException {
        
        schoolRepository.deleteAll();
        
        int numberOfSchools = 2;
        String xmlRecords = createSchoolInterchangeXml(numberOfSchools);
        
        File inputFile = IngestionTest.createTestFile(xmlRecords);
        
        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.CSV, FileType.CSV_SCHOOL,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);
        
        edFiProcessor.processFileEntry(inputFileEntry);
        
        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();
        
        persistenceProcessor.processIngestionStream(inputFileEntry.getNeutralRecordFile(),
                ingestionPersistenceProcessorOutputFile);
        
        verifySchools(schoolRepository, 0);
        
    }
    
    @Test
    public void testSchoolInterchangeCsvParsing() throws IOException, SAXException {
        
        schoolRepository.deleteAll();
        
        int numberOfSchools = 2;
        String csvRecords = createSchoolInterchangeCsv(numberOfSchools);
        
        File inputFile = IngestionTest.createTestFile(IngestionTest.INGESTION_FILE_PREFIX,
                IngestionTest.INGESTION_CSV_FILE_SUFFIX, csvRecords);
        
        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.CSV, FileType.CSV_SCHOOL,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);
        
        edFiProcessor.processFileEntry(inputFileEntry);
        
        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();
        
        persistenceProcessor.processIngestionStream(inputFileEntry.getNeutralRecordFile(),
                ingestionPersistenceProcessorOutputFile);
        
        verifySchools(schoolRepository, 0);
        
    }
    
    @Test
    public void testSchoolInterchangeCsvFileParsing() throws IOException, SAXException {
        
        schoolRepository.deleteAll();
        
        File inputFile = IngestionTest.getFile("smooks/InterchangeSchool.csv");
        
        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.CSV, FileType.CSV_SCHOOL,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);
        
        edFiProcessor.processFileEntry(inputFileEntry);
        
        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();
        
        persistenceProcessor.processIngestionStream(inputFileEntry.getNeutralRecordFile(),
                ingestionPersistenceProcessorOutputFile);
        
        assertEquals(2, schoolRepository.count());
        
    }
    
    public static String createSchoolInterchangeXml(int numberOfSchools) {
        StringBuilder builder = new StringBuilder();
        
        builder.append(createSchoolInterchangeXmlHeader());
        
        for (int index = 1; index <= numberOfSchools; index++) {
            School school = createSchool(index);
            builder.append(createSchoolXml(school));
        }
        builder.append(createSchoolInterchangeXmlFooter());
        
        return builder.toString();
    }
    
    public static String createSchoolIngestionJson(int numberOfSchools) throws IOException, SAXException {
        StringBuilder builder = new StringBuilder();
        
        for (int index = 1; index <= numberOfSchools; index++) {
            School school = createSchool(index);
            builder.append(Translator.mapToJson(school, "create"));
            builder.append(System.getProperty("line.separator"));
        }
        
        return builder.toString();
    }
    
    public static String createSchoolInterchangeCsv(int numberOfSchools) {
        StringBuilder builder = new StringBuilder();
        
        for (int index = 1; index <= numberOfSchools; index++) {
            School school = createSchool(index);
            builder.append(createSchoolCsv(school));
            builder.append(System.getProperty("line.separator"));
        }
        
        return builder.toString();
    }
    
    public static List<NeutralRecord> createSchoolIngestionNeutralRecords(int numberOfSchools) {
        List<NeutralRecord> list = new ArrayList<NeutralRecord>();
        
        for (int index = 1; index <= numberOfSchools; index++) {
            School school = createSchool(index);
            
            list.add(Translator.mapToNeutralRecord(school));
        }
        
        return list;
    }
    
    public static String createSchoolInterchangeXmlHeader() {
        
        String interchangeXmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n";
        interchangeXmlHeader += "<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "\n";
        
        return interchangeXmlHeader;
    }
    
    public static String createSchoolInterchangeXmlFooter() {
        
        String interchangeXmlFooter = "</InterchangeEducationOrganization>" + "\n";
        
        return interchangeXmlFooter;
    }
    
    public static String createSchoolXml(School school) {
        String schoolXml = "";
        
        schoolXml += "<School>" + "\n";
        
        // Test Version Only - allow specification of Student ID
        schoolXml += "<SchoolId>" + school.getSchoolId() + "</SchoolId>" + "\n";
        
        schoolXml += "<StateOrganizationId>" + school.getStateOrganizationId() + "</StateOrganizationId>" + "\n";
        schoolXml += "<NameOfInstitution>" + school.getFullName() + "</NameOfInstitution>" + "\n";
        schoolXml += "</School>" + "\n";
        
        return schoolXml;
    }
    
    public static String createSchoolCsv(School school) {
        String schoolCsv = "";
        
        // Test Version Only - allow specification of Student ID
        schoolCsv += school.getSchoolId() + ",";
        
        schoolCsv += school.getStateOrganizationId() + ",";
        
        // Skip 2 fields for now
        schoolCsv += ",,";
        
        schoolCsv += school.getFullName();
        
        return schoolCsv;
    }
    
    public static School createSchool(int schoolId) {
        School school = new School();
        
        school.setSchoolId(schoolId);
        school.setStateOrganizationId("" + schoolId);
        school.setFullName("nameOfInstitution" + "_" + schoolId);
        
        return school;
    }
    
    public static void verifySchools(PagingAndSortingRepository<?, Integer> repository, long numberOfSchools) {
        
        long repositorySize = repository.count();
        
        if (numberOfSchools > 0) {
            assertEquals(repositorySize, numberOfSchools);
        }
        
        for (int index = 1; index <= repositorySize; index++) {
            School school = (School) repository.findOne(index);
            verifySchool(index, school);
        }
        
    }
    
    public static void verifySchool(int schoolId, School school) {
        
        assertNotNull(school);
        assertEquals("" + schoolId, school.getStateOrganizationId());
        assertEquals("nameOfInstitution" + "_" + schoolId, school.getFullName());
        
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
