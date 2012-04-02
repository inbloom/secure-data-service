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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/validatorContext.xml" })
public class ControlFileValidationTest {

    @Test
    public void testValidateControlFileDescriptorBatchJob() {
        String dir = "test";
        String ctrFile = "MainControl.ctl";
        LocalFileSystemLandingZone lz = new LocalFileSystemLandingZone();
        lz.setDirectory(new File(dir));
        ControlFile cf = null;
        try {
            cf = ControlFile.parse(new File(dir + ctrFile));
        } catch (IOException e) {
            fail("Failed to parse control file");
        }
        ControlFileDescriptor fileDesc = new ControlFileDescriptor(cf, lz);

        ControlFileValidation cfValidation = new ControlFileValidation();
        BatchJob job = BatchJob.createDefault("MainControl.ctl");
        cfValidation.validate(fileDesc, job);
    }


}
