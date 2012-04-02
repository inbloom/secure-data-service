package org.slc.sli.ingestion.tool;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/validatorContext.xml" })
public class ControlFileValidationTest {
    @Autowired
    ControlFileValidation cfValidation;
    @Test
    public void testValidateControlFileDescriptorBatchJob() {
        Resource ctrFile = new ClassPathResource("test/MainControlFile.ctl");
        String dir = "resources/test";
        LocalFileSystemLandingZone lz = new LocalFileSystemLandingZone();
        lz.setDirectory(new File(dir));
        ControlFile cf = null;
        try {
            cf = ControlFile.parse(ctrFile.getFile());
        } catch (IOException e) {
            fail("Failed to parse control file");
        }
        ControlFileDescriptor fileDesc = new ControlFileDescriptor(cf, lz);

        BatchJob job = null;
        try {
            job = BatchJob.createDefault(ctrFile.getFile().getPath());
        } catch (IOException e) {
            fail("Failed to create job");
        }
        cfValidation.validate(fileDesc, job);
    }


}
