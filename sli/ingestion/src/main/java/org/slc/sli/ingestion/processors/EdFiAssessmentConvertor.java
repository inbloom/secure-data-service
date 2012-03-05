package org.slc.sli.ingestion.processors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
    private static final String PARENT_ASSESSMENT_FAMILY_ID = "parentAssessmentFamilyId";
    private static final String ASSESSMENT_FAMILY_TITLE = "AssessmentFamilyTitle";
    private static final String ASSESSMENT = "assessment";
    private static final String ASSESSMENT_FAMILY = "AssessmentFamily";
    private static final Logger LOG = LoggerFactory.getLogger(EdFiAssessmentConvertor.class);
    private final FileUtils fileUtils;
    private final List<String> inspectRecordTypes = Arrays.asList(ASSESSMENT, ASSESSMENT_FAMILY);
    
    @Autowired
    public EdFiAssessmentConvertor(FileUtils fileUtils) {
        super();
        this.fileUtils = fileUtils;
    }
    
    public void doConversion(IngestionFileEntry fileEntry) throws IOException {
        LOG.debug("Converting ingested ed fi file {}", fileEntry);
        NeutralRecordFileReader reader = new NeutralRecordFileReader(fileEntry.getNeutralRecordFile());
        Map<String, List<NeutralRecord>> edfiRecords = new HashMap<String, List<NeutralRecord>>();
        for (String type : inspectRecordTypes) {
            edfiRecords.put(type, new ArrayList<NeutralRecord>());
        }
        try {
            while (reader.hasNext()) {
                NeutralRecord record = reader.next();
                LOG.debug("Recieved neutral record {}", record);
                if (edfiRecords.keySet().contains(record.getRecordType())) {
                    edfiRecords.get(record.getRecordType()).add(record);
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
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
    protected List<NeutralRecord> convert(Map<String, List<NeutralRecord>> orig) {
        Map<Object, NeutralRecord> assessmentFamilies = getAssessmentFamilyMap(orig.get(ASSESSMENT_FAMILY));
        LOG.debug("Assessment family map is {}", assessmentFamilies);
        List<NeutralRecord> assessments = orig.get(ASSESSMENT);
        for (NeutralRecord record : assessments) {
            LOG.debug("converting assessment {}", record);
            List<String> familyHierarchy = resolveFamily(record, assessmentFamilies, new HashSet<Object>());
            record.setAttributeField("assessmentFamilyHierarchyName", StringUtils.join(familyHierarchy, "."));
            record.getAttributes().remove(PARENT_ASSESSMENT_FAMILY_ID);
        }
        return assessments;
    }
    
    private Map<Object, NeutralRecord> getAssessmentFamilyMap(List<NeutralRecord> familyRecords) {
        // instance #1754 I wish Java had function literals...
        Map<Object, NeutralRecord> assessmentFamilies = new HashMap<Object, NeutralRecord>();
        for (NeutralRecord record : familyRecords) {
            assessmentFamilies.put(record.getAttributes().get("id"), record);
        }
        return assessmentFamilies;
    }
    
    private List<String> resolveFamily(NeutralRecord rec, Map<Object, NeutralRecord> families, Set<Object> visited) {
        Object familyId = rec.getAttributes().get(PARENT_ASSESSMENT_FAMILY_ID);
        NeutralRecord family = families.get(familyId);
        List<String> hierarchy = new ArrayList<String>();
        if (family != null && !visited.contains(family)) {
            visited.add(family);
            hierarchy = resolveFamily(family, families, visited);
            hierarchy.add(family.getAttributes().get(ASSESSMENT_FAMILY_TITLE).toString());
        }
        return hierarchy;
    }
}
