package org.slc.sli.ingestion.transformation.assessment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordFileReader;
import org.slc.sli.ingestion.NeutralRecordFileWriter;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.transformation.AbstractTransformationStrategy;
import org.slc.sli.ingestion.util.FileUtils;
import org.slc.sli.ingestion.util.LogUtil;

/**
 * Transformer for Assessment Entities
 *
 * @author ifaybyshev
 *
 */
@Component("assessmentTransformationStrategy")
public class AssessmentCombiner extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(AssessmentCombiner.class);

    private Map<String, Map<Object, NeutralRecord>> collections;

    private Map<Object, NeutralRecord> transformedAssessments = new HashMap<Object, NeutralRecord>();

    private final FileUtils fileUtils;

    private static final Set<String> IGNORE_TYPES = new HashSet<String>(Arrays.asList("learningStandard",
            "learningObjective"));

    @Autowired
    public AssessmentCombiner(FileUtils fileUtils) {
        this.collections = new HashMap<String, Map<Object, NeutralRecord>>();
        this.fileUtils = fileUtils;
    }

    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go"
     *
     */
    @Override
    public void performTransformation() {
        loadData();
        transform();
        persist();
    }

    public void loadData() {
        LOG.info("Loading data for transformation.");

        loadCollectionFromDb("assessment");
        LOG.info("Assessment is loaded into local storage.  Total Count = " + collections.get("assessment").size());

        loadCollectionFromDb("assessmentFamily");
        LOG.info("AssessmentFamily is loaded into local storage.  Total Count = "
                + collections.get("assessmentFamily").size());
    }

    @SuppressWarnings("unchecked")
    public void transform() {
        LOG.debug("Transforming data: Injecting assessmentFamilies into assessment");

        ObjectiveAssessmentBuilder objAssmtBuilder = new ObjectiveAssessmentBuilder(getNeutralRecordMongoAccess(),
                getJob().getId());

        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.get("assessment").entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();

            // get the key of parent
            Map<String, Object> attrs = neutralRecord.getAttributes();
            String parentFamilyId = (String) attrs.get("parentAssessmentFamilyId");
            attrs.remove("parentAssessmentFamilyId");
            String familyHierarchyName = "";
            familyHierarchyName = getAssocationFamilyMap(parentFamilyId, new HashMap<String, Map<String, Object>>(),
                    familyHierarchyName);

            attrs.put("assessmentFamilyHierarchyName", familyHierarchyName);

            @SuppressWarnings("unchecked")
            List<String> objectiveAssessmentRefs = (List<String>) attrs.get("objectiveAssessmentRefs");
            attrs.remove("objectiveAssessmentRefs");
            List<Map<String, Object>> objectiveAssessments = new ArrayList<Map<String, Object>>();
            if (objectiveAssessmentRefs != null && !(objectiveAssessmentRefs.isEmpty())) {

                for (String objectiveAssessmentRef : objectiveAssessmentRefs) {

                    objectiveAssessments.add(objAssmtBuilder.getObjectiveAssessment(objectiveAssessmentRef,
                            ObjectiveAssessmentBuilder.BY_ID));
                }
                attrs.put("objectiveAssessment", objectiveAssessments);
            }

            String assessmentPeriodDescriptorRef = (String) attrs.get("periodDescriptorRef");
            attrs.remove("periodDescriptorRef");
            if (assessmentPeriodDescriptorRef != null) {

                attrs.put("assessmentPeriodDescriptor", getAssessmentPeriodDescriptor(assessmentPeriodDescriptorRef));

            }

            List<Map<String, Object>> assessmentItemsRefs = (List<Map<String, Object>>) attrs.get("assessmentItemRefs");
            if (assessmentItemsRefs != null && assessmentItemsRefs.size() > 0) {
                List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
                attrs.put("assessmentItem", items);
                for (Map<String, Object> assessmentItem : assessmentItemsRefs) {
                    String itemRef = (String) assessmentItem.get("ref");
                    Map<String, Object> item = getAssessmentItem(itemRef);
                    if (item != null) {
                        items.add(item);
                    } else {
                        super.getErrorReport(neutralRecord.getSourceFile()).error("Could not resolve AssessmentItemReference.  AssessmentItem with id " + itemRef + " not found.", this);
                    }
                }
            }
            attrs.remove("assessmentItemRefs");

            neutralRecord.setAttributes(attrs);
            transformedAssessments.put(neutralRecord.getLocalId(), neutralRecord);
        }

    }

    private Map<String, Object> getAssessmentItem(String itemRef) {
        Map<String, String> paths = new HashMap<String, String>();
        paths.put("localId", itemRef);

        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByPathsForJob(
                "assessmentItem", paths, getJob().getId());

        if (data.iterator().hasNext()) {
            return data.iterator().next().getAttributes();
        }
        return null;
    }

    private Map<String, Object> getAssessmentPeriodDescriptor(String assessmentPeriodDescriptorRef) {
        Map<String, String> paths = new HashMap<String, String>();
        paths.put("body.codeValue", assessmentPeriodDescriptorRef);

        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByPathsForJob(
                "assessmentPeriodDescriptor", paths, getJob().getId());

        if (data.iterator().hasNext()) {
            return data.iterator().next().getAttributes();
        }

        return null;

    }

    @SuppressWarnings("unchecked")
    private String getAssocationFamilyMap(String key, HashMap<String, Map<String, Object>> deepFamilyMap,
            String familyHierarchyName) {

        Map<String, String> paths = new HashMap<String, String>();
        paths.put("body.AssessmentFamilyIdentificationCode.ID", key);

        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByPathsForJob(
                "assessmentFamily", paths, getJob().getId());

        Map<String, Object> associationAttrs;

        ArrayList<Map<String, Object>> tempIdentificationCodes;
        Map<String, Object> tempMap;

        for (NeutralRecord tempNr : data) {
            associationAttrs = tempNr.getAttributes();

            if (associationAttrs.get("AssessmentFamilyIdentificationCode") instanceof ArrayList<?>) {
                tempIdentificationCodes = (ArrayList<Map<String, Object>>) associationAttrs
                        .get("AssessmentFamilyIdentificationCode");

                tempMap = tempIdentificationCodes.get(0);
                if (familyHierarchyName.equals("")) {

                    familyHierarchyName = (String) associationAttrs.get("AssessmentFamilyTitle");

                } else {

                    familyHierarchyName = associationAttrs.get("AssessmentFamilyTitle") + "." + familyHierarchyName;

                }
                deepFamilyMap.put((String) tempMap.get("ID"), associationAttrs);
            }

            // check if there are parent nodes
            if (associationAttrs.containsKey("parentAssessmentFamilyId")
                    && !deepFamilyMap.containsKey(associationAttrs.get("parentAssessmentFamilyId"))) {
                familyHierarchyName = getAssocationFamilyMap((String) associationAttrs.get("parentAssessmentFamilyId"),
                        deepFamilyMap, familyHierarchyName);
            }

        }

        return familyHierarchyName;
    }

    public void persist() {
        LOG.info("Persisting transformed data to storage.");
        try {
            Map<IngestionFileEntry, List<Object>> fileEntries = getMetaDataFiles();
            // transformedCollections should have been populated in the transform() step.
            for (Entry<IngestionFileEntry, List<Object>> entry : fileEntries.entrySet()) {
                NeutralRecordFileWriter writer = new NeutralRecordFileWriter(entry.getKey().getNeutralRecordFile());
                List<Object> originals = entry.getValue();
                try {
                    for (Object original : originals) {
                        if (original instanceof NeutralRecord) {
                            // pass through record
                            writer.writeRecord((NeutralRecord) original);
                        }
                        NeutralRecord record = transformedAssessments.get(original);
                        if (record != null) {
                            writer.writeRecord(record);
                        }
                    }
                } finally {
                    writer.close();
                }
            }

        } catch (IOException e) {
            LogUtil.error(LOG, "Error persisting transformed data to storage", e);
        }
    }

    /**
     * Will return the assessment metadata files we are processing.
     * Since we are transforming assessments, one should exist.
     * I know this is hacky, replace when we get a better way to bypass additional ingestion work
     * that breaks this.
     *
     * @return
     * @throws IOException
     */
    private Map<IngestionFileEntry, List<Object>> getMetaDataFiles() throws IOException {
        List<IngestionFileEntry> allFiles = getJob().getFiles();
        Map<IngestionFileEntry, List<Object>> metaDataFiles = new HashMap<IngestionFileEntry, List<Object>>();
        for (IngestionFileEntry fe : allFiles) {
            if (FileType.XML_ASSESSMENT_METADATA.equals(fe.getFileType())) {
                List<Object> inEntry = new ArrayList<Object>();
                NeutralRecordFileReader reader = new NeutralRecordFileReader(fe.getNeutralRecordFile());
                while (reader.hasNext()) {
                    NeutralRecord rec = reader.next();
                    if (rec.getRecordType().equals("assessment")) {
                        inEntry.add(rec.getLocalId());
                    } else if (IGNORE_TYPES.contains(rec.getRecordType())) {
                        inEntry.add(rec);
                    }
                }
                metaDataFiles.put(fe, inEntry);
            }
        }
        return metaDataFiles;
    }

    /**
     * Stores all items in collection found in database to local storage (HashMap)
     *
     * @param collectionName
     */
    private void loadCollectionFromDb(String collectionName) {

        Criteria jobIdCriteria = Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId());

        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findByQueryForJob(
                collectionName, new Query(jobIdCriteria), getJob().getId(), 0, 0);

        Map<Object, NeutralRecord> collection = new HashMap<Object, NeutralRecord>();
        NeutralRecord tempNr;

        Iterator<NeutralRecord> iter = data.iterator();
        while (iter.hasNext()) {
            tempNr = iter.next();
            collection.put(tempNr.getRecordId(), tempNr);
        }

        collections.put(collectionName, collection);
    }

    protected Collection<NeutralRecord> getTransformedAssessments() {
        return transformedAssessments.values();
    }

}
