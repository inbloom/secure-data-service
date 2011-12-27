package org.slc.sli.ingestion.landingzone.handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.smooks.SmooksEdFiVisitor;
import org.slc.sli.ingestion.validation.ErrorReport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * handler for csv files
 *
 * @author dduran
 *
 */
public class CsvFileHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(CsvFileHandler.class);

    private String targetSelector;

    private Map<FileType, String> smooksConfigFileNameMap;

    @Override
    IngestionFileEntry doHandling(IngestionFileEntry item, ErrorReport vr) {

        try {
            handleCsvFile(item);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return item;

    }

    void handleCsvFile(IngestionFileEntry fileEntry) throws IOException, SAXException {
        String smooksConfigFileName = smooksConfigFileNameMap.get(fileEntry.getFileType());
        if (smooksConfigFileName != null) {
            // Create Ingestion XML input stream
            InputStream inputStream = new BufferedInputStream(new FileInputStream(fileEntry.getFile()));

            // Create Ingestion Neutral record writer
            File outputFile = File.createTempFile("camel_", ".tmp");
            outputFile.deleteOnExit();
            NeutralRecordFileWriter fileWriter = new NeutralRecordFileWriter(outputFile);

            // Create and configure smooks instance
            Smooks smooks = new Smooks(smooksConfigFileName);
            smooks.addVisitor(new SmooksEdFiVisitor("record", fileWriter), targetSelector);

            try {

                // convert XML into Ingestion Neutral record instances
                smooks.filterSource(new StreamSource(inputStream));

                // set the IngestionFileEntry NeutralRecord file we just wrote
                fileEntry.setNeutralRecordFile(outputFile);

            } catch (SmooksException smooksException) {
                LOG.error("smooks exception encountered " + smooksException);
            } finally {
                IOUtils.closeQuietly(inputStream);
                fileWriter.close();
            }
        } else {
            throw new IllegalArgumentException("File type not supported: " + fileEntry.getFileType());
        }

    }

    public void setTargetSelector(String targetSelector) {
        this.targetSelector = targetSelector;
    }

    public void setSmooksConfigFileNameMap(Map<FileType, String> smooksConfigFileNameMap) {
        this.smooksConfigFileNameMap = smooksConfigFileNameMap;
    }

}
