package org.slc.sli.ingestion;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import ch.qos.logback.classic.Logger;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.model.NewBatchJob;

/**
 * Unit tests for BatchJobLogger
 *
 * @author ifaybyshev
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/spring/applicationContext-test.xml" })
public class BatchJobLoggerTest {

    public static final String DUMMY_DIR = FilenameUtils.normalize("src/test/resources/dummylz");
    public static final String DUMMY_CONTROL_FILE = FilenameUtils.normalize("testControlFile.ctl");
    
    @Test
    public void testLogger() {
        NewBatchJob job = new NewBatchJob(NewBatchJob.createId(DUMMY_CONTROL_FILE));
        
        LocalFileSystemLandingZone lz = new LocalFileSystemLandingZone();
        lz.setDirectory(new File(DUMMY_DIR));
        
        try {
            Logger logger = BatchJobLogger.createLoggerForJob(job, lz);
            assertTrue("xxx", logger.getName().endsWith(job.getId()));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
