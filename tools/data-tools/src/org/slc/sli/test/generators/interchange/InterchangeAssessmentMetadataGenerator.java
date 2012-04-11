package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.AssessmentItem;
import org.slc.sli.test.edfi.entities.ComplexObjectType;
import org.slc.sli.test.edfi.entities.InterchangeAssessmentMetadata;
import org.slc.sli.test.edfi.entities.LearningObjective;
import org.slc.sli.test.edfi.entities.LearningStandard;
import org.slc.sli.test.edfi.entities.relations.AssessmentItemMeta;
import org.slc.sli.test.edfi.entities.relations.LearningObjectiveMeta;
import org.slc.sli.test.edfi.entities.relations.LearningStandardMeta;
import org.slc.sli.test.generators.LearningObjectiveGenerator;
import org.slc.sli.test.generators.LearningStandardGenerator;
import org.slc.sli.test.mappingGenerator.AssessmentMetaRelations;
import org.slc.sli.test.mappingGenerator.StateEdFiXmlGenerator;

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
                assessmentItem = AssessmentItemMetaGenerator.generateLowFi(assessmentItemMeta);
            }

            interchangeObjects.add(assessmentItem);
        }

        System.out.println("generated " + assessmentItemMetas.size() + " AssessmentItem objects in: "
                + (System.currentTimeMillis() - startTime));
    }
}
