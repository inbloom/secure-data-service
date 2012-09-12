package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.NeutralRecord;

/**
 *
 * @author joechung
 *
 */
@Scope("prototype")
@Component("studentTransformationStrategy")
public class StudentCombiner extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(StudentCombiner.class);
    @Override
    protected void performTransformation() {
        LOG.info("Transforming student data");
        List<NeutralRecord> transformedStudents = new ArrayList<NeutralRecord>();
        Map<Object, NeutralRecord> students = getCollectionFromDb("student");
        for (Map.Entry<Object, NeutralRecord> studentNeutralRecordEntry : students.entrySet()) {
            NeutralRecord studentRecord = studentNeutralRecordEntry.getValue();
            Map<String, Object> attributes = studentRecord.getAttributes();
            List<Object> sections = new ArrayList<Object>();
            attributes.put("sections", sections);
//            attributes.put("joeTest", "1234");

            Query associationQuery = new Query();
            associationQuery.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
            String studentUniqueStateId = studentRecord.getAttributes().get("studentUniqueStateId").toString();
            associationQuery.addCriteria(Criteria.where("body.studentReference.studentIdentity.studentUniqueStateId").is(studentUniqueStateId));
            Iterable<NeutralRecord> associationRecords = getNeutralRecordMongoAccess().getRecordRepository()
                .findAllByQuery("studentSectionAssociation", associationQuery);

            associationQuery.getQueryObject();
            for (NeutralRecord studentSectionAssociationRecord : associationRecords) {
                Query sectionQuery = new Query();
                sectionQuery.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
                String sectionId = ((BasicDBObject) ((BasicDBObject) studentSectionAssociationRecord.getAttributes().get("sectionReference")).get("sectionIdentity")).get("uniqueSectionCode").toString();
                sectionQuery.addCriteria(Criteria.where("body.uniqueSectionCode").is(sectionId));
                Iterable<NeutralRecord> sectionRecords = getNeutralRecordMongoAccess().getRecordRepository()
                        .findAllByQuery("section", sectionQuery);
                for (NeutralRecord sectionRecord : sectionRecords) {
                    Map<String, Object> sectionAttributes = sectionRecord.getAttributes();
                    sectionAttributes.remove("schoolReference");
                    sectionAttributes.remove("courseOfferingReference");
                    sectionAttributes.remove("sessionReference");
                    sections.add(sectionRecord.getAttributes());
                }
            }

            transformedStudents.add(studentRecord);

        }
        insertRecords(transformedStudents, "student_transformed");
        LOG.info("Completed transforming student data");
    }

}
