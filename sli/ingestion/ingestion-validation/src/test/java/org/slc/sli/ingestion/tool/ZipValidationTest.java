package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.landingzone.validation.ZipFileValidator;

/**
 * Unit Test for ZipValidation
 *
 * @author npandey
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext.xml" })
public class ZipValidationTest {

    @Autowired
    private ZipValidation zipValidation;
    
    @Test
    public void testValidate() {
        
        ZipFileValidator zv = zipValidation.getValidator();
        Assert.assertNotNull(zv);
        
        Resource zipFileResource = new ClassPathResource("Session1.zip");
        File zipFile = null;
        
        try {
            zipFile = zipFileResource.getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        BatchJob job = BatchJob.createDefault("Test.zip");
        File ctlFile = zipValidation.validate(zipFile, job);
        Assert.assertNotNull(ctlFile);
    }
    
    @Test
    public void testInValidZip() {
        Resource zipFileResource = new ClassPathResource("SessionInValid.zip");
        File zipFile = null;
        
        try {
            zipFile = zipFileResource.getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        BatchJob job = BatchJob.createDefault("Test.zip");
        File ctlFile = zipValidation.validate(zipFile, job);
        Assert.assertNull(ctlFile);
    }
    
    @Test
    public void testExceptionHandling() {
        BatchJob job = BatchJob.createDefault("Test.zip");
        File ctlFile = zipValidation.validate(null, job);
        Assert.assertNull(ctlFile);
    }
}
