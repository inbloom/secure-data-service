package org.slc.sli.ingestion.handler;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.util.MD5;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 *
 * @author npandey
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class IdRefResolutionHandlerTest {

    @Autowired
    private IdRefResolutionHandler idRefResolutionHandler;

    @Test
    @Ignore
    public void testListOfReferences() throws FileNotFoundException {
        File inputFile = IngestionTest.getFile("ReferenceResolution/RefXml_sample.xml");
        ErrorReport errorReport = Mockito.mock(ErrorReport.class);
        FileProcessStatus fileProcessStatus = Mockito.mock(FileProcessStatus.class);
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                FileType.XML_STUDENT_ASSESSMENT, inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);
        idRefResolutionHandler.doHandling(inputFileEntry, errorReport, fileProcessStatus);
    }

}
