package org.slc.sli.ingestion;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import org.junit.Test;

public class BatchJobTest {

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
        job.addFile(new File("src/test/resources"));
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
        Date date1 = new Date();
        Thread.sleep(1);

        BatchJob job = BatchJob.createDefault();

        Thread.sleep(1);
        Date date2 = new Date();

        String id = job.getId();
        assertEquals(id.length(), 36);

        Date jobDate = job.getCreationDate();
        assertTrue(jobDate.after(date1));
        assertTrue(jobDate.before(date2));

        ArrayList<File> files = (ArrayList<File>) job.getFiles();
        assertEquals(files.size(), 0);

        ArrayList<Fault> faults = (ArrayList<Fault>) job.getFaults();
        assertEquals(faults.size(), 0);

    }

}
