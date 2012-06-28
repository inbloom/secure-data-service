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

import org.slc.sli.test.edfi.entities.*;

public class StudentAssessmentItemGenerator {
    private boolean optional;
    private Random random = new Random();

    private AssessmentItemResultType[] airts = AssessmentItemResultType.values();

    public StudentAssessmentItemGenerator(boolean optional) {
        this.optional = optional;
    }

    public StudentAssessmentItem generate(String id, AssessmentItemReferenceType assessmentItemReference, ReferenceType studentTestAssessmentReference, ReferenceType studentObjectiveAssessmentReference) {
        StudentAssessmentItem sai = new StudentAssessmentItem();

        sai.setId(id);

        sai.setAssessmentItemResult(airts[random.nextInt(airts.length)]);

        sai.setAssessmentItemReference(assessmentItemReference);

        if (optional) {
            sai.setRawScoreResult(random.nextInt(100));

            //TODO: StudentTestAssessmentReference
            if (studentTestAssessmentReference != null) {
//                sai.setStudentTestAssessmentReference(studentTestAssessmentReference);
                sai.setStudentAssessmentReference(studentTestAssessmentReference);
            }

            //TODO: StudentObjectiveAssessmentReference
            if (studentObjectiveAssessmentReference != null) {
                sai.setStudentObjectiveAssessmentReference(studentObjectiveAssessmentReference);
            }
        }

        return sai;
    }

    public StudentAssessmentItem generate(String id, AssessmentItemReferenceType assessmentItemReference) {
        return generate(id, assessmentItemReference, null, null);
    }

    public static StudentAssessmentItem generateLowFi(String id, AssessmentItemReferenceType assessmentItemReference) {
        StudentAssessmentItemGenerator saig = new StudentAssessmentItemGenerator(false);
        return saig.generate(id, assessmentItemReference);
    }
}
