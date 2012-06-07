package org.slc.sli.test.edfi.entities.meta;

import java.util.concurrent.atomic.AtomicInteger;

public final class StudentAssessmentMeta {

    public final String xmlId;
    public final String studentId;
    public final String assessmentId;

    // TODO: find better way to do unique id. studentId + assessmentId is not enough.
    // can a student take the same assessment on the same date?
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private StudentAssessmentMeta(String studentId, String assessmentId) {
        this.xmlId = IdTransformer.transformId(studentId + "." + assessmentId + "." + COUNTER.getAndIncrement());
        this.studentId = studentId;
        this.assessmentId = assessmentId;
    }

    public static StudentAssessmentMeta create(StudentMeta studentMeta, AssessmentMeta assessmentMeta) {
        return new StudentAssessmentMeta(studentMeta.id, assessmentMeta.id);
    }

}
