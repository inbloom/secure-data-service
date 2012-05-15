package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.test.edfi.entities.Assessment;
import org.slc.sli.test.edfi.entities.AssessmentFamily;
import org.slc.sli.test.edfi.entities.AssessmentItem;
import org.slc.sli.test.edfi.entities.AssessmentPeriodDescriptor;
import org.slc.sli.test.edfi.entities.ComplexObjectType;
import org.slc.sli.test.edfi.entities.InterchangeAssessmentMetadata;
import org.slc.sli.test.edfi.entities.LearningObjective;
import org.slc.sli.test.edfi.entities.LearningStandard;
import org.slc.sli.test.edfi.entities.ObjectiveAssessment;
import org.slc.sli.test.edfi.entities.PerformanceLevelDescriptor;
import org.slc.sli.test.edfi.entities.meta.AssessmentFamilyMeta;
import org.slc.sli.test.edfi.entities.meta.AssessmentItemMeta;
import org.slc.sli.test.edfi.entities.meta.AssessmentMeta;
import org.slc.sli.test.edfi.entities.meta.AssessmentPeriodDescriptorMeta;
import org.slc.sli.test.edfi.entities.meta.LearningObjectiveMeta;
import org.slc.sli.test.edfi.entities.meta.LearningStandardMeta;
import org.slc.sli.test.edfi.entities.meta.ObjectiveAssessmentMeta;
import org.slc.sli.test.edfi.entities.meta.PerformanceLevelDescriptorMeta;
import org.slc.sli.test.edfi.entities.meta.relations.AssessmentMetaRelations;
import org.slc.sli.test.generators.AssessmentFamilyGenerator;
import org.slc.sli.test.generators.AssessmentGenerator;
import org.slc.sli.test.generators.AssessmentItemGenerator;
import org.slc.sli.test.generators.AssessmentPeriodDescriptorGenerator;
import org.slc.sli.test.generators.LearningObjectiveGenerator;
import org.slc.sli.test.generators.LearningStandardGenerator;
import org.slc.sli.test.generators.ObjectiveAssessmentGenerator;
import org.slc.sli.test.generators.PerformanceLevelDescriptorGenerator;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

public class InterchangeAssessmentMetadataGenerator {

    public static InterchangeAssessmentMetadata generate() {
        InterchangeAssessmentMetadata interchange = new InterchangeAssessmentMetadata();
        List<ComplexObjectType> interchangeObjects = interchange
                .getAssessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor();

        addEntitiesToInterchange(interchangeObjects);

        return interchange;
    }

    private static void addEntitiesToInterchange(List<ComplexObjectType> interchangeObjects) {

        generateLearningStandards(interchangeObjects, AssessmentMetaRelations.LEARN_STD_MAP.values());

        generateLearningObjectives(interchangeObjects, AssessmentMetaRelations.LEARNING_OBJECTIVE_MAP.values());

        generateAssessmentItems(interchangeObjects, AssessmentMetaRelations.ASSESSMENT_ITEM_MAP.values());

        generatePerformanceLevelDescriptors(interchangeObjects, AssessmentMetaRelations.PERF_LEVEL_DESC_MAP.values());

        Map<String, ObjectiveAssessment> objAssessMap = generateObjectiveAssessments(interchangeObjects,
                AssessmentMetaRelations.OBJECTIVE_ASSESSMENT_MAP.values());

        generateAssessmentPeriodDescriptors(interchangeObjects, AssessmentMetaRelations.ASSESS_PERIOD_DESC_MAP.values());

        generateAssessmentFamilies(interchangeObjects, AssessmentMetaRelations.ASSESSMENT_FAMILY_MAP.values());

        generateAssessments(interchangeObjects, AssessmentMetaRelations.ASSESSMENT_MAP.values(), objAssessMap);
    }

    private static void generateLearningStandards(List<ComplexObjectType> interchangeObjects,
            Collection<LearningStandardMeta> learningStandardMetas) {
        long startTime = System.currentTimeMillis();

        for (LearningStandardMeta learningStandardMeta : learningStandardMetas) {
            LearningStandard learningStandard;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                learningStandard = null;
            } else {
                learningStandard = LearningStandardGenerator.generateLowFi(learningStandardMeta.id);
            }

            interchangeObjects.add(learningStandard);
        }

        System.out.println("generated " + learningStandardMetas.size() + " LearningStandard objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateLearningObjectives(List<ComplexObjectType> interchangeObjects,
            Collection<LearningObjectiveMeta> learningObjectiveMetas) {
        long startTime = System.currentTimeMillis();

        for (LearningObjectiveMeta learningObjectiveMeta : learningObjectiveMetas) {
            LearningObjective learningObjective;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                learningObjective = null;
            } else {
                learningObjective = LearningObjectiveGenerator.generateLowFi(learningObjectiveMeta);
            }

            interchangeObjects.add(learningObjective);
        }

        System.out.println("generated " + learningObjectiveMetas.size() + " LearningObjective objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateAssessmentItems(List<ComplexObjectType> interchangeObjects,
            Collection<AssessmentItemMeta> assessmentItemMetas) {
        long startTime = System.currentTimeMillis();

        for (AssessmentItemMeta assessmentItemMeta : assessmentItemMetas) {
            AssessmentItem assessmentItem;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                assessmentItem = null;
            } else {
                assessmentItem = AssessmentItemGenerator.generateLowFi(assessmentItemMeta);
            }

            interchangeObjects.add(assessmentItem);
        }

        System.out.println("generated " + assessmentItemMetas.size() + " AssessmentItem objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generatePerformanceLevelDescriptors(List<ComplexObjectType> interchangeObjects,
            Collection<PerformanceLevelDescriptorMeta> perfLevelDescMetas) {
        long startTime = System.currentTimeMillis();

        for (PerformanceLevelDescriptorMeta perfLevelDescMeta : perfLevelDescMetas) {
            PerformanceLevelDescriptor perfLevelDesc;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                perfLevelDesc = null;
            } else {
                perfLevelDesc = PerformanceLevelDescriptorGenerator.generateLowFi(perfLevelDescMeta);
            }

            interchangeObjects.add(perfLevelDesc);
        }

        System.out.println("generated " + perfLevelDescMetas.size() + " PerformanceLevelDescriptor objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static Map<String, ObjectiveAssessment> generateObjectiveAssessments(
            List<ComplexObjectType> interchangeObjects, Collection<ObjectiveAssessmentMeta> objAssessMetas) {
        long startTime = System.currentTimeMillis();

        Map<String, ObjectiveAssessment> objAssessMap = new HashMap<String, ObjectiveAssessment>();
        for (ObjectiveAssessmentMeta objAssessMeta : objAssessMetas) {
            ObjectiveAssessment objectiveAssessment;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                objectiveAssessment = ObjectiveAssessmentGenerator.generateLowFi(objAssessMeta);//update by lina
            } else {
                objectiveAssessment = ObjectiveAssessmentGenerator.generateLowFi(objAssessMeta);
            }

            objAssessMap.put(objectiveAssessment.getId(), objectiveAssessment);
            interchangeObjects.add(objectiveAssessment);
        }

        System.out.println("generated " + objAssessMetas.size() + " ObjectiveAssessment objects in: "
                + (System.currentTimeMillis() - startTime));
        return objAssessMap;
    }

    private static void generateAssessmentPeriodDescriptors(List<ComplexObjectType> interchangeObjects,
            Collection<AssessmentPeriodDescriptorMeta> assessPeriodDescMetas) {
        long startTime = System.currentTimeMillis();

        for (AssessmentPeriodDescriptorMeta assessPeriodDescMeta : assessPeriodDescMetas) {
            AssessmentPeriodDescriptor assessPeriodDesc;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                assessPeriodDesc = null;
            } else {
                assessPeriodDesc = AssessmentPeriodDescriptorGenerator.generateLowFi(assessPeriodDescMeta);
            }

            interchangeObjects.add(assessPeriodDesc);
        }

        System.out.println("generated " + assessPeriodDescMetas.size() + " AssessmentPeriodDescriptor objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateAssessmentFamilies(List<ComplexObjectType> interchangeObjects,
            Collection<AssessmentFamilyMeta> assessmentFamilyMetas) {
        long startTime = System.currentTimeMillis();

        for (AssessmentFamilyMeta assessmentFamilyMeta : assessmentFamilyMetas) {
            AssessmentFamily assessmentFamily;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                assessmentFamily = null;
            } else {
                assessmentFamily = AssessmentFamilyGenerator.generateLowFi(assessmentFamilyMeta);
            }

            interchangeObjects.add(assessmentFamily);
        }

        System.out.println("generated " + assessmentFamilyMetas.size() + " AssessmentFamily objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateAssessments(List<ComplexObjectType> interchangeObjects,
            Collection<AssessmentMeta> assessmentMetas, Map<String, ObjectiveAssessment> objAssessMap) {
        long startTime = System.currentTimeMillis();

        for (AssessmentMeta assessmentMeta : assessmentMetas) {
            Assessment assessment;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                assessment = null;
            } else {
                assessment = AssessmentGenerator.generate(assessmentMeta, objAssessMap);
            }

            interchangeObjects.add(assessment);
        }

        System.out.println("generated " + assessmentMetas.size() + " Assessment objects in: "
                + (System.currentTimeMillis() - startTime));
    }
}
