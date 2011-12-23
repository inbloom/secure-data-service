package org.slc.sli.ingestion.landingzone.handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.smooks.SmooksEdFiVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * handler for xml files
 *
 * @author dduran
 *
 */
public class XmlFileHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(XmlFileHandler.class);

    private String smooksConfigFileName;

    private Map<FileType, List<String>> targetSelectorMap;

    @Override
    IngestionFileEntry doHandling(IngestionFileEntry item) {
        try {
            handleXmlFile(item);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return item;
    }

    void handleXmlFile(IngestionFileEntry fileEntry) throws IOException, SAXException {

        // Create Ingestion XML input stream
        InputStream inputStream = new BufferedInputStream(new FileInputStream(fileEntry.getFile()));

        // Create Ingestion Neutral record writer
        File outputFile = File.createTempFile("camel_", ".tmp");
        outputFile.deleteOnExit();
        NeutralRecordFileWriter fileWriter = new NeutralRecordFileWriter(outputFile);

        Smooks smooks = initSmooks(fileEntry.getFileType(), fileWriter);

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

    }

    private Smooks initSmooks(FileType fileType, NeutralRecordFileWriter fileWriter) throws IOException, SAXException {

        List<String> targetSelectorList = targetSelectorMap.get(fileType);
        if (targetSelectorList != null) {

            Smooks smooks = new Smooks(smooksConfigFileName);

            for (String targetSelector : targetSelectorList) {
                smooks.addVisitor(new SmooksEdFiVisitor("record", fileWriter), targetSelector);
            }

            return smooks;
        } else {
            throw new IllegalArgumentException("File type not supported: " + fileType);
        }

    }

    public void setSmooksConfigFileName(String smooksConfigFileName) {
        this.smooksConfigFileName = smooksConfigFileName;
    }

    public void setTargetSelectorMap(Map<FileType, List<String>> targetSelectorMap) {
        this.targetSelectorMap = targetSelectorMap;
    }

}
