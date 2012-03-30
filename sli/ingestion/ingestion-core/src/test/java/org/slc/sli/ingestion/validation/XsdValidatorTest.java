package org.slc.sli.ingestion.validation;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 *
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class XsdValidatorTest {

    @Autowired
    private XsdValidator xsdValidator;

    @Test
    public void testIsValid() {
        IngestionFileEntry ife = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT, "InterchangeStudent.xml", "");
        Assert.assertFalse(xsdValidator.isValid(ife, Mockito.mock(ErrorReport.class)));
    }

}
