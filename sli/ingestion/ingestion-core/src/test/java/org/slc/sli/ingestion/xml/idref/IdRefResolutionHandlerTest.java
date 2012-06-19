package org.slc.sli.ingestion.xml.idref;

import java.io.File;
import java.util.Set;

import junit.framework.Assert;
import junitx.util.PrivateAccessor;

import org.apache.commons.io.FileUtils;
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

    /**
     * TestExecutor interface
     *
     * @author okrook
     *
     */
    public interface TestExecutor {
        void execute(File file) throws Throwable;
    }

    @Autowired
    private IdRefResolutionHandler idRefResolutionHandler;

    @Test
    public void testFindIDRefsToResolve() throws Throwable {
        File inputFile = IngestionTest.getFile("ReferenceResolution/InterchangeStudentGrade.xml");

        TestExecutor findIdRefsToResolve = new TestExecutor() {

            @SuppressWarnings("unchecked")
            @Override
            public void execute(File file) throws Throwable {

                ErrorReport errorReport = Mockito.mock(ErrorReport.class);
                Set<String> result = (Set<String>) PrivateAccessor.invoke(idRefResolutionHandler, "findIDRefsToResolve", new Class[]{File.class, ErrorReport.class}, new Object[]{file, errorReport});

                Assert.assertNotNull(result);
                Assert.assertEquals(1, result.size());
                Assert.assertTrue(result.contains("GBE-8th Grade English-3"));
            }
        };

        performTestOnFile(inputFile, findIdRefsToResolve);
    }



    @Test
    public void testProcess() throws Throwable {
        final File inputFile = IngestionTest.getFile("ReferenceResolution/InterchangeStudentGrade.xml");

        TestExecutor process = new TestExecutor() {

            @Override
            public void execute(File file) throws Throwable {
                ErrorReport errorReport = Mockito.mock(ErrorReport.class);
                FileProcessStatus fileProcessStatus = Mockito.mock(FileProcessStatus.class);
                String beforeHash = MD5.calculate(inputFile);
                IngestionFileEntry inputFileEntry = new IngestionFileEntry(FileFormat.EDFI_XML,
                        FileType.XML_STUDENT_GRADES, file.getName(), beforeHash);
                inputFileEntry.setFile(file);

                idRefResolutionHandler.doHandling(inputFileEntry, errorReport, fileProcessStatus);

                String afterHash = MD5.calculate(file);

                Assert.assertFalse(beforeHash.equals(afterHash));
            }
        };

        performTestOnFile(inputFile, process);
    }


    private static void performTestOnFile(File inputFile, TestExecutor test) throws Throwable {
        File tempInputFile = null;

        try {
            tempInputFile = File.createTempFile("unit", ".test");

            FileUtils.copyFile(inputFile, tempInputFile);
            test.execute(tempInputFile);

        } finally {
            FileUtils.deleteQuietly(tempInputFile);
        }
    }
}
