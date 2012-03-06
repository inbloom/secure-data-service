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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.smooks.SliSmooksFactory;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * smooks handler for edfi files
 *
 * @author dduran
 *
 */
@Component
public class SmooksFileHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(SmooksFileHandler.class);

    private SliSmooksFactory sliSmooksFactory;

    @Value("${landingzone.inbounddir}")
    private String lzDirectory;

    @Override
    IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport) {
        try {

            generateNeutralRecord(fileEntry, errorReport);

        } catch (IOException e) {
            LOG.error("IOException", e);
            errorReport.fatal("Could not instantiate smooks, unable to read configuration file.",
                    SmooksFileHandler.class);
        } catch (SAXException e) {
            errorReport.fatal("Could not instantiate smooks, problem parsing configuration file.",
                    SmooksFileHandler.class);
        }

        return fileEntry;
    }

    void generateNeutralRecord(IngestionFileEntry ingestionFileEntry, ErrorReport errorReport) throws IOException,
            SAXException {

        File neutralRecordOutFile = createTempFile();

        NeutralRecordFileWriter nrFileWriter = new NeutralRecordFileWriter(neutralRecordOutFile);

        // create instance of Smooks (with visitors already added)
        Smooks smooks = sliSmooksFactory.createInstance(ingestionFileEntry, nrFileWriter, errorReport);

        InputStream inputStream = new BufferedInputStream(new FileInputStream(ingestionFileEntry.getFile()));
        try {

            // filter fileEntry inputStream, converting into NeutralRecord entries as we go
            smooks.filterSource(new StreamSource(inputStream));

            // set the IngestionFileEntry NeutralRecord file we just wrote
            ingestionFileEntry.setNeutralRecordFile(neutralRecordOutFile);

        } catch (SmooksException se) {
            LOG.error("smooks exception encountered:\n" + Arrays.toString(se.getStackTrace()));
            errorReport.error("SmooksException encountered while filtering input.", SmooksFileHandler.class);
        } finally {
            IOUtils.closeQuietly(inputStream);
            nrFileWriter.close();
        }
    }

    private File createTempFile() throws IOException {
        File landingZone = new File(lzDirectory);
        File outputFile = landingZone.exists() ? File.createTempFile("camel_", ".tmp", landingZone) : File
                .createTempFile("camel_", ".tmp");
        return outputFile;
    }

    public void setSliSmooksFactory(SliSmooksFactory sliSmooksFactory) {
        this.sliSmooksFactory = sliSmooksFactory;
    }

}
