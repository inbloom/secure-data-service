package org.slc.sli.ingestion.landingzone;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
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
public class BatchJobAssembler implements MessageSourceAware {

    @Autowired
    private LandingZone landingZone;

    private MessageSource messageSource;

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

            try {
                File f = parseFileEntry(entry);

                job.addFile(f);
            } catch (SubmissionLevelException e) {
                job.addFault(Fault.createError(e.getMessage()));
            }
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
            fail("SL_ERR_MSG8", fe.fileName);
        }

        try {
            // and the attributes match
            String actualMd5Hex = landingZone.getMd5Hex(f);

            if (!actualMd5Hex.equals(fe.checksum)) {
                if (Log.isDebugEnabled()) {
                    Log.debug(String.format("File [%s] checksum (%s) does not match control file checksum (%s).",
                            fe.fileName, actualMd5Hex, fe.checksum));
                }

                fail("SL_ERR_MSG2", fe.fileName);
            }
        } catch (IOException e) {
            fail("SL_ERR_MSG2", fe.fileName);
        }


        // TODO verify proper file type
        // TODO verify proper file format

        return f;
    }

    public LandingZone getLandingZone() {
        return landingZone;
    }

    public void setLandingZone(LandingZone landingZone) {
        this.landingZone = landingZone;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private void fail(String code, Object... args) throws SubmissionLevelException {
        throw new SubmissionLevelException(messageSource.getMessage(code, args, "#?" + code + "?#", null));
    }
}
