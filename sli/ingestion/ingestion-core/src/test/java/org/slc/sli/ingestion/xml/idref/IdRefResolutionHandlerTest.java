package org.slc.sli.ingestion.xml.idref;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junitx.util.PrivateAccessor;

import org.apache.commons.io.IOUtils;
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

    public void testListOfReferences() throws FileNotFoundException {
        File inputFile = IngestionTest.getFile("ReferenceResolution/gradebook.xml");
        ErrorReport errorReport = Mockito.mock(ErrorReport.class);
        FileProcessStatus fileProcessStatus = Mockito.mock(FileProcessStatus.class);
        IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                FileType.XML_STUDENT_GRADES, inputFile.getName(), MD5.calculate(inputFile));
        inputFileEntry.setFile(inputFile);
        idRefResolutionHandler.doHandling(inputFileEntry, errorReport, fileProcessStatus);

    }

    @Test
    public void testLoadRefs() throws Throwable {
        File inputFile = IngestionTest.getFile("ReferenceResolution/InterchangeStudentGrade.xml");
        File tempInputFile = new File(inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf(".xml")) + "_TEMP.xml");

        IOUtils.copy(new FileInputStream(inputFile), new FileOutputStream(tempInputFile));

        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) PrivateAccessor.invoke(idRefResolutionHandler, "loadRefs", new Class[]{File.class}, new Object[]{tempInputFile});

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("GBE-8th Grade English-3", result.get(0));
    }

    @Test
    public void testLoadIds() throws Throwable {
        File inputFile = IngestionTest.getFile("ReferenceResolution/InterchangeStudentGrade.xml");
        File tempInputFile = new File(inputFile.getPath().substring(0, inputFile.getPath().lastIndexOf(".xml")) + "_TEMP.xml");

        IOUtils.copy(new FileInputStream(inputFile), new FileOutputStream(tempInputFile));


        List<String> refs = (List<String>) PrivateAccessor.invoke(idRefResolutionHandler, "loadRefs", new Class[]{File.class}, new Object[]{tempInputFile});
        Map<String, String> result = (Map<String, String>) PrivateAccessor.invoke(idRefResolutionHandler, "loadIds", new Class[]{File.class, List.class}, new Object[]{tempInputFile, refs});

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.containsKey("GBE-8th Grade English-3"));
        Assert.assertEquals(
        "<GradebookEntry id=\"GBE-8th Grade English-3\">\n"
      + "      <GradebookEntryType>Unit test</GradebookEntryType>\n"
      + "      <DateAssigned>2011-09-29</DateAssigned>\n"
      + "      <SectionReference>\n"
      + "        <SectionIdentity>\n"
      + "          <StateOrganizationId>East Daybreak Junior High</StateOrganizationId>\n"
      + "          <UniqueSectionCode>8th Grade English - Sec 6</UniqueSectionCode>\n"
      + "        </SectionIdentity>\n"
      + "      </SectionReference>\n"
      + "    </GradebookEntry>"
                           , result.get("GBE-8th Grade English-3"));
    }

}
