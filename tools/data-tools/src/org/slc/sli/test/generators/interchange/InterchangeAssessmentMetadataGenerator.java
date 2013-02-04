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


package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slc.sli.test.edfi.entities.SLCAssessment;
import org.slc.sli.test.edfi.entities.AssessmentFamily;
import org.slc.sli.test.edfi.entities.SLCAssessmentItem;
import org.slc.sli.test.edfi.entities.AssessmentPeriodDescriptor;
import org.slc.sli.test.edfi.entities.InterchangeAssessmentMetadata;
import org.slc.sli.test.edfi.entities.SLCLearningObjective;
import org.slc.sli.test.edfi.entities.LearningStandard;
import org.slc.sli.test.edfi.entities.SLCObjectiveAssessment;
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
import org.slc.sli.test.utils.InterchangeWriter;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

public class InterchangeAssessmentMetadataGenerator {

    public static void generate(InterchangeWriter<InterchangeAssessmentMetadata> writer) {
//        InterchangeAssessmentMetadata interchange = new InterchangeAssessmentMetadata();
//        List<ComplexObjectType> interchangeObjects = interchange
//                .getAssessmentFamilyOrAssessmentOrAssessmentPeriodDescriptor();

        writeEntitiesToInterchange(writer);

//        return interchange;
    }

    private static void writeEntitiesToInterchange(InterchangeWriter<InterchangeAssessmentMetadata> writer) {

        generateLearningStandards(writer, AssessmentMetaRelations.LEARN_STD_MAP.values());

        generateLearningObjectives(writer, AssessmentMetaRelations.LEARNING_OBJECTIVE_MAP.values());

        generateAssessmentItems(writer, AssessmentMetaRelations.ASSESSMENT_ITEM_MAP.values());

        generatePerformanceLevelDescriptors(writer, AssessmentMetaRelations.PERF_LEVEL_DESC_MAP.values());

        Map<String, SLCObjectiveAssessment> objAssessMap = generateObjectiveAssessments(writer,
                AssessmentMetaRelations.OBJECTIVE_ASSESSMENT_MAP.values());

        generateAssessmentPeriodDescriptors(writer, AssessmentMetaRelations.ASSESS_PERIOD_DESC_MAP.values());

        generateAssessmentFamilies(writer, AssessmentMetaRelations.ASSESSMENT_FAMILY_MAP.values());

        generateAssessments(writer, AssessmentMetaRelations.ASSESSMENT_MAP.values(), objAssessMap);
    }

    private static void generateLearningStandards(InterchangeWriter<InterchangeAssessmentMetadata> writer,
            Collection<LearningStandardMeta> learningStandardMetas) {
        long startTime = System.currentTimeMillis();

        for (LearningStandardMeta learningStandardMeta : learningStandardMetas) {
            LearningStandard learningStandard;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                learningStandard = null;
            } else {
                learningStandard = LearningStandardGenerator.generateLowFi(learningStandardMeta.id);
            }

            writer.marshal(learningStandard);
        }

        System.out.println("generated " + learningStandardMetas.size() + " LearningStandard objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateLearningObjectives(InterchangeWriter<InterchangeAssessmentMetadata> writer,
            Collection<LearningObjectiveMeta> learningObjectiveMetas) {
        long startTime = System.currentTimeMillis();

        for (LearningObjectiveMeta learningObjectiveMeta : learningObjectiveMetas) {
            SLCLearningObjective learningObjective;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                learningObjective = null;
            } else {
                learningObjective = LearningObjectiveGenerator.generateLowFi(learningObjectiveMeta);
            }

            writer.marshal(learningObjective);
        }

        System.out.println("generated " + learningObjectiveMetas.size() + " LearningObjective objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateAssessmentItems(InterchangeWriter<InterchangeAssessmentMetadata> writer,
            Collection<AssessmentItemMeta> assessmentItemMetas) {
        long startTime = System.currentTimeMillis();

        for (AssessmentItemMeta assessmentItemMeta : assessmentItemMetas) {
            SLCAssessmentItem assessmentItem;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                assessmentItem = null;
            } else {
                assessmentItem = AssessmentItemGenerator.generateLowFi(assessmentItemMeta);
            }

           

            writer.marshal(assessmentItem);
           
        }

        System.out.println("generated " + assessmentItemMetas.size() + " AssessmentItem objects in: "
                + (System.currentTimeMillis() - startTime));
        
        
    }

    private static void generatePerformanceLevelDescriptors(InterchangeWriter<InterchangeAssessmentMetadata> writer,
            Collection<PerformanceLevelDescriptorMeta> perfLevelDescMetas) {
        long startTime = System.currentTimeMillis();

        for (PerformanceLevelDescriptorMeta perfLevelDescMeta : perfLevelDescMetas) {
            PerformanceLevelDescriptor perfLevelDesc;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                perfLevelDesc = null;
            } else {
                perfLevelDesc = PerformanceLevelDescriptorGenerator.generateLowFi(perfLevelDescMeta);
            }

           

            writer.marshal(perfLevelDesc);
          
        }

        System.out.println("generated " + perfLevelDescMetas.size() + " PerformanceLevelDescriptor objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static Map<String, SLCObjectiveAssessment> generateObjectiveAssessments(
    		InterchangeWriter<InterchangeAssessmentMetadata> writer, Collection<ObjectiveAssessmentMeta> objAssessMetas) {
        long startTime = System.currentTimeMillis();

        Map<String, SLCObjectiveAssessment> objAssessMap = new HashMap<String, SLCObjectiveAssessment>();
        for (ObjectiveAssessmentMeta objAssessMeta : objAssessMetas) {
            SLCObjectiveAssessment objectiveAssessment;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                objectiveAssessment = ObjectiveAssessmentGenerator.generateLowFi(objAssessMeta);//update by lina
            } else {
                objectiveAssessment = ObjectiveAssessmentGenerator.generateLowFi(objAssessMeta);
            }

            objAssessMap.put(objectiveAssessment.getId(), objectiveAssessment);

            writer.marshal(objectiveAssessment);
     
        }

        System.out.println("generated " + objAssessMetas.size() + " ObjectiveAssessment objects in: "
                + (System.currentTimeMillis() - startTime));
        return objAssessMap;
    }

    private static void generateAssessmentPeriodDescriptors(InterchangeWriter<InterchangeAssessmentMetadata> writer,
            Collection<AssessmentPeriodDescriptorMeta> assessPeriodDescMetas) {
        long startTime = System.currentTimeMillis();

        for (AssessmentPeriodDescriptorMeta assessPeriodDescMeta : assessPeriodDescMetas) {
            AssessmentPeriodDescriptor assessPeriodDesc;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                assessPeriodDesc = null;
            } else {
                assessPeriodDesc = AssessmentPeriodDescriptorGenerator.generateLowFi(assessPeriodDescMeta);
            }
            writer.marshal(assessPeriodDesc);
    
        }

        System.out.println("generated " + assessPeriodDescMetas.size() + " AssessmentPeriodDescriptor objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateAssessmentFamilies(InterchangeWriter<InterchangeAssessmentMetadata> writer,
            Collection<AssessmentFamilyMeta> assessmentFamilyMetas) {
        long startTime = System.currentTimeMillis();

        for (AssessmentFamilyMeta assessmentFamilyMeta : assessmentFamilyMetas) {
            AssessmentFamily assessmentFamily;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                assessmentFamily = null;
            } else {
                assessmentFamily = AssessmentFamilyGenerator.generateLowFi(assessmentFamilyMeta);
            }


         
            writer.marshal(assessmentFamily);
           
        }

        System.out.println("generated " + assessmentFamilyMetas.size() + " AssessmentFamily objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    private static void generateAssessments(InterchangeWriter<InterchangeAssessmentMetadata> writer,
            Collection<AssessmentMeta> assessmentMetas, Map<String, SLCObjectiveAssessment> objAssessMap) {
        long startTime = System.currentTimeMillis();

        for (AssessmentMeta assessmentMeta : assessmentMetas) {
            SLCAssessment assessment;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                assessment = null;
            } else {
                assessment = AssessmentGenerator.generate(assessmentMeta, objAssessMap);
            }
 

            writer.marshal(assessment);

 
        }

        System.out.println("generated " + assessmentMetas.size() + " Assessment objects in: "
                + (System.currentTimeMillis() - startTime));
    }
}
