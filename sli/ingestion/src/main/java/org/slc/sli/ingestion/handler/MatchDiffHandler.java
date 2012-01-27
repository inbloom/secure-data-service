package org.slc.sli.ingestion.handler;

import java.io.File;
import java.io.IOException;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ccheng
 *
 */
@Component
public class MatchDiffHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(MatchDiffHandler.class);

    @Autowired
    LocalFileSystemLandingZone lz;

    private File newRecordFile;
    private File currentRecordFile;

    @Override
    IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport) {
        try {
            // load new and current state files to HDFS
            loadFiles(fileEntry, errorReport);

            // compute diff between new and current files
            computeDelta(fileEntry, errorReport);

        } catch (IOException e) {
            LOG.error("Failed to load files to HDFS.");
            errorReport.fatal("Failed to load files to HDFS.", MatchDiffHandler.class);
        }

        return fileEntry;
    }

    void loadFiles(IngestionFileEntry fileEntry, ErrorReport errorReport) throws IOException {

        // load new files from user
        newRecordFile = fileEntry.getNeutralRecordFile();
        // TODO: switch to HDFS as follows:
        // HDFSUtil.addFile(newRecordFile, "/");
        // temporarily using local landing zone
        lz.loadFile(newRecordFile);

        // load current state files
        currentRecordFile = getCurrentRecordFile(fileEntry, errorReport);
        // TODO: switch to HDFS as follows:
        // HDFSUtil.addFile(currentRecordFile, "/");
        // temporarily using local landing zone
        lz.loadFile(currentRecordFile);

    }

    void computeDelta(IngestionFileEntry fileEntry, ErrorReport errorReport) throws IOException {

        // TODO: compute delta between new and current state records
        File deltaFile = null;

        deltaFile = computeDeltaImp(newRecordFile, currentRecordFile);

        // write delta to deltaNeutralRecordFile
        fileEntry.setDeltaNeutralRecordFile(deltaFile);

    }

    private File getCurrentRecordFile(IngestionFileEntry fileEntry, ErrorReport errorReport) throws IOException {

        // TODO: fetch current state records
        // temporarily return empty file
        File file = File.createTempFile("currentRecord_", ".tmp");
        file.deleteOnExit();
        return file;
    }

    private File computeDeltaImp(File newRecordFile, File currentRecordFile) {

        // TODO: right now the currentRecordFile is empty, so the delta file is the same as the newRecordFile
        return newRecordFile;
    }
}
