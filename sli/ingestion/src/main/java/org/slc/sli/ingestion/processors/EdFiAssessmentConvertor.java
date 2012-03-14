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
    private static final String PERIOD_CODE_VALUE = "codeValue";
    private static final String PERIOD_DESCRIPTOR_REF = "periodDescriptorRef";
    private static final String ASSESSMENT_FAMILY_IDENTIFICATION_CODE = "AssessmentFamilyIdentificationCode";
    private static final String PARENT_ASSESSMENT_FAMILY_ID = "parentAssessmentFamilyId";
    private static final String OBJ_ASSESSMENT_ID_FIELD = "id";
    private static final String ASSESSMENT_FAMILY_TITLE = "AssessmentFamilyTitle";
    private static final String ASSESSMENT = "assessment";
    private static final String ASSESSMENT_FAMILY = "AssessmentFamily";
    private static final String ASSESSMENT_PERIOD_DESCRIPTOR = "assessmentPeriodDescriptor";
    private static final String OBJECTIVE_ASSESSMENT = "objectiveAssessment";
    private static final String OBJECTIVE_ASSESSMENT_REFS = "objectiveAssessmentRefs";
    private static final Logger LOG = LoggerFactory.getLogger(EdFiAssessmentConvertor.class);
    private final FileUtils fileUtils;
    private final List<String> inspectRecordTypes = Arrays.asList(ASSESSMENT, ASSESSMENT_FAMILY,
            ASSESSMENT_PERIOD_DESCRIPTOR, OBJECTIVE_ASSESSMENT);
    
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
        Map<Object, NeutralRecord> assessmentPeriodDescriptors = getMapByField(orig.get(ASSESSMENT_PERIOD_DESCRIPTOR),
                PERIOD_CODE_VALUE);
        Map<Object, NeutralRecord> objectiveAssessments = getMapByField(orig.get(OBJECTIVE_ASSESSMENT), OBJ_ASSESSMENT_ID_FIELD);
        LOG.debug("Assessment family map is {}", assessmentFamilies);
        List<NeutralRecord> assessments = orig.get(ASSESSMENT);
        for (NeutralRecord record : assessments) {
            LOG.debug("converting assessment {}", record);
            List<String> familyHierarchy = resolveFamily(record, assessmentFamilies, new HashSet<Object>());
            record.setAttributeField("assessmentFamilyHierarchyName", StringUtils.join(familyHierarchy, "."));
            record.getAttributes().remove(PARENT_ASSESSMENT_FAMILY_ID);
            Object periodRef = record.getAttributes().get(PERIOD_DESCRIPTOR_REF);
            if (periodRef != null) {
                Map<String, Object> periodDescriptor = assessmentPeriodDescriptors.get(periodRef).getAttributes();
                record.setAttributeField(ASSESSMENT_PERIOD_DESCRIPTOR, periodDescriptor);
            }
            record.getAttributes().remove(PERIOD_DESCRIPTOR_REF);
            addObjectiveAssessments(record, objectiveAssessments);
        }
        return assessments;
    }

    private void addObjectiveAssessments(NeutralRecord record, Map<Object, NeutralRecord> objectiveAssessments) {
        List<?> objectiveRefs = (List<?>) record.getAttributes().get(OBJECTIVE_ASSESSMENT_REFS);
        if(objectiveRefs == null || objectiveRefs.isEmpty()){
            return;
        }
        List<NeutralRecord> objAssmtsForAssmt = new ArrayList<NeutralRecord>(objectiveRefs.size());
        for(Object ref: objectiveRefs){
            objAssmtsForAssmt.add(objectiveAssessments.get(ref));
        }
        record.setAttributeField(OBJECTIVE_ASSESSMENT, objAssmtsForAssmt);
        record.getAttributes().remove(OBJECTIVE_ASSESSMENT_REFS);
    }
    
    private Map<Object, NeutralRecord> getAssessmentFamilyMap(List<NeutralRecord> records) {
        // instance #1754 I wish Java had function literals...
        Map<Object, NeutralRecord> assessmentFamilies = new HashMap<Object, NeutralRecord>();
        for (NeutralRecord record : records) {
            @SuppressWarnings("unchecked")
            List<Map<?, ?>> identities = (List<Map<?, ?>>) record.getAttributes().get(
                    ASSESSMENT_FAMILY_IDENTIFICATION_CODE);
            for (Map<?, ?> identity : identities) {
                if (identity != null) {
                    assessmentFamilies.put(identity.get("ID"), record);
                }
            }
        }
        return assessmentFamilies;
    }
    
    private Map<Object, NeutralRecord> getMapByField(List<NeutralRecord> records, String field) {
        // instance #1755 I wish Java had function literals...
        Map<Object, NeutralRecord> map = new HashMap<Object, NeutralRecord>();
        for (NeutralRecord record : records) {
            map.put(record.getAttributes().get(field), record);
        }
        return map;
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
