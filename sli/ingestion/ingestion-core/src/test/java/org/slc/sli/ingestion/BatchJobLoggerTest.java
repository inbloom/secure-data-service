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
    
    @Test
    public void testLogger() {
        BatchJob job = BatchJob.createDefault();
        
        LocalFileSystemLandingZone lz = new LocalFileSystemLandingZone();
        lz.setDirectory(new File(DUMMY_DIR));
        
        try {
            Logger logger = BatchJobLogger.createLoggerForJob(job.getId(), lz);
            assertTrue("xxx", logger.getName().endsWith(job.getId()));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
