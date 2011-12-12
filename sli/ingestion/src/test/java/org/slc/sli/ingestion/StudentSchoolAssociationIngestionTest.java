package org.slc.sli.ingestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.xml.sax.SAXException;

import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.processors.EdFiProcessor;
import org.slc.sli.ingestion.processors.PersistenceProcessor;
import org.slc.sli.ingestion.util.MD5;
import org.slc.sli.repository.SchoolRepository;
import org.slc.sli.repository.StudentRepository;
import org.slc.sli.repository.StudentSchoolAssociationRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class StudentSchoolAssociationIngestionTest {
    
    public static final int MOCK_STUDENT_SCHOOL_REPOSITORY_ID_OFFSET = 99;
    
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
        
        studentRepository.deleteAll();
        this.createStudents(numberOfStudentSchoolAssociations);
        
        schoolRepository.deleteAll();
        this.createSchools(numberOfStudentSchoolAssociations);
        
        studentSchoolAssociationRepository.deleteAll();
        
        String xmlRecords = createStudentSchoolAssociationInterchangeXml(numberOfStudentSchoolAssociations);
        
        File inputFile = IngestionTest.createTestFile(xmlRecords);
        
        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                FileType.XML_STUDENT_ENROLLMENT, inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);
        
        File ingestionEdFiProcessorOutputFile = IngestionTest.createTempFile();
        
        edFiProcessor.processIngestionStream(inputFileEntry, ingestionEdFiProcessorOutputFile);
        
        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();
        
        persistenceProcessor.processIngestionStream(ingestionEdFiProcessorOutputFile,
                ingestionPersistenceProcessorOutputFile);
        
        verifyStudentSchoolAssociations(studentSchoolAssociationRepository, 0);
        
    }
    
    @Test
    public void testStudentSchoolAssociationInterchangeCsvParsing() throws IOException, SAXException {
        
        int numberOfStudentSchoolAssociations = 2;
        
        studentRepository.deleteAll();
        this.createStudents(numberOfStudentSchoolAssociations);
        
        schoolRepository.deleteAll();
        this.createSchools(numberOfStudentSchoolAssociations);
        
        studentSchoolAssociationRepository.deleteAll();
        
        String csvRecords = createStudentSchoolAssociationInterchangeCsv(numberOfStudentSchoolAssociations);
        
        File inputFile = IngestionTest.createTestFile(csvRecords);
        
        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.CSV,
                FileType.CSV_STUDENT_SCHOOL_ASSOCIATION, inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);
        
        File ingestionEdFiProcessorOutputFile = IngestionTest.createTempFile();
        
        edFiProcessor.processIngestionStream(inputFileEntry, ingestionEdFiProcessorOutputFile);
        
        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();
        
        persistenceProcessor.processIngestionStream(ingestionEdFiProcessorOutputFile,
                ingestionPersistenceProcessorOutputFile);
        
        verifyStudentSchoolAssociations(studentSchoolAssociationRepository, 0);
        
    }
    
    @SuppressWarnings("unused")
    private void createStudents(int numberOfStudents) throws IOException, SAXException {
        
        studentRepository.deleteAll();
        
        StudentIngestionTest studentIngestionTest = new StudentIngestionTest();
        
        List<NeutralRecord> neutralRecords = StudentIngestionTest
                .createStudentIngestionNeutralRecords(numberOfStudents);
        
        File neutralRecordsFile = IngestionTest.createNeutralRecordsFile(neutralRecords);
        
        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();
        
        persistenceProcessor.processIngestionStream(neutralRecordsFile, ingestionPersistenceProcessorOutputFile);
        
    }
    
    @SuppressWarnings("unused")
    private void createSchools(int numberOfSchools) throws IOException, SAXException {
        
        schoolRepository.deleteAll();
        
        SchoolIngestionTest schoolIngestionTest = new SchoolIngestionTest();
        
        List<NeutralRecord> neutralRecords = SchoolIngestionTest.createSchoolIngestionNeutralRecords(numberOfSchools);
        
        File neutralRecordsFile = IngestionTest.createNeutralRecordsFile(neutralRecords);
        
        File ingestionPersistenceProcessorOutputFile = IngestionTest.createTempFile();
        
        persistenceProcessor.processIngestionStream(neutralRecordsFile, ingestionPersistenceProcessorOutputFile);
        
    }
    
    public static String createStudentSchoolAssociationInterchangeXml(int numberOfAssociations) {
        StringBuilder builder = new StringBuilder();
        
        builder.append(createStudentSchoolAssociationInterchangeXmlHeader());
        
        for (int index = 1; index <= numberOfAssociations; index++) {
            StudentSchoolAssociationInterchange studentSchoolAssociation = createStudentSchoolAssociation(index);
            builder.append(createStudentSchoolAssociationXml(studentSchoolAssociation));
        }
        builder.append(createStudentSchoolAssociationInterchangeXmlFooter());
        
        return builder.toString();
    }
    
    public static String createStudentSchoolAssociationInterchangeCsv(int numberOfAssociations) {
        StringBuilder builder = new StringBuilder();
        
        for (int index = 1; index <= numberOfAssociations; index++) {
            StudentSchoolAssociationInterchange studentSchoolAssociation = createStudentSchoolAssociation(index);
            builder.append(createStudentSchoolAssociationCsv(studentSchoolAssociation));
            builder.append(System.getProperty("line.separator"));
        }
        
        return builder.toString();
    }
    
    public static String createStudentSchoolAssociationInterchangeXmlHeader() {
        
        String interchangeXmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n";
        interchangeXmlHeader += "<InterchangeStudentEnrollment xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentEnrollment.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">"
                + "\n";
        
        return interchangeXmlHeader;
    }
    
    public static String createStudentSchoolAssociationInterchangeXmlFooter() {
        
        String interchangeXmlFooter = "</InterchangeStudentEnrollment>" + "\n";
        
        return interchangeXmlFooter;
    }
    
    public static String createStudentSchoolAssociationXml(StudentSchoolAssociationInterchange studentSchoolAssociation) {
        String studentSchoolAssociationXml = "";
        
        studentSchoolAssociationXml += "<StudentSchoolAssociation>" + "\n";
        
        // Test Version Only - allow specification of Association ID
        studentSchoolAssociationXml += "<AssociationId>" + studentSchoolAssociation.getAssociationId() + "</AssociationId>" + "\n";
        
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
    
    public static String createStudentSchoolAssociationCsv(StudentSchoolAssociationInterchange studentSchoolAssociation) {
        String studentSchoolAssociationCsv = "";
        
        // Test Version Only - allow specification of Student ID
        studentSchoolAssociationCsv += studentSchoolAssociation.getAssociationId() + ",";
        
        // Skip 2 fields
        studentSchoolAssociationCsv += ",,";
        
        studentSchoolAssociationCsv += studentSchoolAssociation.getStudentUniqueStateId() + ",";
        
        // Skip 22 fields for now
        studentSchoolAssociationCsv += ",,,,,,,,,,,,,,,,,,,,,,";
        
        studentSchoolAssociationCsv += studentSchoolAssociation.getStateOrganizationId();
        
        return studentSchoolAssociationCsv;
    }
    
    public static StudentSchoolAssociationInterchange createStudentSchoolAssociation(int associationId) {
        StudentSchoolAssociationInterchange studentSchoolAssociation = new StudentSchoolAssociationInterchange();
        
        studentSchoolAssociation.setAssociationId(associationId);
        studentSchoolAssociation.setStudentUniqueStateId("" + associationId);
        studentSchoolAssociation.setStateOrganizationId("" + associationId);
        
        return studentSchoolAssociation;
    }
    
    public static void verifyStudentSchoolAssociations(PagingAndSortingRepository<?, Integer> repository,
            long numberOfAssociations) {
        
        long repositorySize = repository.count();
        
        if (numberOfAssociations > 0) {
            assertEquals(repositorySize, numberOfAssociations);
        }
        
        Assert.assertTrue((repositorySize > 0));
        
        Iterable iterable = repository.findAll();
        Iterator iterator = iterable.iterator();
        while (iterator.hasNext()) {
            StudentSchoolAssociation studentSchoolAssociation = (StudentSchoolAssociation)iterator.next();
            verifyStudentSchoolAssociation(studentSchoolAssociation);
        }
        
    }
    
    public static void verifyStudentSchoolAssociation(StudentSchoolAssociation studentSchoolAssociation) {
        
        assertNotNull(studentSchoolAssociation);
        assertEquals(studentSchoolAssociation.getStudentId(), studentSchoolAssociation.getSchoolId());
        
    }
    
}
