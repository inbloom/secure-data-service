/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.ingestion.transformation.assessment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.AbstractTransformationStrategy;

/**
 * Transformer for StudentAssessmentAssociation entities.
 *
 * @author nbrown
 * @author shalka
 */
//@Scope("prototype")
//@Component("studentAssessmentAssociationTransformationStrategy")
public class StudentAssessmentCombiner extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(StudentAssessmentCombiner.class);

    private static final String STUDENT_ASSESSMENT_ASSOCIATION = "studentAssessmentAssociation";
    private static final String STUDENT_ASSESSMENT_ASSOCIATION_TRANSFORMED = "studentAssessmentAssociation_transformed";
    private static final String STUDENT_OBJECTIVE_ASSESSMENT = "studentObjectiveAssessment";
    private static final String STUDENT_ASSESSMENT_REFERENCE = "studentAssessmentRef";
    private static final String OBJECTIVE_ASSESSMENT_REFERENCE = "objectiveAssessmentRef";
    private static final String STUDENT_ASSESSMENT_ITEMS_FIELD = "studentAssessmentItems";
    private static final String BODY = "body.";

    private Map<Object, NeutralRecord> studentAssessments;
    List<NeutralRecord> transformedStudentAssessments;

    @Autowired
    private ObjectiveAssessmentBuilder builder;

    /**
     * Default constructor.
     */
    public StudentAssessmentCombiner() {
        studentAssessments = new HashMap<Object, NeutralRecord>();
        transformedStudentAssessments = new ArrayList<NeutralRecord>();
    }

    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go."
     */
    @Override
    public void performTransformation() {
        loadStudentAssessmentData();
        transformStudentAssessment();
        insertRecords(transformedStudentAssessments, STUDENT_ASSESSMENT_ASSOCIATION_TRANSFORMED);
    }

    /**
     * Pre-requisite interchanges for student assessment data to be successfully transformed:
     * student, assessment metadata
     */
    public void loadStudentAssessmentData() {
        LOG.info("Loading data for studentAssessmentAssociation transformation.");
        studentAssessments = getCollectionFromDb(STUDENT_ASSESSMENT_ASSOCIATION);
        LOG.info("{} is loaded into local storage.  Total Count = {}", STUDENT_ASSESSMENT_ASSOCIATION,
                studentAssessments.size());
    }

    /**
     * Transforms student assessments from Ed-Fi model into SLI model.
     */
    public void transformStudentAssessment() {
        LOG.info("Transforming student assessment data");
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : studentAssessments.entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();
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
            neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");
            neutralRecord.setCreationTime(getWorkNote().getRangeMinimum());
            transformedStudentAssessments.add(neutralRecord);
        }
        LOG.info("Finished transforming student assessment data for {} student assessment associations.",
                studentAssessments.size());
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
