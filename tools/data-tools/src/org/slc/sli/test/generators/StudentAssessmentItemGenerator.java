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
