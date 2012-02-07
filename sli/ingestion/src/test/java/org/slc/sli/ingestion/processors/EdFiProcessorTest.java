package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.util.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.xml.sax.SAXException;

/**
 * Tests for EdFiProcessor
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class EdFiProcessorTest {

    @Autowired
    private EdFiProcessor edFiProcessor;

    @Ignore
    @Test
    public void shouldBeAbleToDeriveConfigurationFile() {

    }

    @Test
    public void shouldReportErrorEmptyCsv() throws IOException, SAXException {

        // Get Input File
        File inputFile = IngestionTest.getFile("fileLevelTestData/invalidCSV/emptyFile/student.csv");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.CSV, FileType.CSV_STUDENT,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        edFiProcessor.processFileEntry(inputFileEntry);

        assertTrue("Empty csv file should give error.", inputFileEntry.getErrorReport().hasErrors());
    }

    @Test
    public void shouldReportErrorEmptyXml() throws IOException, SAXException {

        // Get Input File
        File inputFile = IngestionTest.getFile("fileLevelTestData/invalidXML/emptyFile/student.xml");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        edFiProcessor.processFileEntry(inputFileEntry);

        assertTrue("Empty xml file should give error.", inputFileEntry.getErrorReport().hasErrors());
    }

    @Test
    @Ignore
    public void shouldTranslateStudentCsvToNeutralRecords() throws IOException, SAXException {

        // Get Input File
        File inputFile = IngestionTest.getFile("smooks/InterchangeStudent.csv");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.CSV, FileType.CSV_STUDENT,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        // Translate EDFI File to AVRO NeutralRecords
        edFiProcessor.processFileEntry(inputFileEntry);

        // Parse Result SLI Records from AVRO NeutralRecords File
        List<NeutralRecord> resultList = IngestionTest.getNeutralRecords(inputFileEntry.getNeutralRecordFile());

        // Temporary
        // FileUtils.writeStringToFile(File.createTempFile("SLI",".sli"), resultString);

        // Get Expected SLI Records File
        File expectedFile = IngestionTest.getFile("smooks/InterchangeStudent.sli");

        // Parse Expected SLI Records from AVRO NeutralRecords File
        List<NeutralRecord> expectedList = IngestionTest.getNeutralRecords(expectedFile);

        // Verify
        verifyNeutralRecords(expectedList, resultList);
    }

    @Test
    @Ignore
    public void shouldTranslateSchoolCsvToNeutralRecords() throws IOException, SAXException {

        // Get Input File
        File inputFile = IngestionTest.getFile("smooks/InterchangeSchool.csv");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.CSV, FileType.CSV_SCHOOL,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        // Translate EDFI File to AVRO NeutralRecords
        edFiProcessor.processFileEntry(inputFileEntry);

        // Parse Result SLI Records from AVRO NeutralRecords File
        List<NeutralRecord> resultList = IngestionTest.getNeutralRecords(inputFileEntry.getNeutralRecordFile());

        // Get Expected SLI Records File
        File expectedFile = IngestionTest.getFile("smooks/InterchangeSchool.sli");

        // Parse Expected SLI Records from AVRO NeutralRecords File
        List<NeutralRecord> expectedList = IngestionTest.getNeutralRecords(expectedFile);

        // Verify
        verifyNeutralRecords(expectedList, resultList);
    }

    @Test
    public void shouldTranslateStudentSchoolAssociationsCsvToNeutralRecords() throws IOException, SAXException {

        // Get Input File
        File inputFile = IngestionTest.getFile("smooks/InterchangeStudentSchoolAssociation.csv");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.CSV,
                FileType.CSV_STUDENT_SCHOOL_ASSOCIATION, inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        // Translate EDFI File to AVRO NeutralRecords
        edFiProcessor.processFileEntry(inputFileEntry);

        // Parse Result SLI Records from AVRO NeutralRecords File
        List<NeutralRecord> resultList = IngestionTest.getNeutralRecords(inputFileEntry.getNeutralRecordFile());

        // Get Expected SLI Records File
        File expectedFile = IngestionTest.getFile("smooks/InterchangeStudentSchoolAssociation.sli");

        // Parse Expected SLI Records from AVRO NeutralRecords File
        List<NeutralRecord> expectedList = IngestionTest.getNeutralRecords(expectedFile);

        // Verify
        verifyNeutralRecords(expectedList, resultList);
    }

    private void verifyNeutralRecords(List<NeutralRecord> expectedList, List<NeutralRecord> resultList) {

        Assert.assertEquals("CSV-NeutralRecords translation failed since list sizes do not match", expectedList.size(),
                resultList.size());

        for (int index = 0; index < expectedList.size(); index++) {
            Assert.assertTrue("CSV-NeutralRecords translation failed since records are not equal",
                    expectedList.get(index).equals(resultList.get(index)));
        }
    }

}
