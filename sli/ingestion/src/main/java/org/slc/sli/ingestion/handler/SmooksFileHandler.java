package org.slc.sli.ingestion.handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.smooks.SliSmooksFactory;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * smooks handler for edfi files
 *
 * @author dduran
 *
 */
public class SmooksFileHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(SmooksFileHandler.class);

    @Override
    IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport) {
        try {

            generateNeutralRecord(fileEntry, errorReport);

        } catch (IOException e) {
            errorReport.fatal("Could not instantiate smooks: " + Arrays.toString(e.getStackTrace()),
                    SmooksFileHandler.class);
        } catch (SAXException e) {
            errorReport.fatal("Could not instantiate smooks: " + Arrays.toString(e.getStackTrace()),
                    SmooksFileHandler.class);
        }

        return fileEntry;
    }

    void generateNeutralRecord(IngestionFileEntry fileEntry, ErrorReport errorReport) throws IOException, SAXException {

        File neutralRecordOutFile = createTempFile();

        NeutralRecordFileWriter nrFileWriter = new NeutralRecordFileWriter(neutralRecordOutFile);

        // create instance of Smooks (with visitors already added)
        Smooks smooks = SliSmooksFactory.createInstance(fileEntry.getFileType(), nrFileWriter);

        InputStream inputStream = new BufferedInputStream(new FileInputStream(fileEntry.getFile()));
        try {

            // filter fileEntry inputStream, converting into NeutralRecord entries as we go
            smooks.filterSource(new StreamSource(inputStream));

            // set the IngestionFileEntry NeutralRecord file we just wrote
            fileEntry.setNeutralRecordFile(neutralRecordOutFile);

        } catch (SmooksException se) {
            LOG.error("smooks exception encountered " + se);
            errorReport.error(
                    "SmooksException encountered while filtering input: " + Arrays.toString(se.getStackTrace()),
                    SmooksFileHandler.class);
        } finally {
            IOUtils.closeQuietly(inputStream);
            nrFileWriter.close();
        }
    }

    private File createTempFile() throws IOException {
        File outputFile = File.createTempFile("camel_", ".tmp");
        outputFile.deleteOnExit();
        return outputFile;
    }

}
