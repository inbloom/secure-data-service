package org.slc.sli.ingestion.handler;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.util.MD5;

/**
 * tests for SmooksFileHandler
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SmooksFileHandlerTest {

    @Autowired
    SmooksFileHandler smooksFileHandler;

    @Test
    public void firstRecordMissingColumnCsv() throws IOException, SAXException {

        // Get Input File
        File inputFile = IngestionTest.getFile("fileLevelTestData/invalidCSV/firstRecordMissingColumn/student.csv");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.CSV, FileType.CSV_STUDENT,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        FaultsReport errorReport = new FaultsReport();
        smooksFileHandler.handle(inputFileEntry, errorReport);

        assertTrue("Missing column should give error.", errorReport.hasErrors());
    }

    @Test
    public void incorrectColumnNameCsv() throws IOException, SAXException {

        // Get Input File
        File inputFile = IngestionTest.getFile("fileLevelTestData/invalidCSV/IncorrectColumnName/student.csv");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.CSV, FileType.CSV_STUDENT,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        FaultsReport errorReport = new FaultsReport();
        smooksFileHandler.handle(inputFileEntry, errorReport);

        assertTrue("Incorrect column name should give error.", errorReport.hasErrors());
    }

    /*
     * XML TESTS
     */

    @Test
    @Ignore
    // we have removed the type information from the smooks mappings for this sprint.
    // TODO: remove @Ignore when the smooks mappings once again contain type information
    public void valueTypeNotMatchAttributeType() throws IOException, SAXException {

        // Get Input File
        File inputFile = IngestionTest
                .getFile("fileLevelTestData/invalidXML/valueTypeNotMatchAttributeType/student.xml");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        FaultsReport errorReport = new FaultsReport();
        smooksFileHandler.handle(inputFileEntry, errorReport);

        assertTrue("Value type mismatch should give error.", errorReport.hasErrors());
    }

    @Test
    public void malformedXML() throws IOException, SAXException {

        // Get Input File
        File inputFile = IngestionTest.getFile("fileLevelTestData/invalidXML/malformedXML/student.xml");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        FaultsReport errorReport = new FaultsReport();
        smooksFileHandler.handle(inputFileEntry, errorReport);

        assertTrue("Malformed XML should give error.", errorReport.hasErrors());
    }

    @Test
    public void validXml() throws IOException, SAXException {

        // Get Input File
        File inputFile = IngestionTest.getFile("fileLevelTestData/validXML/student.xml");

        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT,
                inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);

        FaultsReport errorReport = new FaultsReport();
        smooksFileHandler.handle(inputFileEntry, errorReport);

        assertTrue("Valid XML should give no errors.", !errorReport.hasErrors());
    }
}
