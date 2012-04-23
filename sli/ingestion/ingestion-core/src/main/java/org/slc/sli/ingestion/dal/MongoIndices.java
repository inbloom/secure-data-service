package org.slc.sli.ingestion.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Get mongo indexing commands for all collections.
 *
 * @author tshewchuk
 *
 */
public final class MongoIndices {

    private static final Logger LOG = LoggerFactory.getLogger(MongoIndices.class);

    @SuppressWarnings("serial")
    static final Map<String, ArrayList<String>> BASE_MONGO_ENTITIES = new HashMap<String, ArrayList<String>>() {
        private static final long serialVersionUID = 1L;
        {
           put("assessment", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("course", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("educationOrganization", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("gradebookEntry", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("parent", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("school", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("session", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("staff", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("staffEducationOrganizationAssociation", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("student", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("studentAssessmentAssociation", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("teacher", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("studentSectionGradebookEntry", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("studentSchoolAssociation", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("studentSchoolAssociation", new ArrayList<String>() { { add("body.studentId"); add("metaData.tenantId"); add("metaData.externalId"); } });
           put("studentSchoolAssociation", new ArrayList<String>() { { add("body.schoolId"); add("metaData.tenantId"); add("metaData.externalId"); } });
           put("studentSectionAssociation", new ArrayList<String>() { { add("body.studentId"); add("metaData.tenantId"); add("metaData.externalId"); } });
           put("studentSectionAssociation", new ArrayList<String>() { { add("body.sectionId"); add("metaData.tenantId"); add("metaData.externalId"); } });
           put("studentParentAssociation", new ArrayList<String>() { { add("body.parentId"); add("body.studentId"); add("metaData.tenantId"); } });
           put("studentSectionAssociation", new ArrayList<String>() { { add("body.studentId"); add("metaData.tenantId"); add("body.sectionId"); } });
           put("studentTranscriptAssociation", new ArrayList<String>() { { add("body.studentId"); add("metaData.tenantId"); add("body.courseId"); } });
           put("teacherSectionAssociation", new ArrayList<String>() { { add("body.teacherId"); add("metaData.tenantId"); add("body.sectionId"); } });
           put("section", new ArrayList<String>() { { add("metaData.tenantId"); add("metaData.externalId"); } });
           put("section", new ArrayList<String>() { { add("body.schoolId"); add("metaData.tenantId"); add("metaData.externalId"); } });
           put("section", new ArrayList<String>() { { add("body.courseId"); add("metaData.tenantId"); add("metaData.externalId"); } });
        }
    };

    public static void ensureMongoIndicies(String collectionNameSuffix, MongoTemplate mongoTemplate) {
        // Read the configuration file into the index list.
        for (Map.Entry<String, ArrayList<String>> indexCommand : BASE_MONGO_ENTITIES.entrySet()) {
            DBObject dBObject = new BasicDBObject();
            int serial = 0;
            for (Iterator<String> index = indexCommand.getValue().iterator(); index.hasNext();) {
                dBObject.put(index.next(), ++serial);
            }
            String collectionName = indexCommand.getKey() + collectionNameSuffix;
            try {
                mongoTemplate.getDb().getCollection(collectionName).ensureIndex(dBObject);
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
         }
    }

}
