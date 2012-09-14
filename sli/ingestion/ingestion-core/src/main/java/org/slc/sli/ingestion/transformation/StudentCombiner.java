package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.assessment.ObjectiveAssessmentBuilder;

/**
 *
 * @author joechung
 *
 */
@Scope("prototype")
@Component("studentTransformationStrategy")
public class StudentCombiner extends AbstractTransformationStrategy {

    private static final String STUDENT_OBJECTIVE_ASSESSMENT = "studentObjectiveAssessment";
    private static final String STUDENT_ASSESSMENT_REFERENCE = "studentAssessmentRef";
    private static final String OBJECTIVE_ASSESSMENT_REFERENCE = "objectiveAssessmentRef";
    private static final String STUDENT_ASSESSMENT_ITEMS_FIELD = "studentAssessmentItems";
    private static final String BODY = "body.";

    @Autowired
    private ObjectiveAssessmentBuilder builder;

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

            String studentUniqueStateId = studentRecord.getAttributes().get("studentUniqueStateId").toString();

            // collapse section into student
            Query sectionAssociationQuery = new Query();
            sectionAssociationQuery.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
            sectionAssociationQuery.addCriteria(Criteria.where("body.studentReference.studentIdentity.studentUniqueStateId").is(studentUniqueStateId));
            Iterable<NeutralRecord> associationRecords = getNeutralRecordMongoAccess().getRecordRepository()
                .findAllByQuery("studentSectionAssociation", sectionAssociationQuery);

            for (NeutralRecord studentSectionAssociationRecord : associationRecords) {
                Map<String, Object> sectionAttributes = studentSectionAssociationRecord.getAttributes();
                sectionAttributes.remove("studentReference");
                sections.add(sectionAttributes);
//                Query sectionQuery = new Query();
//                sectionQuery.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
//                String sectionId = ((BasicDBObject) ((BasicDBObject) studentSectionAssociationRecord.getAttributes().get("sectionReference")).get("sectionIdentity")).get("uniqueSectionCode").toString();
//                sectionQuery.addCriteria(Criteria.where("body.uniqueSectionCode").is(sectionId));
//                Iterable<NeutralRecord> sectionRecords = getNeutralRecordMongoAccess().getRecordRepository()
//                        .findAllByQuery("section", sectionQuery);
//                for (NeutralRecord sectionRecord : sectionRecords) {
//                    Map<String, Object> sectionAttributes = sectionRecord.getAttributes();
//                    sectionAttributes.remove("schoolReference");
//                    sectionAttributes.remove("courseOfferingReference");
//                    sectionAttributes.remove("sessionReference");
//                    sections.add(sectionRecord.getAttributes());
//                }
            }

            // collapse assessments into student
            List<Object> assessments = new ArrayList<Object>();
            attributes.put("assessments", assessments);

            Query assessmentAssociationQuery = new Query();
            assessmentAssociationQuery.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
            assessmentAssociationQuery.addCriteria(Criteria.where("body.studentId").is(studentUniqueStateId));
            Iterable<NeutralRecord> assessmentAssociationRecords = getNeutralRecordMongoAccess().getRecordRepository()
                    .findAllByQuery("studentAssessmentAssociation", assessmentAssociationQuery);


            for (NeutralRecord studentAssessmentAssociationRecord : assessmentAssociationRecords) {
                Map<String, Object> assessmentAttributes = transform(studentAssessmentAssociationRecord).getAttributes();
                assessmentAttributes.remove("objectiveAssessmentRefs");
                assessmentAttributes.remove("studentId");
                assessmentAttributes.remove("assessmentId");
//                BasicDBList idList = (BasicDBList) ((BasicDBObject) ((BasicDBObject) studentAssessmentAssociationRecord.getAttributes().get("assessmentId")).get("AssessmentIdentity")).get("AssessmentIdentificationCode");
//                BasicDBObject assessmentIdDbObject = (BasicDBObject) idList.get(0);
//                assessmentAttributes.put("assessmentId", assessmentIdDbObject.toString());
                assessments.add(assessmentAttributes);
//                Query assessmentQuery = new Query();
//                assessmentQuery.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
//                BasicDBList idList = (BasicDBList) ((BasicDBObject) ((BasicDBObject) studentAssessmentAssociationRecord.getAttributes().get("assessmentId")).get("AssessmentIdentity")).get("AssessmentIdentificationCode");
//                BasicDBObject assessmentIdDbObject = (BasicDBObject) idList.get(0);
//                String assessmentId = assessmentIdDbObject.get("ID").toString();
//                assessmentQuery.addCriteria(Criteria.where("body.assessmentIdentificationCode.ID").is(assessmentId));
//                Iterable<NeutralRecord> assessmentRecords = getNeutralRecordMongoAccess().getRecordRepository()
//                        .findAllByQuery("assessment", assessmentQuery);
//                for (NeutralRecord assessmentRecord : assessmentRecords) {
//                    Map<String, Object> assessmentAttributes = transform(assessmentRecord).getAttributes();
//                    assessmentAttributes.remove("objectiveAssessmentRefs");
//                    assessments.add(assessmentAttributes);
//                }
            }

            transformedStudents.add(studentRecord);

        }
        insertRecords(transformedStudents, "student_transformed");
        LOG.info("Completed transforming student data");
    }

    /**
     * Transforms student assessments from Ed-Fi model into SLI model.
     */
    private NeutralRecord transform(NeutralRecord studentAssessmentRecord) {
        Map<String, Object> attributes = studentAssessmentRecord.getAttributes();
        String studentAssessmentAssociationId = (String) attributes.remove("xmlId");

        if (studentAssessmentAssociationId != null) {
            List<Map<String, Object>> studentObjectiveAssessments = getStudentObjectiveAssessments(studentAssessmentAssociationId);
            if (studentObjectiveAssessments.size() > 0) {
                LOG.debug("found {} student objective assessments for student assessment id: {}.",
                        studentObjectiveAssessments.size(), studentAssessmentAssociationId);
                attributes.put("studentObjectiveAssessments", studentObjectiveAssessments);
            }

            List<Map<String, Object>> studentAssessmentItems = getStudentAssessmentItems(studentAssessmentAssociationId);
            if (studentAssessmentItems.size() > 0) {
                LOG.debug("found {} student assessment items for student assessment id: {}.",
                        studentAssessmentItems.size(), studentAssessmentAssociationId);
                attributes.put(STUDENT_ASSESSMENT_ITEMS_FIELD, studentAssessmentItems);
            }
        } else {
            LOG.warn(
                    "no local id for student assessment association: {}. cannot embed student objective assessment objects.",
                    studentAssessmentAssociationId);
        }
        studentAssessmentRecord.setRecordType(studentAssessmentRecord.getRecordType() + "_transformed");
        studentAssessmentRecord.setCreationTime(getWorkNote().getRangeMinimum());
        return studentAssessmentRecord;
    }

    /**
     * Gets all student objective assessments that reference the student assessment's local (xml)
     * id.
     *
     * @param studentAssessmentAssociationId
     *            volatile identifier.
     * @return list of student objective assessments (represented by neutral records).
     */
    private List<Map<String, Object>> getStudentObjectiveAssessments(String studentAssessmentAssociationId) {
        List<Map<String, Object>> assessments = new ArrayList<Map<String, Object>>();
        Query query = new Query().limit(0);
        query.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
        query.addCriteria(Criteria.where(BODY + STUDENT_ASSESSMENT_REFERENCE).is(studentAssessmentAssociationId));

        Iterable<NeutralRecord> studentObjectiveAssessments = getNeutralRecordMongoAccess().getRecordRepository()
                .findAllByQuery(STUDENT_OBJECTIVE_ASSESSMENT, query);

        if (studentObjectiveAssessments != null) {
            Iterator<NeutralRecord> itr = studentObjectiveAssessments.iterator();
            NeutralRecord studentObjectiveAssessment = null;
            while (itr.hasNext()) {
                studentObjectiveAssessment = itr.next();
                Map<String, Object> assessmentAttributes = studentObjectiveAssessment.getAttributes();
                String objectiveAssessmentRef = (String) assessmentAttributes.remove(OBJECTIVE_ASSESSMENT_REFERENCE);

                LOG.debug("Student Objective Assessment: {} --> finding objective assessment: {}", studentObjectiveAssessment.getLocalId(), objectiveAssessmentRef);

                Map<String, Object> objectiveAssessment = builder.getObjectiveAssessment(getNeutralRecordMongoAccess(),
                        getJob(), objectiveAssessmentRef);

                if (objectiveAssessment != null) {
                    LOG.debug("Found objective assessment: {}", objectiveAssessmentRef);
                    assessmentAttributes.put("objectiveAssessment", objectiveAssessment);
                } else {
                    LOG.warn("Failed to find objective assessment: {} for student assessment: {}",
                            objectiveAssessmentRef, studentAssessmentAssociationId);
                }

                Map<String, Object> attributes = new HashMap<String, Object>();
                for (Map.Entry<String, Object> entry : assessmentAttributes.entrySet()) {
                    if (!entry.getKey().equals(OBJECTIVE_ASSESSMENT_REFERENCE)
                            && !entry.getKey().equals(STUDENT_ASSESSMENT_REFERENCE)) {
                        attributes.put(entry.getKey(), entry.getValue());
                    }
                }

                assessments.add(attributes);
            }
        } else {
            LOG.warn("Couldn't find any student objective assessments for student assessment: {}",
                    studentAssessmentAssociationId);
        }

        LOG.debug("Found {} student objective assessments for student assessment: {}", assessments.size(), studentAssessmentAssociationId);
        return assessments;
    }

    private List<Map<String, Object>> getStudentAssessmentItems(String studentAssessmentId) {
        List<Map<String, Object>> studentAssessmentItems = new ArrayList<Map<String, Object>>();
        Map<String, String> studentAssessmentItemSearchPaths = new HashMap<String, String>();
        studentAssessmentItemSearchPaths.put("localParentIds.studentResultRef", studentAssessmentId);

        Iterable<NeutralRecord> sassItems = getNeutralRecordMongoAccess().getRecordRepository().findByPathsForJob(
                "studentAssessmentItem", studentAssessmentItemSearchPaths, getJob().getId());

        if (sassItems != null) {
            for (NeutralRecord sai : sassItems) {
                String assessmentId = (String) sai.getLocalParentIds().get("assessmentItemIdentificatonCode");
                if (assessmentId != null) {
                    Map<String, String> assessmentSearchPath = new HashMap<String, String>();
                    assessmentSearchPath.put("body.identificationCode", assessmentId);

                    Iterable<NeutralRecord> assessmentItems = getNeutralRecordMongoAccess().getRecordRepository()
                            .findByPathsForJob("assessmentItem", assessmentSearchPath, getJob().getId());

                    if (assessmentItems.iterator().hasNext()) {
                        NeutralRecord assessmentItem = assessmentItems.iterator().next();
                        sai.getAttributes().put("assessmentItem", assessmentItem.getAttributes());
                    } else {
                        super.getErrorReport(sai.getSourceFile()).error(
                                "Cannot find AssessmentItem referenced by StudentAssessmentItem.  AssessmentItemIdentificationCode: "
                                        + assessmentId, this);
                    }
                } else {
                    super.getErrorReport(sai.getSourceFile())
                            .error("StudentAsessmentItem does not contain an AssessmentItemIdentificationCode referencing an AssessmentItem",
                                    this);
                }

                studentAssessmentItems.add(sai.getAttributes());
            }
        }
        return studentAssessmentItems;
    }
}
