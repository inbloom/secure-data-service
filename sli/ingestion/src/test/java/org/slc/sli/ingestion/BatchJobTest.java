package org.slc.sli.ingestion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.util.MD5;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/spring/applicationContext-test.xml" })
public class BatchJobTest {
    
    @Autowired
    private LandingZone lz;

    @Test
    public void testCreateId() {
        String id1 = BatchJob.createId();
        String id2 = BatchJob.createId();
        assertEquals(id1.length(), 36);
        assertEquals(id2.length(), 36);
        assertFalse(id1.equals(id2));
    }
    
    @Test
    public void testProperties() {
        
        BatchJob job = BatchJob.createDefault();
        
        Enumeration<?> names = job.propertyNames();
        int c = 0;
        while (names.hasMoreElements()) {
            names.nextElement();
            c++;
        }
        assertEquals(c, 0);
        
        assertNull(job.getProperty("hello"));
        assertEquals("world", job.getProperty("hello", "world"));
        
        job.setProperty("hello", "dolly");
        
        assertEquals("dolly", job.getProperty("hello"));
        assertEquals("dolly", job.getProperty("hello", "world"));
    }
    
    @Test
    public void testFiles() {
        BatchJob job = BatchJob.createDefault();
        assertEquals(0, job.getFiles().size());
        IngestionFileEntry entry = new IngestionFileEntry(FileFormat.EDFI_XML, FileType.XML_STUDENT,
                "InterchangeStudent.xml",MD5.calculate("InterchangeStudent.xml", getLandingZone()));
        job.addFile(entry);
        assertEquals(1, job.getFiles().size());
    }
    
    @Test
    public void testFaults() {
        BatchJob job = BatchJob.createDefault();
        assertEquals(0, job.getFaults().size());
        job.addFault(Fault.createWarning("this is a warning"));
        assertEquals(1, job.getFaults().size());
        assertFalse(job.hasErrors());
        job.addFault(Fault.createError("this is an error"));
        assertEquals(2, job.getFaults().size());
        assertTrue(job.hasErrors());
    }
    
    @Test
    public void testCreateDefault() throws InterruptedException {
        
        // generate dates before and after the BatchJob is instantiated,
        // so we can verify its creationDate is accurate.
        Date now = new Date();
        BatchJob job = BatchJob.createDefault();
        
        System.out.println(System.getProperty("java.class.path"));
        
        String id = job.getId();
        assertEquals(id.length(), 36);
        
        Date jobDate = job.getCreationDate();
        assertTrue(jobDate.after(new Date(now.getTime() - 1)));
        assertTrue(jobDate.before(new Date(now.getTime() + 10)));
        
        ArrayList<IngestionFileEntry> files = (ArrayList<IngestionFileEntry>) job.getFiles();
        assertEquals(files.size(), 0);
        
        ArrayList<Fault> faults = (ArrayList<Fault>) job.getFaults();
        assertEquals(faults.size(), 0);
        
    }
    
    public LandingZone getLandingZone() {
        return lz;
    }

    public void setLandingZone(LandingZone landingZone) {
        this.lz = landingZone;
    }

}
