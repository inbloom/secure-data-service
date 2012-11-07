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


package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.AssessmentReportingMethodType;
import org.slc.sli.test.edfi.entities.ObjectiveAssessmentReferenceType;
import org.slc.sli.test.edfi.entities.PerformanceLevelDescriptorType;
import org.slc.sli.test.edfi.entities.ReferenceType;
import org.slc.sli.test.edfi.entities.ScoreResult;
import org.slc.sli.test.edfi.entities.StudentAssessment;
import org.slc.sli.test.edfi.entities.StudentObjectiveAssessment;
import org.slc.sli.test.edfi.entities.meta.relations.AssessmentMetaRelations;

public class StudentObjectiveAssessmentGenerator {
    private static final Random RANDOM = new Random(31);

    private boolean optional;

    private AssessmentReportingMethodType[] armts = AssessmentReportingMethodType.values();

    public StudentObjectiveAssessmentGenerator(boolean optional) {
        this.optional = optional;
    }

    public StudentObjectiveAssessment generate(String Id, ReferenceType studentTestAssessmentReference,
            ObjectiveAssessmentReferenceType objectiveAssessmentReference) {
        StudentObjectiveAssessment soa = new StudentObjectiveAssessment();

        soa.setId(Id);

        Random random = new Random(31);
        int numberOfScoreResults = 1 + random.nextInt(5);
        for (int i = 0; i < numberOfScoreResults; i++) {
            ScoreResult sr = new ScoreResult();
            sr.setAssessmentReportingMethod(armts[random.nextInt(armts.length)]);
            sr.setResult("result " + random.nextInt());
            soa.getScoreResults().add(sr);
        }

//        soa.setStudentTestAssessmentReference(studentTestAssessmentReference);
        soa.setStudentAssessmentReference(studentTestAssessmentReference);

        soa.setObjectiveAssessmentReference(objectiveAssessmentReference);

        if (optional) {
            // TODO: add PerformanceLevels
        }

        return soa;
    }

    public static StudentObjectiveAssessment generateLowFi(StudentAssessment studentAssessment) {
        StudentObjectiveAssessment soa = new StudentObjectiveAssessment();
        Random random = new Random(31);

        // score results
        ScoreResult scoreResult = new ScoreResult();
        scoreResult.setAssessmentReportingMethod(AssessmentReportingMethodType.values()[random
                .nextInt(AssessmentReportingMethodType.values().length)]);
        scoreResult.setResult("score result");
        soa.getScoreResults().add(scoreResult);

        // performance levels
        String randomPerfLevelDescId = AssessmentMetaRelations.getRandomPerfLevelDescMeta().id;
//        soa.getPerformanceLevels().add(
//                PerformanceLevelDescriptorGenerator.getPerformanceLevelDescriptorType(randomPerfLevelDescId));
        PerformanceLevelDescriptorType pldt = new PerformanceLevelDescriptorType();
        pldt.setPerformanceLevelMet(true);
        pldt.setCodeValue(randomPerfLevelDescId);
//        pldt.setDescription(randomPerfLevelDescId);
        soa.getPerformanceLevels().add(pldt);
        
        // student reference
        ReferenceType studentAssessmentReference = new ReferenceType();
        studentAssessmentReference.setRef(studentAssessment);
//        soa.setStudentTestAssessmentReference(studentAssessmentReference);
        soa.setStudentAssessmentReference(studentAssessmentReference);

        // objective assessment
        String randomObjAssessCode = AssessmentMetaRelations.getRandomObjectiveAssessmentMeta().id;
        soa.setObjectiveAssessmentReference(ObjectiveAssessmentGenerator
                .getObjectiveAssessmentReferenceType(randomObjAssessCode));

        return soa;
    }
}
