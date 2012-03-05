package org.slc.sli.ingestion.transformation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Transformer for Assessment Entities
 *
 * @author ifaybyshev
 *
 */
public class AssessmentCombiner extends AbstractCombiner<NeutralRecordMongoAccess, String> {


    private static final Logger LOG = LoggerFactory.getLogger(AssessmentCombiner.class);

    private String jobId;

    public AssessmentCombiner(NeutralRecordMongoAccess neutralRecordMongoAccess) {
        super(neutralRecordMongoAccess);
    }
    
    /**
     * Transforms items inside staging database
     *
     * TODO: shouldn't this method be a part of the AbstractCombiner?
     */
    @Override
    String doHandling(NeutralRecordMongoAccess item, ErrorReport errorReport) {
        LOG.info("Starting Transforming Assessments: Combining");

        String status = "FAIL";

        try {

            // load collections to memory from Mongo
            loadData();

            // perform transformations of the data in local storage
            transform();

            // persist transformed data to storage (i.e. db)
            status = persist();

        } catch (Exception e) {
            LOG.error("Exception", e);

            errorReport.fatal("Could not transform data.", AssessmentCombiner.class);
        }

        return status;
    }

    @Override
    public void loadData() {
        LOG.info("Loading data for transformation.");

        addCollection("teacher");
        LOG.info("Teacher is loaded into local storage.  Total Count = " + collections.get("teacher").size());

        addCollection("teacherSchoolAssociation");
        LOG.info("TeacherSchoolAssociation is loaded into local storage.  Total Count = "
                + collections.get("teacherSchoolAssociation").size());
    }

    @Override
    public void transform() {
        LOG.debug("Transforming data: Injecting schoolIds of all schools that teacher is a part of into teacher record");

        HashMap<Object, NeutralRecord> newCollection = new HashMap<Object, NeutralRecord>();
        String key;

        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collections.get("teacher").entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();

            // get the key of parent
            Map<String, Object> attrs = neutralRecord.getAttributes();
            key = (String) attrs.get("staffUniqueStateId");

            // find children from database
            Map<String, String> paths = new HashMap<String, String>();
            paths.put("body.teacherId", key);

            Iterable<NeutralRecord> data = neutralRecordMongoAccess.getRecordRepository().findByPaths(
                    "teacherSchoolAssociation", paths);
            Iterator<NeutralRecord> iter = data.iterator();

            NeutralRecord tempNr;
            String schoolId;
            Map<String, Object> associationAttrs;

            while (iter.hasNext()) {
                tempNr = iter.next();
                associationAttrs = tempNr.getAttributes();
                schoolId = (String) associationAttrs.get("schoolId");

                attrs.put("schoolId", schoolId);
            }

            neutralRecord.setAttributes(attrs);
            newCollection.put(neutralRecord.getLocalId(), neutralRecord);
        }

        collections.put("modifiedTeacher", newCollection);

    }

    @Override
    public String persist() {
        LOG.info("Persisting transformed data to storage.");

        for (Map.Entry<String, Map<Object, NeutralRecord>> collectionEntry : collections.entrySet()) {

            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collectionEntry.getValue().entrySet()) {

                NeutralRecord neutralRecord = neutralRecordEntry.getValue();
                neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");

                neutralRecordMongoAccess.getRecordRepository().create(neutralRecord);
            }
        }

        return "OK";
    }

    /**
     * Stores all items in collection found in database to local storage (HashMap)
     *
     * @param collectionName
     */
    private void addCollection(String collectionName) {
        Iterable<NeutralRecord> data = neutralRecordMongoAccess.getRecordRepository().findAll(collectionName);
        Iterator<NeutralRecord> iter = data.iterator();

        Map<Object, NeutralRecord> collection = new HashMap<Object, NeutralRecord>();
        NeutralRecord tempNr;

        while (iter.hasNext()) {
            tempNr = iter.next();
            collection.put(tempNr.getLocalId(), tempNr);
        }

        collections.put(collectionName, collection);
    }

    @Override
    public void setJobId(String id) {
        this.jobId = id;

    }

}
