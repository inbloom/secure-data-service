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


package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.SLCLearningObjective;
import org.slc.sli.test.edfi.entities.SLCLearningObjectiveIdentityType;
import org.slc.sli.test.edfi.entities.SLCLearningObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.LearningStandardId;
import org.slc.sli.test.edfi.entities.SLCLearningStandardIdentityType;
import org.slc.sli.test.edfi.entities.SLCLearningStandardReferenceType;
import org.slc.sli.test.edfi.entities.meta.LearningObjectiveMeta;

public class LearningObjectiveGenerator {

    private int loId = 0;
    private SLCLearningStandardReferenceType learningStandardRef;
    private LearningStandardId learningStandardId;
    private static int i = 0;
    
    public LearningObjectiveGenerator() {
        SLCLearningStandardIdentityType lsIdentity = new SLCLearningStandardIdentityType();
//        lsIdentity.setLearningStandardId("Learning Standard Content Standard G1");
        // TODO match up this id to a valid learningStandard when needed
        lsIdentity.setIdentificationCode("ls" + i);
        i = i + 1;
        learningStandardRef.setLearningStandardIdentity(lsIdentity);
    }

    public SLCLearningObjective getLearningObjective(String learningObjectiveId) {
        loId++;
        SLCLearningObjective lo = new SLCLearningObjective();
        String id = learningObjectiveId == null ? ("LOID" + loId) : learningObjectiveId;
        lo.setId(id);

        LearningStandardId learningStdIdForObjective = new LearningStandardId();
        learningStdIdForObjective.setContentStandardName("Learning Standard Content Standard");
        learningStdIdForObjective.setIdentificationCode("Objective" + loId);

        lo.setLearningObjectiveId(learningStandardId);
        lo.setObjective("Learning Objective " + loId);
        lo.setDescription("Learning Objective Desciption " + loId);
        lo.setAcademicSubject(AcademicSubjectType.AGRICULTURE_FOOD_AND_NATURAL_RESOURCES);
        lo.setObjectiveGradeLevel(GradeLevelType.OTHER);
        lo.getLearningStandardReference().add(learningStandardRef);

        return lo;
    }

    public static SLCLearningObjectiveReferenceType getLearningObjectiveReferenceType(SLCLearningObjective lo) {
        SLCLearningObjectiveIdentityType loId = new SLCLearningObjectiveIdentityType();
        loId.setObjective(lo.getObjective());
        loId.setObjectiveGradeLevel(lo.getObjectiveGradeLevel());
        loId.setAcademicSubject(lo.getAcademicSubject());
        SLCLearningObjectiveReferenceType lor = new SLCLearningObjectiveReferenceType();
        lor.setLearningObjectiveIdentity(loId);
        return lor;
    }

    public static SLCLearningObjective generateLowFi(LearningObjectiveMeta learningObjectiveMeta) {
        SLCLearningObjective lo = new SLCLearningObjective();

        LearningStandardId learningStdIdForObjective = new LearningStandardId();
        learningStdIdForObjective.setContentStandardName("Content Standard Name");
        learningStdIdForObjective.setIdentificationCode(learningObjectiveMeta.id);
        lo.setLearningObjectiveId(learningStdIdForObjective);

        lo.setObjective(learningObjectiveMeta.id);
        lo.setDescription("Learning Objective Desciption " + learningObjectiveMeta.id);
        lo.setAcademicSubject(AcademicSubjectType.AGRICULTURE_FOOD_AND_NATURAL_RESOURCES);
        lo.setObjectiveGradeLevel(GradeLevelType.OTHER);

        for (String learningStandardId : learningObjectiveMeta.learningStandardIds) {
            lo.getLearningStandardReference().add(
                    LearningStandardGenerator.getLearningStandardReferenceType(learningStandardId));
        }

        return lo;
    }
}
