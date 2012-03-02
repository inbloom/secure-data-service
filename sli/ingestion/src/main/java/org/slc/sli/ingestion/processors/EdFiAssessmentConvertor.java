package org.slc.sli.ingestion.processors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileReader;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.util.FileUtils;

/**
 * Class used to convert edfi assessments from the metadata interchange into their SLI counterparts
 * 
 * @author nbrown
 * 
 */
@Component
public class EdFiAssessmentConvertor {
    private static final Logger LOG = LoggerFactory.getLogger(EdFiAssessmentConvertor.class);
    private final FileUtils fileUtils;
    
    @Autowired
    public EdFiAssessmentConvertor(FileUtils fileUtils) {
        super();
        this.fileUtils = fileUtils;
    }
    
    public void doConversion(IngestionFileEntry fileEntry) throws IOException {
        LOG.debug("Converting ingested ed fi file {}", fileEntry);
        NeutralRecordFileReader reader = new NeutralRecordFileReader(fileEntry.getNeutralRecordFile());
        List<NeutralRecord> edfiRecords = new ArrayList<NeutralRecord>();
        try {
            while (reader.hasNext()) {
                edfiRecords.add(reader.next());
            }
        } finally {
            reader.close();
        }
        List<NeutralRecord> sliRecords = convert(edfiRecords);
        File tempFile = fileUtils.createTempFile();
        NeutralRecordFileWriter writer = new NeutralRecordFileWriter(tempFile);
        try {
            for (NeutralRecord record : sliRecords) {
                writer.writeRecord(record);
            }
        } finally {
            writer.close();
        }
        File oldNRFile = fileEntry.getNeutralRecordFile();
        fileEntry.setNeutralRecordFile(tempFile);
        oldNRFile.delete();
    }
    
    /**
     * Merge together an input of edfi assessments into sli assessments
     * 
     * @param orig
     *            the set of edfi assessments
     * @return the corresponding set of sli assessments
     */
    protected List<NeutralRecord> convert(List<NeutralRecord> orig) {
        return orig;
    }
    
}
