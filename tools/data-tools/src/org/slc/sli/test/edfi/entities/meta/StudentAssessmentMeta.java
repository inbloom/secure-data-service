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
