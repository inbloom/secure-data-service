package org.slc.sli.ingestion.referenceresolution;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Holder;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.IngestionTest;

/**
 *
 * @author tke
 *
 */
public class FreeMarkerExtendedReferenceResolverTest {
    FreeMarkerExtendedReferenceResolver referenceFactory = new FreeMarkerExtendedReferenceResolver();

    private void test(InputStream content, InputStream expected, String xpath) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            referenceFactory.resolve(xpath, content, out);

            String expectedXML = IOUtils.toString(expected);
            String actualXML = out.toString("UTF-8");

            expectedXML = expectedXML.replaceAll("\\n|\\r", "");
            expectedXML = expectedXML.replaceAll("\\s+", "");
            actualXML = actualXML.replaceAll("\\n|\\r", "");
            actualXML = actualXML.replaceAll("\\s+", "");

            Assert.assertEquals(expectedXML, actualXML);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    @Test
    public void testResolution() throws IOException, SAXException {
        final InputStream input = IngestionTest.getFileInputStream("idRefResolutionData/InterchangeStudentParent/StudentReference_input.xml");
        final InputStream expected = IngestionTest.getFileInputStream("idRefResolutionData/InterchangeStudentParent/StudentReference_output.xml");

        Map<String, String> config = new HashMap<String, String>();
        config.put("/InterchangeStudentParent/StudentParentAssociation/StudentReference", "idRefResolution/InterchangeStudentParent/StudentParentAssociation/StudentReference.ftl");

        referenceFactory.setIdRefConfigs(config);

        test(input, expected, "/InterchangeStudentParent/StudentParentAssociation/StudentReference");

        final Holder<Boolean> exceptionThrown = new Holder<Boolean>(Boolean.FALSE);

        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    test(input, expected, "/InterchangeStudentParent/StudentParentAssociation/StudentReference");
                } catch (Throwable t) {
                    exceptionThrown.value = Boolean.TRUE;
                    throw new RuntimeException(t);
                }
            }
        };

        Thread th1 = new Thread(run, "Thread1");

        Thread th2 = new Thread(run, "Thread2");

        th1.start();
        th2.start();

        try {
            th1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }

        try {
            th2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }

        Assert.assertFalse(exceptionThrown.value);
    }

    @Test
    public void testBadConfiguration() {
        Map<String, String> config = new HashMap<String, String>();
        referenceFactory.setIdRefConfigs(config);

        config.put("/InterchangeStudentAssessment/StudentAssessment/AssessmentReference", "idRefResolution/InterchangeAssessmentMetadata/Assessment/AssessmentFamilyReference.ftl");

        //Assert.assertNull(referenceFactory.resolve("/InterchangeStudentAssessment/StudentAssessment/AssessmentReference2", null));
    }

}
