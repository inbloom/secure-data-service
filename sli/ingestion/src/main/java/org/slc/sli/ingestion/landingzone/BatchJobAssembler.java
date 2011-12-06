package org.slc.sli.ingestion.landingzone;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.processors.SubmissionLevelException;

/**
 *
 * @author jsa
 *
 */
@Component
public class BatchJobAssembler {

    /**
     *
     */
    @Autowired
    private LandingZone landingZone;

    /**
     * Attempt to generate a new BatchJob based on data found in the
     * controlFile.
     *
     * @param controlFile
     * @return BatchJob
     * @throws IOException
     */
    public BatchJob assembleJob(ControlFile controlFile) throws IOException {

        BatchJob job = BatchJob.createDefault();

        List<ControlFile.FileEntry> entries = controlFile.getFileEntries();

        // assert the control file has more than zero file items
        if (entries.size() < 1) {
            job.addFault(Fault.createError("No files specified in control file"));
        }

        for (ControlFile.FileEntry entry : entries) {

            // verify each file item can be mapped to an accessible file
            File f = landingZone.getFile(entry.fileName);
            if (f == null) {
                // file does not exist.
                job.addFault(Fault.createError("File [" + entry.fileName + "] was not found."));
                continue;
            }

            // and the attributes match
            String actualMd5Hex = landingZone.getMd5Hex(f);

            if (!actualMd5Hex.equals(entry.checksum)) {
                job.addFault(Fault.createError("File [" + entry.fileName + "] " + "checksum (" + actualMd5Hex + ") "
                        + "does not match control file " + "checksum (" + entry.checksum + ")."));
                continue;
            }

            // TODO verify proper file type
            // TODO verify proper file format

            // add the file to the job
            job.addFile(f);
        }

        // iterate over the configProperties and copy into the job
        // TODO validate config properties are legit
        Enumeration<Object> e = controlFile.configProperties.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            job.setProperty(key, controlFile.configProperties.getProperty(key));
        }

        return job;
    }

    protected File parseFileEntry(ControlFile.FileEntry fe) throws SubmissionLevelException {
        File f = landingZone.getFile(fe.fileName);

        if (f == null) {
            throw new SubmissionLevelException();
        }

        try {
            // and the attributes match
            String actualMd5Hex = landingZone.getMd5Hex(f);

            if (!actualMd5Hex.equals(fe.checksum)) {
                throw new SubmissionLevelException();
            }
        } catch (IOException e) {
            throw new SubmissionLevelException();
        }

        return f;
    }
}
