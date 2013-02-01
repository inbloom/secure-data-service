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


package org.slc.sli.test.edfi.entities.meta.relations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.slc.sli.test.edfi.entities.meta.AssessmentFamilyMeta;
import org.slc.sli.test.edfi.entities.meta.AssessmentItemMeta;
import org.slc.sli.test.edfi.entities.meta.AssessmentMeta;
import org.slc.sli.test.edfi.entities.meta.AssessmentPeriodDescriptorMeta;
import org.slc.sli.test.edfi.entities.meta.LearningObjectiveMeta;
import org.slc.sli.test.edfi.entities.meta.LearningStandardMeta;
import org.slc.sli.test.edfi.entities.meta.ObjectiveAssessmentMeta;
import org.slc.sli.test.edfi.entities.meta.PerformanceLevelDescriptorMeta;

public class AssessmentMetaRelations {

    // knobs to control number of entities to create
    public static int ASSESSMENTS = 1;
    public static int OBJ_ASSESS_PER_DEPENDANT = 1;
    public static int LEARN_OBJ_PER_OBJ_ASSES = 1;
    public static int ASSESS_ITEM_PER_DEPENDANT = 3;
    public static int LEARN_STANDARD_PER_DEPENDANT = 1;
    public static int PERF_LEVEL_DESC_PER_DEPENDANT = 1;
    public static int ASSESS_PERIOD_DESC_PER_ASSESS_FAMILY = 1;
    public static int INV_PROBABILITY_STUDENTASSESSMENT_HAS_OBJECTIVEASSESSMENT = 2;
    public static int INV_PROBABILITY_STUDENTASSESSMENT_HAS_STUDENTASSESSMENTITEM = 2;

    // publicly accessible structures for the "meta-skeleton" entities
    public static final Map<String, AssessmentMeta> ASSESSMENT_MAP = new TreeMap<String, AssessmentMeta>();
    public static final Map<String, ObjectiveAssessmentMeta> OBJECTIVE_ASSESSMENT_MAP = new TreeMap<String, ObjectiveAssessmentMeta>();
    public static final Map<String, LearningObjectiveMeta> LEARNING_OBJECTIVE_MAP = new TreeMap<String, LearningObjectiveMeta>();
    public static final Map<String, AssessmentItemMeta> ASSESSMENT_ITEM_MAP = new TreeMap<String, AssessmentItemMeta>();
    public static final Map<String, LearningStandardMeta> LEARN_STD_MAP = new TreeMap<String, LearningStandardMeta>();
    public static final Map<String, PerformanceLevelDescriptorMeta> PERF_LEVEL_DESC_MAP = new TreeMap<String, PerformanceLevelDescriptorMeta>();
    public static final Map<String, AssessmentFamilyMeta> ASSESSMENT_FAMILY_MAP = new TreeMap<String, AssessmentFamilyMeta>();
    public static final Map<String, AssessmentPeriodDescriptorMeta> ASSESS_PERIOD_DESC_MAP = new TreeMap<String, AssessmentPeriodDescriptorMeta>();

    // constants
    private static final Random RANDOM = new Random(31);
    private static final String ASSESS_PREFIX = "as";
    private static final String OBJ_ASSESS_PREFIX = "objas";
    private static final String LEARN_OBJ_PREFIX = "lobj";
    private static final String ASSESSMENT_ITEM_PREFIX = "asit";
    private static final String LEARN_STD_PREFIX = "lstd";
    private static final String PERF_LEVEL_DESC_PREFIX = "pld";
    private static final String ASSESS_FAMILY_PREFIX = "asf";
    private static final String ASSESS_PERIOD_DESC_PREFIX = "apd";

    public static void buildStandaloneAssessments() {
        long startTime = System.currentTimeMillis();

        buildAssessments();

        System.out.println("Time taken to build Assessment entity relations: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void buildAssessments() {

        for (int idNum = 0; idNum < ASSESSMENTS; idNum++) {

            AssessmentMeta assessmentMeta = AssessmentMeta.create(ASSESS_PREFIX + idNum);

            // TODO: sections

            for (ObjectiveAssessmentMeta objAssessMeta : aggregateObjectiveAssessments()) {
                assessmentMeta.objectiveAssessmentIds.add(objAssessMeta.id);
            }

            for (AssessmentItemMeta assessmentItemMeta : aggregateAssessmentItems()) {
                assessmentMeta.assessmentItemIds.add(assessmentItemMeta.id);
                assessmentItemMeta.setAssessmentId(assessmentMeta.id);
            }

            for (PerformanceLevelDescriptorMeta perfLevelDescMeta : aggregatePerfLevelDesc()) {
                assessmentMeta.performanceLevelDescriptorIds.add(perfLevelDescMeta.id);
            }

            assessmentMeta.assessmentFamilyId = getOrCreateAssessmentFamily().id;

            assessmentMeta.assessmentPeriodDescriptorId = getOrCreateAssessmentPeriodDescriptor().id;

            ASSESSMENT_MAP.put(assessmentMeta.id, assessmentMeta);
        }
    }

    private static List<ObjectiveAssessmentMeta> aggregateObjectiveAssessments() {

        List<ObjectiveAssessmentMeta> objAssessList = new ArrayList<ObjectiveAssessmentMeta>();
        for (int idNum = 0; idNum < OBJ_ASSESS_PER_DEPENDANT; idNum++) {

            objAssessList.add(getOrCreateObjectiveAssessment());
        }
        return objAssessList;
    }

    private static List<LearningObjectiveMeta> aggregateLearningObjectivesForObjAssess() {

        List<LearningObjectiveMeta> learningObjectiveList = new ArrayList<LearningObjectiveMeta>();
        for (int idNum = 0; idNum < LEARN_OBJ_PER_OBJ_ASSES; idNum++) {

            learningObjectiveList.add(getOrCreateLearningObjective());
        }
        return learningObjectiveList;
    }

    private static List<AssessmentItemMeta> aggregateAssessmentItems() {

        List<AssessmentItemMeta> assessmentItemList = new ArrayList<AssessmentItemMeta>();
        for (int idNum = 0; idNum < ASSESS_ITEM_PER_DEPENDANT; idNum++) {

            assessmentItemList.add(getOrCreateAssessmentItem());
        }
        return assessmentItemList;
    }

    private static List<LearningStandardMeta> aggregateLearningStandards() {

        List<LearningStandardMeta> learningStandardList = new ArrayList<LearningStandardMeta>();
        for (int idNum = 0; idNum < LEARN_STANDARD_PER_DEPENDANT; idNum++) {

            learningStandardList.add(getOrCreateLearningStandard());
        }
        return learningStandardList;
    }

    private static List<PerformanceLevelDescriptorMeta> aggregatePerfLevelDesc() {

        List<PerformanceLevelDescriptorMeta> perfLevelDescList = new ArrayList<PerformanceLevelDescriptorMeta>();
        for (int idNum = 0; idNum < PERF_LEVEL_DESC_PER_DEPENDANT; idNum++) {

            perfLevelDescList.add(getOrCreatePerfLevelDesc());
        }
        return perfLevelDescList;
    }

    private static List<AssessmentPeriodDescriptorMeta> aggregateAssessmentPeriodDescriptors() {

        List<AssessmentPeriodDescriptorMeta> assessPeriodDescList = new ArrayList<AssessmentPeriodDescriptorMeta>();
        for (int idNum = 0; idNum < ASSESS_PERIOD_DESC_PER_ASSESS_FAMILY; idNum++) {

            assessPeriodDescList.add(getOrCreateAssessmentPeriodDescriptor());
        }
        return assessPeriodDescList;
    }

    private static ObjectiveAssessmentMeta getOrCreateObjectiveAssessment() {
        ObjectiveAssessmentMeta objAssessMeta = null;

        Random random = new Random(31);

        if (random.nextBoolean()) {
            objAssessMeta = getRandomObjectiveAssessmentMeta();
        }

        if (objAssessMeta == null) {
            objAssessMeta = createObjectiveAssessment();
        }
        return objAssessMeta;
    }

    private static LearningObjectiveMeta getOrCreateLearningObjective() {
        LearningObjectiveMeta learningObjectiveMeta = null;

        Random random = new Random(31);

        if (random.nextBoolean()) {
            learningObjectiveMeta = getRandomLearningObjectiveMeta();
        }

        if (learningObjectiveMeta == null) {
            learningObjectiveMeta = createLearningObjective();
        }
        return learningObjectiveMeta;
    }

    private static AssessmentItemMeta getOrCreateAssessmentItem() {
        AssessmentItemMeta assessmentItemMeta = null;

        Random random = new Random(31);

        if (random.nextBoolean()) {
            assessmentItemMeta = getRandomAssessmentItemMeta();
        }

        if (assessmentItemMeta == null) {
            assessmentItemMeta = createAssessmentItem();
        }
        return assessmentItemMeta;
    }

    private static LearningStandardMeta getOrCreateLearningStandard() {
        LearningStandardMeta learningStandardMeta = null;

        Random random = new Random(31);

        if (random.nextBoolean()) {
            learningStandardMeta = getRandomLearningStandardMeta();
        }

        if (learningStandardMeta == null) {
            learningStandardMeta = createLearningStandard();
        }
        return learningStandardMeta;
    }

    private static PerformanceLevelDescriptorMeta getOrCreatePerfLevelDesc() {
        PerformanceLevelDescriptorMeta perfLevelDescMeta = null;

        Random random = new Random(31);

        if (random.nextBoolean()) {
            perfLevelDescMeta = getRandomPerfLevelDescMeta();
        }

        if (perfLevelDescMeta == null) {
            perfLevelDescMeta = createPerformanceLevelDescriptor();
        }
        return perfLevelDescMeta;
    }

    private static AssessmentFamilyMeta getOrCreateAssessmentFamily() {
        AssessmentFamilyMeta assessmentFamilyMeta = null;

        Random random = new Random(31);

        if (random.nextBoolean()) {
            assessmentFamilyMeta = getRandomAssessmentFamilyMeta();
        }

        if (assessmentFamilyMeta == null) {
            assessmentFamilyMeta = createAssessmentFamily();
        }
        return assessmentFamilyMeta;
    }

    private static AssessmentPeriodDescriptorMeta getOrCreateAssessmentPeriodDescriptor() {
        AssessmentPeriodDescriptorMeta assessPeriodDescMeta = null;

        Random random = new Random(31);

        if (random.nextBoolean()) {
            assessPeriodDescMeta = getRandomAssessmentPeriodDescriptorMeta();
        }

        if (assessPeriodDescMeta == null) {
            assessPeriodDescMeta = createAssessmentPeriodDescriptor();
        }
        return assessPeriodDescMeta;
    }

    private static ObjectiveAssessmentMeta createObjectiveAssessment() {
        ObjectiveAssessmentMeta objAssessMeta = ObjectiveAssessmentMeta.create(OBJ_ASSESS_PREFIX
                + OBJECTIVE_ASSESSMENT_MAP.size());

        for (LearningObjectiveMeta learningObjectiveMeta : aggregateLearningObjectivesForObjAssess()) {
            objAssessMeta.learningObjectiveIds.add(learningObjectiveMeta.id);
        }

        for (AssessmentItemMeta assessmentItemMeta : aggregateAssessmentItems()) {
            objAssessMeta.assessmentItemIds.add(assessmentItemMeta.id);
        }

        for (LearningStandardMeta learningStandardMeta : aggregateLearningStandards()) {
            objAssessMeta.learningStandardIds.add(learningStandardMeta.id);
        }

        for (PerformanceLevelDescriptorMeta perfLevelDescMeta : aggregatePerfLevelDesc()) {
            objAssessMeta.performanceLevelDescriptorIds.add(perfLevelDescMeta.id);
        }

        OBJECTIVE_ASSESSMENT_MAP.put(objAssessMeta.id, objAssessMeta);
        return objAssessMeta;
    }

    private static LearningObjectiveMeta createLearningObjective() {
        LearningObjectiveMeta learningObjectiveMeta = LearningObjectiveMeta.create(LEARN_OBJ_PREFIX
                + LEARNING_OBJECTIVE_MAP.size());

        for (LearningStandardMeta learningStandardMeta : aggregateLearningStandards()) {
            learningObjectiveMeta.learningStandardIds.add(learningStandardMeta.id);
        }
        LEARNING_OBJECTIVE_MAP.put(learningObjectiveMeta.id, learningObjectiveMeta);
        return learningObjectiveMeta;
    }

    private static AssessmentItemMeta createAssessmentItem() {
        AssessmentItemMeta assessmentItemMeta = AssessmentItemMeta.create(ASSESSMENT_ITEM_PREFIX
                + ASSESSMENT_ITEM_MAP.size());

        for (LearningStandardMeta learningStandardMeta : aggregateLearningStandards()) {
            assessmentItemMeta.learningStandardIds.add(learningStandardMeta.id);
        }
        
        ASSESSMENT_ITEM_MAP.put(assessmentItemMeta.id, assessmentItemMeta);
        return assessmentItemMeta;
    }

    private static LearningStandardMeta createLearningStandard() {
        LearningStandardMeta learningStandardMeta = LearningStandardMeta
                .create(LEARN_STD_PREFIX + LEARN_STD_MAP.size());
        LEARN_STD_MAP.put(learningStandardMeta.id, learningStandardMeta);
        return learningStandardMeta;
    }

    private static PerformanceLevelDescriptorMeta createPerformanceLevelDescriptor() {
        PerformanceLevelDescriptorMeta perfLevelDescMeta = PerformanceLevelDescriptorMeta.create(PERF_LEVEL_DESC_PREFIX
                + PERF_LEVEL_DESC_MAP.size());
        PERF_LEVEL_DESC_MAP.put(perfLevelDescMeta.id, perfLevelDescMeta);
        return perfLevelDescMeta;
    }

    private static AssessmentFamilyMeta createAssessmentFamily() {
        AssessmentFamilyMeta assessmentFamilyMeta = AssessmentFamilyMeta.create(ASSESS_FAMILY_PREFIX
                + ASSESSMENT_FAMILY_MAP.size());

        for (AssessmentPeriodDescriptorMeta assessPeriodDescMeta : aggregateAssessmentPeriodDescriptors()) {
            assessmentFamilyMeta.assessmentPeriodDescriptorIds.add(assessPeriodDescMeta.id);
        }
        ASSESSMENT_FAMILY_MAP.put(assessmentFamilyMeta.id, assessmentFamilyMeta);
        return assessmentFamilyMeta;
    }

    private static AssessmentPeriodDescriptorMeta createAssessmentPeriodDescriptor() {
        AssessmentPeriodDescriptorMeta assessPeriodDescMeta = AssessmentPeriodDescriptorMeta
                .create(ASSESS_PERIOD_DESC_PREFIX + ASSESS_PERIOD_DESC_MAP.size());
        ASSESS_PERIOD_DESC_MAP.put(assessPeriodDescMeta.id, assessPeriodDescMeta);
        return assessPeriodDescMeta;
    }

    public static AssessmentMeta getRandomAssessmentMeta() {
        AssessmentMeta random = null;
        Random seededRandom = new Random(31);
        if (ASSESSMENT_MAP.size() > 0) {
            random = ASSESSMENT_MAP.get(ASSESS_PREFIX + seededRandom.nextInt(ASSESSMENT_MAP.size()));
        }
        return random;
    }

    public static ObjectiveAssessmentMeta getRandomObjectiveAssessmentMeta() {
        ObjectiveAssessmentMeta random = null;
        Random seededRandom = new Random(31);
        if (OBJECTIVE_ASSESSMENT_MAP.size() > 0) {
            random = OBJECTIVE_ASSESSMENT_MAP.get(OBJ_ASSESS_PREFIX + seededRandom.nextInt(OBJECTIVE_ASSESSMENT_MAP.size()));
        }
        return random;
    }

    public static LearningObjectiveMeta getRandomLearningObjectiveMeta() {
        LearningObjectiveMeta random = null;
        Random seededRandom = new Random(31);
        if (LEARNING_OBJECTIVE_MAP.size() > 0) {
            random = LEARNING_OBJECTIVE_MAP.get(LEARN_OBJ_PREFIX + seededRandom.nextInt(LEARNING_OBJECTIVE_MAP.size()));
        }
        return random;
    }

    public static AssessmentItemMeta getRandomAssessmentItemMeta() {
        AssessmentItemMeta random = null;
        Random seededRandom = new Random(31);
        if (ASSESSMENT_ITEM_MAP.size() > 0) {
            random = ASSESSMENT_ITEM_MAP.get(ASSESSMENT_ITEM_PREFIX + seededRandom.nextInt(ASSESSMENT_ITEM_MAP.size()));
        }
        return random;
    }

    public static LearningStandardMeta getRandomLearningStandardMeta() {
        Random seededRandom = new Random(31);
        LearningStandardMeta random = null;
        if (LEARN_STD_MAP.size() > 0) {
            random = LEARN_STD_MAP.get(LEARN_STD_PREFIX + seededRandom.nextInt(LEARN_STD_MAP.size()));
            
        }
        return random;
    }

    public static PerformanceLevelDescriptorMeta getRandomPerfLevelDescMeta() {
        PerformanceLevelDescriptorMeta random = null;
        Random seededRandom = new Random(31);
        if (PERF_LEVEL_DESC_MAP.size() > 0) {
            random = PERF_LEVEL_DESC_MAP.get(PERF_LEVEL_DESC_PREFIX + seededRandom.nextInt(PERF_LEVEL_DESC_MAP.size()));
        }
        return random;
    }

    public static AssessmentFamilyMeta getRandomAssessmentFamilyMeta() {
        AssessmentFamilyMeta random = null;
        Random seededRandom = new Random(31);
        if (ASSESSMENT_FAMILY_MAP.size() > 0) {
            random = ASSESSMENT_FAMILY_MAP.get(ASSESS_FAMILY_PREFIX + seededRandom.nextInt(ASSESSMENT_FAMILY_MAP.size()));
        }
        return random;
    }

    public static AssessmentPeriodDescriptorMeta getRandomAssessmentPeriodDescriptorMeta() {
        AssessmentPeriodDescriptorMeta random = null;
        Random seededRandom = new Random(31);
        if (ASSESS_PERIOD_DESC_MAP.size() > 0) {
            random = ASSESS_PERIOD_DESC_MAP.get(ASSESS_PERIOD_DESC_PREFIX
                    + seededRandom.nextInt(ASSESS_PERIOD_DESC_MAP.size()));
        }
        return random;
    }

    public static void main(String[] args) {

        buildStandaloneAssessments();

        System.out.println(ASSESSMENT_MAP);
        System.out.println(OBJECTIVE_ASSESSMENT_MAP);
        System.out.println(LEARNING_OBJECTIVE_MAP);
        System.out.println(ASSESSMENT_ITEM_MAP);
        System.out.println(LEARN_STD_MAP);
        System.out.println(PERF_LEVEL_DESC_MAP);
        System.out.println(ASSESSMENT_FAMILY_MAP);
        System.out.println(ASSESS_PERIOD_DESC_MAP);

    }
}
