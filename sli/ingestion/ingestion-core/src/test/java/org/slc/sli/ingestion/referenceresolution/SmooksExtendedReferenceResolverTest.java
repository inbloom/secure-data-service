package org.slc.sli.ingestion.referenceresolution;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author tke
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SmooksExtendedReferenceResolverTest {

    @Autowired
    SmooksExtendedReferenceResolver referenceFactory;
    @Test
    public void test() throws IOException {
        String testXML = "<StudentSectionAssociation>"
                            + "<StudentReference>"
                                + "<StudentIdentity>"
                                    + "<StudentUniqueStateId>"
                                        + "testing"
                                  + "</StudentUniqueStateId>"
                                  + "</StudentIdentity>"
                                + "</StudentReference>"
                                + "<SectionReference>"
                                    + "<SectionIdentity>"
                                        + "<UniqueSectionCode>"
                                            + "testing"
                                        + "</UniqueSectionCode>"
                                     + "</SectionIdentity>"
                                + "</SectionReference>"
                                + "</StudentSectionAssociation>";

        String expectedXML = "<StudentSectionAssociationIdentity>"
                                + "<StudentIdentity>"
                                    + "<StudentUniqueStateId>"
                                        + "testing"
                                    + "</StudentUniqueStateId>"
                                + "</StudentIdentity>"
                                + "<SectionIdentity>"
                                    + "<UniqueSectionCode>"
                                        + "testing"
                                    + "</UniqueSectionCode>"
                                + "</SectionIdentity>"
                             + "</StudentSectionAssociationIdentity>";


        File content = null;
        File result = null;

        try {
            content = createFile(testXML);

            result = referenceFactory.resolve("/InterchangeStudentGrade/StudentGradebookEntry/StudentSectionAssociationReference", content);

            String actualXML = readFromFile(result);

            actualXML = actualXML.replaceAll("\\n", "");
            actualXML = actualXML.replaceAll("\\s+", "");

            Assert.assertEquals(expectedXML, actualXML);
        } finally {
            if (content != null) {
                content.delete();
            }

            if (result != null) {
                result.delete();
            }
        }
    }

    private File createFile(String content) throws IOException {
        File contentFile = File.createTempFile("test", ".xml");

        FileWriter writer = null;
        try {
            writer = new FileWriter(contentFile);

            IOUtils.write(content, writer);
        } finally {
            IOUtils.closeQuietly(writer);
        }

        return contentFile;
    }

    private String readFromFile(File file) throws IOException {
        FileReader reader = null;

        try {
            reader = new FileReader(file);

            List<String> lines = IOUtils.readLines(reader);

            return StringUtils.join(lines, '\n');
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

}
