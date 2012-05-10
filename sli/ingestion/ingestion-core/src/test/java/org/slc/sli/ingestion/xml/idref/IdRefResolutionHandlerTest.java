package org.slc.sli.ingestion.xml.idref;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import junitx.util.PrivateAccessor;

import org.apache.commons.io.IOUtils;
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
    public void testFindIDRefsToResolve() throws Throwable {
        File inputFile = IngestionTest.getFile("ReferenceResolution/InterchangeStudentGrade.xml");
        File tempInputFile = new File(inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf(".xml")) + "_TEMP.xml");

        IOUtils.copy(new FileInputStream(inputFile), new FileOutputStream(tempInputFile));

        @SuppressWarnings("unchecked")
        Set<String> result = (Set<String>) PrivateAccessor.invoke(idRefResolutionHandler, "findIDRefsToResolve", new Class[]{File.class}, new Object[]{tempInputFile});

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains("GBE-8th Grade English-3"));
    }

    @Test
    public void testFindMatchingEntities() throws Throwable {
        File inputFile = IngestionTest.getFile("ReferenceResolution/InterchangeStudentGrade.xml");
        File tempInputFile = new File(inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf(".xml")) + "_TEMP.xml");

        IOUtils.copy(new FileInputStream(inputFile), new FileOutputStream(tempInputFile));


        Set<String> refs = (Set<String>) PrivateAccessor.invoke(idRefResolutionHandler, "findIDRefsToResolve", new Class[]{File.class}, new Object[]{tempInputFile});
        Map<String, String> result = (Map<String, String>) PrivateAccessor.invoke(idRefResolutionHandler, "findMatchingEntities", new Class[]{File.class, Set.class}, new Object[]{tempInputFile, refs});

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.containsKey("GBE-8th Grade English-3"));
    }

    @Test
    public void testProcess() throws IOException {
        File inputFile = IngestionTest.getFile("ReferenceResolution/InterchangeStudentGrade.xml");
        File tempInputFile = new File(inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf(".xml")) + "_TEMP.xml");


        InputStream inputFileStream = new FileInputStream(inputFile);
        OutputStream tempInputFileStream = new FileOutputStream(tempInputFile);
        IOUtils.copy(inputFileStream , tempInputFileStream);

        inputFileStream.close();
        tempInputFileStream.close();

        ErrorReport errorReport = Mockito.mock(ErrorReport.class);
        FileProcessStatus fileProcessStatus = Mockito.mock(FileProcessStatus.class);
        String beforeHash = MD5.calculate(inputFile);
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                FileType.XML_STUDENT_GRADES, tempInputFile.getName(), beforeHash);
        inputFileEntry.setFile(tempInputFile);

        idRefResolutionHandler.doHandling(inputFileEntry, errorReport, fileProcessStatus);

        String afterHash = MD5.calculate(tempInputFile);

        Assert.assertFalse(beforeHash.equals(afterHash));

    }
}
