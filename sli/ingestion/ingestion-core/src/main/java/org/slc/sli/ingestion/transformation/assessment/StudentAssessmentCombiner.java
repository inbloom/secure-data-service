/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.ElementSourceImpl;
import org.slc.sli.ingestion.transformation.AbstractTransformationStrategy;

/**
 * Transformer for StudentAssessment entities.
 *
 * @author nbrown
 * @author shalka
 */
@Scope("prototype")
@Component("studentAssessmentTransformationStrategy")
public class StudentAssessmentCombiner extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(StudentAssessmentCombiner.class);

    private static final String STUDENT_ASSESSMENT = "studentAssessment";
    private static final String STUDENT_ASSESSMENT_TRANSFORMED = "studentAssessment_transformed";
    private static final String STUDENT_OBJECTIVE_ASSESSMENT = "studentObjectiveAssessment";
    private static final String STUDENT_ASSESSMENT_ITEM = "studentAssessmentItem";
    private static final String STUDENT_ASSESSMENT_REF = "studentAssessmentRef";
    private static final String STUDENT_ASSESSMENT_REFERENCE = "studentAssessmentReference";
    private static final String OBJECTIVE_ASSESSMENT_REFERENCE = "objectiveAssessmentRef";
    private static final String STUDENT_ASSESSMENT_ITEMS_FIELD = "studentAssessmentItems";
    private static final String STUDENT_ASSESSMENT_REFERENCE_ADMINISTRATION_DATE = "studentAssessmentReference.administrationDate";
    private static final String STUDENT_ASSESSMENT_REFERENCE_STUDENT = "studentAssessmentReference.studentReference.studentUniqueStateId";

    private static final String LOCAL_PARENT_IDS = "localParentIds.";
    private static final String BODY = "body.";

    private static final String ASSESSMENT_TITLE = "AssessmentTitle";
    private static final String ACADEMIC_SUBJECT = "AcademicSubject";
    private static final String GRADE_LEVEL_ASSESSED = "GradeLevelAssessed";
    private static final String VERSION = "Version";
    private static final String ASSESSMENT_ITEM = "assessmentItem";

    private static final String STUDENT_ASSESSMENT_REFERENCE_ASSESSMENT_TITLE = "studentAssessmentReference.assessmentReference.assessmentTitle";
    private static final String STUDENT_ASSESSMENT_REFERENCE_ACADEMIC_SUBJECT = "studentAssessmentReference.assessmentReference.academicSubject";
    private static final String STUDENT_ASSESSMENT_REFERENCE_GRADE_LEVEL_ASSESSED = "studentAssessmentReference.assessmentReference.gradeLevelAssessed";
    private static final String STUDENT_ASSESSMENT_REFERENCE_VERSION = "studentAssessmentReference.assessmentReference.version";

    private Map<Object, NeutralRecord> studentAssessments;
    private List<NeutralRecord> transformedStudentAssessments;

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
        loadData();
        transform();
        insertRecords(transformedStudentAssessments, STUDENT_ASSESSMENT_TRANSFORMED);
    }

    /**
     * Pre-requisite interchanges for student assessment data to be successfully transformed:
     * student, assessment metadata
     */
    public void loadData() {
        LOG.info("Loading data for studentAssessment transformation.");
        studentAssessments = getCollectionFromDb(STUDENT_ASSESSMENT);
        LOG.info("{} is loaded into local storage.  Total Count = {}", STUDENT_ASSESSMENT, studentAssessments.size());
    }

    /**
     * Transforms student assessments from Ed-Fi model into SLI model.
     */
    public void transform() {
        LOG.info("Transforming student assessment data");
        builder.setAbstractTransformationStrategy(this);
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : studentAssessments.entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();

            String studentId = null;
            String administrationDate = null;

            String assessmentTitle = null;
            String academicSubject = null;
            String gradeLevelAssessed = null;
            Integer version = null;

            try {
                studentId = (String) PropertyUtils.getNestedProperty(attributes,
                        "studentId.StudentIdentity.StudentUniqueStateId");
            } catch (NoSuchMethodException e) {
                LOG.warn("Unable to get StudentID within {} for StudentAssessment transform", attributes);
                reportWarnings(neutralRecord.getSourceFile(), new ElementSourceImpl(neutralRecord), CoreMessageCode.CORE_0041, attributes);
            } catch (Exception e) {
                LOG.error("Exception occurred while retreiving student id", e);
                reportError(neutralRecord.getSourceFile(), new ElementSourceImpl(neutralRecord), CoreMessageCode.CORE_0042, e.toString());
            }

            try {
                administrationDate = (String) attributes.get("administrationDate");
            } catch (ClassCastException e) {
                LOG.error("Illegal value {} for administration date, must be a string");
                reportError(neutralRecord.getSourceFile(), new ElementSourceImpl(neutralRecord), CoreMessageCode.CORE_0039, attributes.get("administrationDate"));
            }

            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> assessment = (Map<String, Object>) attributes.get("assessmentId");
                @SuppressWarnings("unchecked")
                Map<String, Object> assessmentIdentity = (Map<String, Object>) assessment.get("AssessmentIdentity");

                assessmentTitle = (String) assessmentIdentity.get(ASSESSMENT_TITLE);
                academicSubject = (String) assessmentIdentity.get(ACADEMIC_SUBJECT);
                gradeLevelAssessed = (String) assessmentIdentity.get(GRADE_LEVEL_ASSESSED);
                version = (Integer) assessmentIdentity.get(VERSION);
            } catch (Exception e) {
                LOG.error("Unable to get key fields for StudentAssessment transform", e);
                reportError(neutralRecord.getSourceFile(), new ElementSourceImpl(neutralRecord), CoreMessageCode.CORE_0040, e.toString());
            }

            Map<String, Object> queryCriteria = new LinkedHashMap<String, Object>();
            queryCriteria.put(STUDENT_ASSESSMENT_REFERENCE_STUDENT, studentId);
            queryCriteria.put(STUDENT_ASSESSMENT_REFERENCE_ADMINISTRATION_DATE, administrationDate);
            queryCriteria.put(STUDENT_ASSESSMENT_REFERENCE_ASSESSMENT_TITLE, assessmentTitle);
            queryCriteria.put(STUDENT_ASSESSMENT_REFERENCE_ACADEMIC_SUBJECT, academicSubject);
            queryCriteria.put(STUDENT_ASSESSMENT_REFERENCE_GRADE_LEVEL_ASSESSED, gradeLevelAssessed);
            queryCriteria.put(STUDENT_ASSESSMENT_REFERENCE_VERSION, version);

            List<Map<String, Object>> studentObjectiveAssessments = getStudentObjectiveAssessmentsNaturalKeys(queryCriteria);

            if (studentObjectiveAssessments.size() > 0) {
                attributes.put("studentObjectiveAssessments", studentObjectiveAssessments);
            }

            List<Map<String, Object>> studentAssessmentItems = getStudentAssessmentItemsNaturalKeys(queryCriteria);

            if (studentAssessmentItems.size() > 0) {
                attributes.put(STUDENT_ASSESSMENT_ITEMS_FIELD, studentAssessmentItems);
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
     * @param studentAssessmentId
     *            volatile identifier.
     * @return list of student objective assessments (represented by neutral records).
     */
    private List<Map<String, Object>> getStudentObjectiveAssessmentsNaturalKeys(Map<String, Object> queryCriteria) {

        List<Map<String, Object>> assessments = new ArrayList<Map<String, Object>>();
        Query query = new Query().limit(0);
        query.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));

        for (Map.Entry<String, Object> entry : queryCriteria.entrySet()) {
            query.addCriteria(Criteria.where(BODY + entry.getKey()).is(entry.getValue()));
        }

        Iterable<NeutralRecord> studentObjectiveAssessments = getNeutralRecordMongoAccess().getRecordRepository()
                .findAllByQuery(STUDENT_OBJECTIVE_ASSESSMENT, query);

        if (studentObjectiveAssessments != null) {
            Iterator<NeutralRecord> itr = studentObjectiveAssessments.iterator();
            NeutralRecord studentObjectiveAssessment = null;
            while (itr.hasNext()) {
                studentObjectiveAssessment = itr.next();
                Map<String, Object> assessmentAttributes = studentObjectiveAssessment.getAttributes();
                String objectiveAssessmentRef = (String) assessmentAttributes.remove(OBJECTIVE_ASSESSMENT_REFERENCE);

                LOG.debug("Student Objective Assessment: {} --> finding objective assessment: {}",
                        studentObjectiveAssessment.getLocalId(), objectiveAssessmentRef);

                Map<String, Object> objectiveAssessment = builder.getObjectiveAssessment(getNeutralRecordMongoAccess(),
                        getJob(), objectiveAssessmentRef);

                if (objectiveAssessment != null) {
                    LOG.debug("Found objective assessment: {}", objectiveAssessmentRef);
                    objectiveAssessment.remove("assessmentId");
                    assessmentAttributes.put("objectiveAssessment", objectiveAssessment);
                }

                Map<String, Object> attributes = new HashMap<String, Object>();
                for (Map.Entry<String, Object> entry : assessmentAttributes.entrySet()) {
                    if (!entry.getKey().equals(OBJECTIVE_ASSESSMENT_REFERENCE)
                            && !entry.getKey().equals(STUDENT_ASSESSMENT_REF)
                            && !entry.getKey().equals(STUDENT_ASSESSMENT_REFERENCE)) {
                        attributes.put(entry.getKey(), entry.getValue());
                    }
                }

                assessments.add(attributes);
            }
        }

        return assessments;
    }

    private List<Map<String, Object>> getStudentAssessmentItemsNaturalKeys(Map<String, Object> queryCriteria) {

        List<Map<String, Object>> studentAssessmentItems = new ArrayList<Map<String, Object>>();
        Query query = new Query().limit(0);
        query.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));

        for (Map.Entry<String, Object> entry : queryCriteria.entrySet()) {
            query.addCriteria(Criteria.where(LOCAL_PARENT_IDS + entry.getKey()).is(entry.getValue()));
        }

        Iterable<NeutralRecord> sassItems = getNeutralRecordMongoAccess().getRecordRepository().findAllByQuery(
                STUDENT_ASSESSMENT_ITEM, query);

        if (sassItems != null) {
            for (NeutralRecord sai : sassItems) {

                String assessmentItemIdentificatonCode = (String) sai.getLocalParentIds().get(
                        "assessmentItemIdentificatonCode");

                if (assessmentItemIdentificatonCode != null) {
                    Map<String, String> assessmentSearchPath = new HashMap<String, String>();
                    assessmentSearchPath.put("body.identificationCode", assessmentItemIdentificatonCode);

                    Iterable<NeutralRecord> assessmentItems = getNeutralRecordMongoAccess().getRecordRepository()
                            .findByPathsForJob(ASSESSMENT_ITEM, assessmentSearchPath, getJob().getId());

                    if (assessmentItems.iterator().hasNext()) {
                        NeutralRecord assessmentItem = assessmentItems.iterator().next();
                        assessmentItem.getAttributes().remove("assessmentId");
                        sai.getAttributes().put(ASSESSMENT_ITEM, assessmentItem.getAttributes());
                    } else {
                        reportError(sai.getSourceFile(), new ElementSourceImpl(sai), CoreMessageCode.CORE_0032, assessmentItemIdentificatonCode);
                    }
                } else {
                    reportError(sai.getSourceFile(), new ElementSourceImpl(sai), CoreMessageCode.CORE_0033);
                }

                studentAssessmentItems.add(sai.getAttributes());
            }
        }
        return studentAssessmentItems;
    }
}
