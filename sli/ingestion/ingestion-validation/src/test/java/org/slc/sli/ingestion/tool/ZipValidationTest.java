package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.FaultsReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext.xml" })
public class ZipValidationTest {

    @Autowired
    private ZipValidation zipValidation;
    @Test
    public void testValidate() throws IOException {
        Resource zipFileResource = new ClassPathResource("Session1.zip");
        File zipFile = zipFileResource.getFile();
        BatchJob job = BatchJob.createDefault("Test.zip");
        FaultsReport fr = Mockito.mock(FaultsReport.class);
        File ctlFile = zipValidation.validate(zipFile, job);
        Assert.assertNotNull(ctlFile);
    }

}
