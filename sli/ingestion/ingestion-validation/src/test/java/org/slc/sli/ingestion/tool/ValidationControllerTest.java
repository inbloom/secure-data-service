package org.slc.sli.ingestion.tool;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit Tests for ValidationController
 *
 * @author tke
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext.xml" })
public class ValidationControllerTest {

    final String controlFileName = "test/MainControlFile.ctl";
    final String zipFileName = "zipFile/Session1.zip";

    @Autowired
    private ValidationController validationController;

    /**
     * Test zip file validation
     */
    @Test
    public void testDoValidationZipFile() {
        Resource zipFileResource = new ClassPathResource(zipFileName);
        try {
           validationController.doValidation(zipFileResource.getFile());
        } catch (IOException e) {
            fail("IO exception");
        }
        //TODO: add assert
    }

    /**
     * Test invalid zip file
     */
    @Test
    public void testDovalidationInvalidZip() {
        Resource invalidFileResource = new ClassPathResource("invalidZip/SessionInValid.zip");
        try {
           validationController.doValidation(invalidFileResource.getFile());
        } catch (IOException e) {
            fail("IO exception");
        }
        //TODO: add assert
    }

    /**
     * Test situation when there are multiple zip and control files
     * in the same folder
     */

    @Test
    public void testDovalidationInvalid() {
        Resource invalidFileResource = new ClassPathResource("invalid/Session1.zip");
        try {
           validationController.doValidation(invalidFileResource.getFile());
        } catch (IOException e) {
            fail("IO exception");
        }
        //TODO: add assert
    }

    /**
     * Test control file validation
     */
    @Test
    public void testDoValidationCtrFile() {
        Resource ctlFileResource = new ClassPathResource(controlFileName);
        try {
            validationController.doValidation(ctlFileResource.getFile());
        } catch (IOException e) {
            fail("IO exception");
        }
        //TODO: add assert
    }

}