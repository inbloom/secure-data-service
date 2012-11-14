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

import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.LearningObjective;
import org.slc.sli.test.edfi.entities.LearningObjectiveIdentityType;
import org.slc.sli.test.edfi.entities.LearningObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.LearningStandardId;
import org.slc.sli.test.edfi.entities.LearningStandardIdentityType;
import org.slc.sli.test.edfi.entities.LearningStandardReferenceType;
import org.slc.sli.test.edfi.entities.meta.LearningObjectiveMeta;

public class LearningObjectiveGenerator {

    private int loId = 0;
    private LearningStandardReferenceType learningStandardRef;
    private LearningStandardId learningStandardId;

    public LearningObjectiveGenerator() {
        LearningStandardIdentityType lsIdentity = new LearningStandardIdentityType();
        lsIdentity.setLearningStandardId("Learning Standard Content Standard G1");
        learningStandardRef.setLearningStandardIdentity(lsIdentity);
    }

    public LearningObjective getLearningObjective(String learningObjectiveId) {
        loId++;
        LearningObjective lo = new LearningObjective();
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

    public static LearningObjectiveReferenceType getLearningObjectiveReferenceType(LearningObjective lo) {
        LearningObjectiveIdentityType loId = new LearningObjectiveIdentityType();
        loId.setObjective(lo.getObjective());
        loId.setObjectiveGradeLevel(lo.getObjectiveGradeLevel());
        loId.setAcademicSubject(lo.getAcademicSubject());
        LearningObjectiveReferenceType lor = new LearningObjectiveReferenceType();
        lor.setLearningObjectiveIdentity(loId);
        return lor;
    }

    public static LearningObjective generateLowFi(LearningObjectiveMeta learningObjectiveMeta) {
        LearningObjective lo = new LearningObjective();

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
