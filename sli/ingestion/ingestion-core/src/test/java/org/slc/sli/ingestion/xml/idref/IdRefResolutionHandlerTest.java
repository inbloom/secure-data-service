package org.slc.sli.ingestion.xml.idref;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

            @Override
            public void execute(File file) throws Throwable {
                @SuppressWarnings("unchecked")
                Set<String> result = (Set<String>) PrivateAccessor.invoke(idRefResolutionHandler, "findIDRefsToResolve", new Class[]{File.class}, new Object[]{file});

                Assert.assertNotNull(result);
                Assert.assertEquals(1, result.size());
                Assert.assertTrue(result.contains("GBE-8th Grade English-3"));
            }
        };

        performTestOnFile(inputFile, findIdRefsToResolve);
    }

    @Test
    public void testFindMatchingEntities() throws Throwable {
        File inputFile = IngestionTest.getFile("ReferenceResolution/InterchangeStudentGrade.xml");

        TestExecutor findMatchingEntities = new TestExecutor() {

            @SuppressWarnings("unchecked")
            @Override
            public void execute(File file) throws Throwable {
                Map<String, File> result = null;

                try {
                    Set<String> refs = (Set<String>) PrivateAccessor.invoke(idRefResolutionHandler, "findIDRefsToResolve", new Class[]{File.class}, new Object[]{file});

                    result = (Map<String, File>) PrivateAccessor.invoke(idRefResolutionHandler, "findMatchingEntities", new Class[]{File.class, Set.class}, new Object[]{file, refs});

                    Assert.assertNotNull(result);
                    Assert.assertEquals(1, result.size());
                    Assert.assertTrue(result.containsKey("GBE-8th Grade English-3"));
                } finally {
                    if (result != null) {
                        for (File f : result.values()) {
                            deleteQuietly(f);
                        }
                    }
                }
            }
        };

        performTestOnFile(inputFile, findMatchingEntities);
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
        InputStream input = null;
        OutputStream output = null;

        try {
            tempInputFile = File.createTempFile("unit", ".test");

            input = new BufferedInputStream(new FileInputStream(inputFile));
            output = new BufferedOutputStream(new FileOutputStream(tempInputFile));

            IOUtils.copy(input, output);

            output.flush();

            test.execute(tempInputFile);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
            deleteQuietly(tempInputFile);
        }
    }

    private static void deleteQuietly(File file) {
        if (file != null) {
            file.delete();
        }
    }
}
