package org.slc.sli.ingestion.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Order;

/**
 * Get mongo indexing commands for all collections.
 *
 * @author tshewchuk
 *
 */
public final class MongoIndices {

    private static final Logger LOG = LoggerFactory.getLogger(MongoIndices.class);

    @SuppressWarnings("serial")
    static final Map<String, ArrayList<ArrayList<String>>> MONGO_COLLECTIONS = new HashMap<String, ArrayList<ArrayList<String>>>() {
        private static final long serialVersionUID = 1L;
        {
           put("assessment", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("attendance", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("body.studentId"); } }); } });
           put("course", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("educationOrganization", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("gradebookEntry", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("parent", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("school", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("session", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("staff", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("staffEducationOrganizationAssociation", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("student", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("studentAssessmentAssociation", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("teacher", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("studentSectionGradebookEntry", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("studentSchoolAssociation", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
               add(new ArrayList<String>() { { add("body.studentId"); add("metaData.tenantId"); add("metaData.externalId"); } });
               add(new ArrayList<String>() { { add("body.schoolId"); add("metaData.tenantId"); add("metaData.externalId"); } }); } });
           put("studentSectionAssociation", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("body.studentId"); add("metaData.tenantId"); add("metaData.externalId"); } });
               add(new ArrayList<String>() { { add("body.sectionId"); add("metaData.tenantId"); add("metaData.externalId"); } });
               add(new ArrayList<String>() { { add("body.studentId"); add("metaData.tenantId"); add("body.sectionId"); } }); } });
           put("studentParentAssociation", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("body.parentId"); add("body.studentId"); add("metaData.tenantId"); } }); } });
           put("studentTranscriptAssociation", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("body.studentId"); add("metaData.tenantId"); add("body.courseId"); } }); } });
           put("teacherSectionAssociation", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("body.teacherId"); add("metaData.tenantId"); add("body.sectionId"); } }); } });
           put("section", new ArrayList<ArrayList<String>>() { {
               add(new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
               add(new ArrayList<String>() { { add("body.schoolId"); add("metaData.tenantId"); add("metaData.externalId"); } });
               add(new ArrayList<String>() { { add("body.courseId"); add("metaData.tenantId"); add("metaData.externalId"); } }); } });
        }
    };

    public static void ensureMongoIndicies(String collectionNameSuffix, MongoTemplate mongoTemplate) {
        // Read the configuration file into the index list.
        for (Map.Entry<String, ArrayList<ArrayList<String>>> collection : MONGO_COLLECTIONS.entrySet()) {
//            DBObject dBObject = new BasicDBObject();
            String collectionName = collection.getKey() + collectionNameSuffix;
            int serial = 0;
            for (Iterator<ArrayList<String>> indexes = collection.getValue().iterator(); indexes.hasNext();) {
                Index indexDef = new Index();
                for (Iterator<String> index = indexes.next().iterator(); index.hasNext();) {
                    indexDef.on(index.next(), Order.ASCENDING);
//                    dBObject.put(index.next(), ++serial);
                }
                indexDef.named(collection.getKey() + String.valueOf(++serial));
                try {
//                mongoTemplate.getDb().getCollection(collectionName).ensureIndex(dBObject);
                    mongoTemplate.ensureIndex(indexDef, collectionName);
                } catch (Exception e) {
                    LOG.error("Error ensuring index " + collection.getKey() + String.valueOf(++serial) + " of collection " + collectionName + ":\n" + e.getMessage());
                }
            }
        }
    }

}
