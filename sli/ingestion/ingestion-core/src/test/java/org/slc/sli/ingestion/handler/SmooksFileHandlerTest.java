package org.slc.sli.ingestion.handler;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import junitx.util.PrivateAccessor;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.milyn.Smooks;
import org.mockito.Mockito;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.smooks.SliSmooksFactory;
import org.slc.sli.ingestion.util.MD5;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

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
    
    @Autowired
    LocalFileSystemLandingZone lz;
    
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
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                FileType.XML_STUDENT_PARENT_ASSOCIATION, inputFile.getName(), MD5.calculate(inputFile));
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
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                FileType.XML_STUDENT_PARENT_ASSOCIATION, inputFile.getName(), MD5.calculate(inputFile), lz.getLZId());
        inputFileEntry.setFile(inputFile);
        inputFileEntry.setBatchJobId("111111111-222222222-333333333-444444444-555555555-6");
        
        FaultsReport errorReport = new FaultsReport();
        smooksFileHandler.handle(inputFileEntry, errorReport);
        
        assertTrue("Malformed XML should give error.", errorReport.hasErrors());
    }
    
    @Test
    @Ignore
    // TODO this needs to work with a mock mongo instance. It shouldn't be trying to create its own
    // database connection
    public void validXml() throws IOException, SAXException {
        
        // Get Input File
        File inputFile = IngestionTest.getFile("fileLevelTestData/validXML/student.xml");
        
        // Create Ingestion File Entry
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                FileType.XML_STUDENT_PARENT_ASSOCIATION, inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);
        inputFileEntry.setBatchJobId("111111111-222222222-333333333-444444444-555555555-6");
        
        FaultsReport errorReport = new FaultsReport();
        smooksFileHandler.handle(inputFileEntry, errorReport);
        
        assertTrue("Valid XML should give no errors." + errorReport.getFaults(), !errorReport.hasErrors());
    }
    
    @Test
    public void testXsdPreValidation() throws IOException, SAXException, NoSuchFieldException {
        Smooks smooks = Mockito.mock(Smooks.class);
        SliSmooksFactory factory = Mockito.mock(SliSmooksFactory.class);
        Mockito.when(factory.createInstance(Mockito.any(IngestionFileEntry.class), Mockito.any(ErrorReport.class)))
                .thenReturn(smooks);
        PrivateAccessor.setField(smooksFileHandler, "sliSmooksFactory", factory);
        
        File xmlFile = IngestionTest.getFile("XsdValidation/InterchangeStudent-Valid.xml");
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT_PARENT_ASSOCIATION,
                xmlFile.getAbsolutePath(), "", lz.getLZId());
        ife.setFile(xmlFile);
        ErrorReport errorReport = Mockito.mock(ErrorReport.class);
        
        smooksFileHandler.handle(ife, errorReport);
        Mockito.verify(errorReport, Mockito.never()).error(Mockito.anyString(), Mockito.anyObject());
    }
}
