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


package org.slc.sli.test.edfi.entities.meta;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicInteger;

public final class StudentAssessmentMeta {

    public final String xmlId;
    public final String studentId;
    public final String assessmentId;
    public static  Calendar calendar;
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    public final String date;
    
    //private static Calendar calendar = new GregorianCalendar(2012, 0, 1);
    //calendar = new GregorianCalendar(2012, 0, 1);

    // TODO: find better way to do unique id. studentId + assessmentId is not enough.
    // can a student take the same assessment on the same date?
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private StudentAssessmentMeta(String studentId, String assessmentId, String date2) {
        this.xmlId = IdTransformer.transformId(studentId + "." + assessmentId + "." + COUNTER.getAndIncrement());
        
        this.studentId = studentId;
        this.assessmentId = assessmentId;
        this.date = date2;
    }

    public static StudentAssessmentMeta create(StudentMeta studentMeta, AssessmentMeta assessmentMeta,  String date) {
          return new StudentAssessmentMeta(studentMeta.id, assessmentMeta.id,date);
    }
    
    public static void resetCalendar() {
        calendar = new GregorianCalendar(2012, 10, 10);
    }

 
//    public static String getDate() {
//       date = DATE_FORMATTER.format(calendar.getTime());
//       return date;
//    }

}
