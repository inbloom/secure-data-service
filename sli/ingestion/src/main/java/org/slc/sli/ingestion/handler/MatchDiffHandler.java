package org.slc.sli.ingestion.handler;

import java.io.File;
import java.io.IOException;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.hdfs.HDFSUtil;

/**
 *
 * @author ccheng
 *
 */
@Component
public class MatchDiffHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(MatchDiffHandler.class);

    @Override
    IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport) {
        try {
            // load new and current state files to HDFS
            loadFiles(fileEntry, errorReport);

            // compute diff between new and current files
            computeDelta(fileEntry, errorReport);

        } catch (IOException e) {
            errorReport.fatal("Failed to load files to HDFS. ", MatchDiffHandler.class);
        }

        return fileEntry;
    }

    void loadFiles(IngestionFileEntry fileEntry, ErrorReport errorReport) throws IOException {

        // load new files from user
        File newRecordFile = fileEntry.getNeutralRecordFile();
        HDFSUtil.addFile(newRecordFile, "/");

        // load current state files
        File currentRecordFile = getCurrentRecordFile(fileEntry, errorReport);
        HDFSUtil.addFile(currentRecordFile, "/");
    }

    void computeDelta(IngestionFileEntry fileEntry, ErrorReport errorReport) {

        // TODO: compute delta between new and current state records
        File deltaFile = null;

        // write delta to deltaNeutralRecordFile
        fileEntry.setDeltaNeutralRecordFile(deltaFile);

    }

    File getCurrentRecordFile(IngestionFileEntry fileEntry, ErrorReport errorReport) {

        // TODO: fetch current state records
        File file = null;
        return file;
    }
}
