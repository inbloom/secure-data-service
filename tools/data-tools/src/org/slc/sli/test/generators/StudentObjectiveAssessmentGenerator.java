package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.*;

public class StudentObjectiveAssessmentGenerator {
    private boolean optional;
    private Random random = new Random();

    private AssessmentReportingMethodType[] armts = AssessmentReportingMethodType.values();

    public StudentObjectiveAssessmentGenerator(boolean optional) {
        this.optional = optional;
    }

    public StudentObjectiveAssessment generate(String Id, ReferenceType studentTestAssessmentReference, ObjectiveAssessmentReferenceType objectiveAssessmentReference) {
        StudentObjectiveAssessment soa = new StudentObjectiveAssessment();

        soa.setId(Id);

        int numberOfScoreResults = 1 + random.nextInt(5);
        for (int i = 0 ; i < numberOfScoreResults; i++) {
            ScoreResult sr = new ScoreResult();
            sr.setAssessmentReportingMethod(armts[random.nextInt(armts.length)]);
            sr.setResult("result " + random.nextInt());
            soa.getScoreResults().add(sr);
        }

        soa.setStudentTestAssessmentReference(studentTestAssessmentReference);

        soa.setObjectiveAssessmentReference(objectiveAssessmentReference);

        if (optional) {
            // TODO: add PerformanceLevels
        }

        return soa;
    }

    public static StudentObjectiveAssessment generateLoFi(String Id, ReferenceType studentTestAssessmentReference, ObjectiveAssessmentReferenceType objectiveAssessmentReference) {
        StudentObjectiveAssessmentGenerator soag = new StudentObjectiveAssessmentGenerator(false);
        return soag.generate(Id, studentTestAssessmentReference, objectiveAssessmentReference);
    }
}
