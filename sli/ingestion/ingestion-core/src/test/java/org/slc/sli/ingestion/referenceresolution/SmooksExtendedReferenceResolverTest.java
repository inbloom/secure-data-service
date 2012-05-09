package org.slc.sli.ingestion.referenceresolution;

import junit.framework.Assert;

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
    public void test() {
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

        String resultXML = "<StudentSectionAssociationReference>"
                            + "<StudentSectionAssociationIdentity>"
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
                             + "</StudentSectionAssociationIdentity>"
                          + "</StudentSectionAssociationReference>";


        String result = referenceFactory.resolve("InterchangeStudentGrade", "StudentGradebookEntry", "StudentSectionAssociationReference", testXML);

        result = result.replaceAll("\\n", "");
        result = result.replaceAll("\\s+", "");

        Assert.assertEquals(resultXML, result);
    }

}
