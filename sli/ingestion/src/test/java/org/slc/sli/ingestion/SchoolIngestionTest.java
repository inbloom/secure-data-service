package org.slc.sli.ingestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.util.MD5;
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

/**
 * a set of test for school ingestion
 *
 * @author yuan
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class SchoolIngestionTest {

    @Autowired
    private EdFiProcessor edFiProcessor;

    @Autowired
    private PersistenceProcessor persistenceProcessor;

    // @Autowired
    // private SchoolRepository schoolRepository;

    @Autowired
    private EntityRepository repository;

    private static String schoolEntityType = "school";

    @Ignore// TODO integration tests will be moved out of this module soon
    @Test
    public void testSchoolIngestionPersistence() throws IOException, SAXException {

        repository.deleteAll(schoolEntityType);

        int numberOfSchools = 2;
        List<NeutralRecord> neutralRecords = createSchoolIngestionNeutralRecords(numberOfSchools);

        File neutralRecordsFile = IngestionTest.createNeutralRecordsFile(neutralRecords);

        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();

        persistenceProcessor.processIngestionStream(neutralRecordsFile, ingestionPersistenceProcessorOutputFile);

        verifySchools(repository, numberOfSchools);

    }

    @Ignore// TODO integration tests will be moved out of this module soon
    @Test
    public void testSchoolInterchangeXmlParsing() throws IOException, SAXException {

        repository.deleteAll(schoolEntityType);

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

        verifySchools(repository, 0);

    }

    @Ignore// TODO integration tests will be moved out of this module soon
    @Test
    public void testSchoolInterchangeCsvParsing() throws IOException, SAXException {

        repository.deleteAll(schoolEntityType);

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

        verifySchools(repository, numberOfSchools);

    }

    @Ignore// TODO integration tests will be moved out of this module soon
    @Test
    public void testSchoolInterchangeCsvFileParsing() throws IOException, SAXException {

        repository.deleteAll(schoolEntityType);

        File inputFile = IngestionTest.getFile("smooks/InterchangeSchool.csv");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.CSV, FileType.CSV_SCHOOL,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        edFiProcessor.processFileEntry(inputFileEntry);

        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();

        persistenceProcessor.processIngestionStream(inputFileEntry.getNeutralRecordFile(),
                ingestionPersistenceProcessorOutputFile);

        assertEquals(2, IngestionTest.getTotalCountOfEntityInRepository(repository, schoolEntityType));

    }

    public static String createSchoolInterchangeXml(int numberOfSchools) {
        StringBuilder builder = new StringBuilder();

        builder.append(createSchoolInterchangeXmlHeader());

        for (int index = 1; index <= numberOfSchools; index++) {
            Entity school = createSchool(index);
            builder.append(createSchoolXml(school));
        }
        builder.append(createSchoolInterchangeXmlFooter());

        return builder.toString();
    }

    public static String createSchoolIngestionJson(int numberOfSchools) throws IOException, SAXException {
        StringBuilder builder = new StringBuilder();

        for (int index = 1; index <= numberOfSchools; index++) {
            Entity school = createSchool(index);
            builder.append(Translator.mapToJson(school, "create"));
            builder.append(System.getProperty("line.separator"));
        }

        return builder.toString();
    }

    public static String createSchoolInterchangeCsv(int numberOfSchools) {
        StringBuilder builder = new StringBuilder();

        for (int index = 1; index <= numberOfSchools; index++) {
            Entity school = createSchool(index);
            builder.append(createSchoolCsv(school));
            builder.append(System.getProperty("line.separator"));
        }

        return builder.toString();
    }

    public static List<NeutralRecord> createSchoolIngestionNeutralRecords(int numberOfSchools) {
        List<NeutralRecord> list = new ArrayList<NeutralRecord>();

        for (int index = 1; index <= numberOfSchools; index++) {
            Entity school = createSchool(index);

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

    public static String createSchoolXml(Entity school) {
        String schoolXml = "";

        schoolXml += "<School>" + "\n";

        // Test Version Only - allow specification of Student ID
        schoolXml += "<SchoolId>" + (school.getBody()).get("schoolId") + "</SchoolId>" + "\n";

        schoolXml += "<StateOrganizationId>" + (school.getBody()).get("stateOrganizationId") + "</StateOrganizationId>"
                + "\n";
        schoolXml += "<NameOfInstitution>" + (school.getBody()).get("fullName") + "</NameOfInstitution>" + "\n";
        schoolXml += "</School>" + "\n";

        return schoolXml;
    }

    public static String createSchoolCsv(Entity school) {
        String schoolCsv = "";

        // Test Version Only - allow specification of Student ID
        schoolCsv += (school.getBody()).get("schoolId") + ",";

        schoolCsv += (school.getBody()).get("stateOrganizationId") + ",";

        // Skip 2 fields for now
        schoolCsv += ",,";

        schoolCsv += (school.getBody()).get("fullName");

        return schoolCsv;
    }

    public static Entity createSchool(int schoolId) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("schoolId", schoolId);
        body.put("stateOrganizationId", Integer.toString(schoolId));
        body.put("fullName", "nameOfInstitution" + "_" + schoolId);

        return new MongoEntity(schoolEntityType, null, body, null);
    }

    public static void verifySchools(EntityRepository repository, long numberOfSchools) {

        long repositorySize = IngestionTest.getTotalCountOfEntityInRepository(repository, schoolEntityType);

        assertEquals(numberOfSchools, repositorySize);

        for (int index = 1; index <= repositorySize; index++) {
            Map<String, String> queryMap = new HashMap<String, String>();
            queryMap.put("schoolId", Integer.toString(index));

            Iterator<Entity> schools = (repository.findByFields(schoolEntityType, queryMap)).iterator();
            if (schools.hasNext()) {
                verifySchool(index, schools.next());
            }
        }

    }

    public static void verifySchool(int schoolId, Entity school) {
        assertNotNull(school);
        assertEquals("" + schoolId, (school.getBody()).get("schoolId"));
        assertEquals("nameOfInstitution" + "_" + schoolId, (school.getBody()).get("fullName"));

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
