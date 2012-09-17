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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.transformation.AbstractTransformationStrategy;

/**
 * Transformer for Assessment Entities
 *
 * @author ifaybyshev
 * @author shalka
 */
@Scope("prototype")
@Component("assessmentTransformationStrategy")
public class AssessmentCombiner extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(AssessmentCombiner.class);

    private Map<Object, NeutralRecord> assessments;
    private List<NeutralRecord> transformedAssessments;

    private static final String ASSESSMENT = "assessment";
    private static final String ASSESSMENT_FAMILY = "assessmentFamily";
    private static final String ASSESSMENT_PERIOD_DESCRIPTOR = "assessmentPeriodDescriptor";
    private static final String ASSESSMENT_TRANSFORMED = "assessment_transformed";

    @Autowired
    private ObjectiveAssessmentBuilder builder;

    /**
     * Default constructor.
     */
    public AssessmentCombiner() {
        assessments = new HashMap<Object, NeutralRecord>();
        transformedAssessments = new ArrayList<NeutralRecord>();
    }

    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go"
     */
    @Override
    public void performTransformation() {
        loadData();
        transform();
        insertRecords(transformedAssessments, ASSESSMENT_TRANSFORMED);
    }

    /**
     * Pre-requisite interchanges for assessment data to be successfully transformed:
     * none (as of 5/8/2012)
     */
    public void loadData() {
        LOG.info("Loading data for assessment transformation.");
        assessments = getCollectionFromDb(ASSESSMENT);
        LOG.info("{} is loaded into local storage.  Total Count = {}", ASSESSMENT, assessments.size());
    }

    /**
     * Transforms assessments from Ed-Fi model into SLI model.
     */
    public void transform() {
        LOG.info("Transforming assessment data");
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : assessments.entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();

            // get the key of parent
            Map<String, Object> attrs = neutralRecord.getAttributes();
            String parentFamilyId = (String) attrs.remove("parentAssessmentFamilyId");
            String familyHierarchyName = "";
            familyHierarchyName = getAssocationFamilyMap(parentFamilyId, new HashMap<String, Map<String, Object>>(),
                    familyHierarchyName);

            attrs.put("assessmentFamilyHierarchyName", familyHierarchyName);

            @SuppressWarnings("unchecked")
            List<String> objectiveAssessmentRefs = (List<String>) attrs.get("objectiveAssessmentRefs");
            attrs.remove("objectiveAssessmentRefs");
            List<Map<String, Object>> familyObjectiveAssessments = new ArrayList<Map<String, Object>>();
            if (objectiveAssessmentRefs != null && !(objectiveAssessmentRefs.isEmpty())) {
                for (String objectiveAssessmentRef : objectiveAssessmentRefs) {
                    Map<String, Object> objectiveAssessment = builder.getObjectiveAssessment(
                            getNeutralRecordMongoAccess(), getJob(), objectiveAssessmentRef);

                    if (objectiveAssessment != null && !objectiveAssessment.isEmpty()) {
                        LOG.info("Found objective assessment: {} for family: {}", objectiveAssessmentRef,
                                familyHierarchyName);
                        familyObjectiveAssessments.add(objectiveAssessment);
                    } else {
                        LOG.warn("Failed to match objective assessment: {} for family: {}", objectiveAssessmentRef,
                                familyHierarchyName);
                    }
                }
                attrs.put("objectiveAssessment", familyObjectiveAssessments);
            }

            if (attrs.containsKey("assessmentItem")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) attrs.get("assessmentItem");
                if (items == null || items.size() == 0) {
                    attrs.remove("assessmentItem");
                }
            }

            String assessmentPeriodDescriptorRef = (String) attrs.remove("periodDescriptorRef");
            if (assessmentPeriodDescriptorRef != null) {
                attrs.put(ASSESSMENT_PERIOD_DESCRIPTOR, getAssessmentPeriodDescriptor(assessmentPeriodDescriptorRef));
            }

            neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");
            neutralRecord.setCreationTime(getWorkNote().getRangeMinimum());
            transformedAssessments.add(neutralRecord);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getAssessmentPeriodDescriptor(String assessmentPeriodDescriptorRef) {
        Query query = new Query().limit(0);
        query.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
        query.addCriteria(Criteria.where("body.codeValue").is(assessmentPeriodDescriptorRef));

        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findAllByQuery(ASSESSMENT_PERIOD_DESCRIPTOR, query);

        if (data.iterator().hasNext()) {
            return data.iterator().next().getAttributes();
        } else {
            Query choice = new Query().limit(0);
            choice.addCriteria(Criteria.where("metaData.tenantId").is(getJob().getTenantId()));
            choice.addCriteria(Criteria.where("body.assessmentPeriodDescriptor.codeValue").is(assessmentPeriodDescriptorRef));
            MongoEntityRepository mongoEntityRepository = getMongoEntityRepository();
            Entity assessmentEntity = mongoEntityRepository.findOne(ASSESSMENT, choice);
            if (assessmentEntity != null) {
                Map<String, Object> assessmentPeriodDescriptor = (Map<String, Object>) assessmentEntity.getBody().get(ASSESSMENT_PERIOD_DESCRIPTOR);
                return assessmentPeriodDescriptor;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String getAssocationFamilyMap(String key, HashMap<String, Map<String, Object>> deepFamilyMap,
            String familyHierarchyName) {
        Query query = new Query().limit(0);
        query.addCriteria(Criteria.where(BATCH_JOB_ID_KEY).is(getBatchJobId()));
        query.addCriteria(Criteria.where("body.AssessmentFamilyIdentificationCode.ID").is(key));
        Iterable<NeutralRecord> data = getNeutralRecordMongoAccess().getRecordRepository().findAllByQuery(ASSESSMENT_FAMILY, query);

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
}
