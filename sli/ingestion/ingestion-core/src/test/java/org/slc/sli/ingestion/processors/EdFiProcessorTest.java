package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.util.MD5;

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

}
