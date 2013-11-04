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
package org.slc.sli.bulk.extract.date;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;

/**
 * Creates a new ExtractVerifier instance based upon entity type.
 *
 * @author tke tshewchuk
 */
@Component
public class ExtractVerifierFactory {

    private static ExtractVerifier simpleExtractVerifier;

    private static ExtractVerifier pathExtractVerifier;

    private static ExtractVerifier teacherSchoolAssociationExtractVerifier;

    private static ExtractVerifier attendanceExtractVerifier;

    private static ExtractVerifier datelessExtractVerifier;

    private static final Map<String, ExtractVerifier> ENTITIES_TO_VERIFIERS = new HashMap<String, ExtractVerifier>();

    public static ExtractVerifier retrieveExtractVerifier(String entityType) {
        return ENTITIES_TO_VERIFIERS.get(entityType);
    }

    @Autowired
    @Qualifier("simpleExtractVerifier")
    public void setSimpleExtractVerifier(ExtractVerifier simpleExtractVerifier) {
        ExtractVerifierFactory.simpleExtractVerifier = simpleExtractVerifier;

        for (String entityType : EntityDates.ENTITY_DATE_FIELDS.keySet()) {
            if (!entityType.equals(EntityNames.ATTENDANCE)) {
                ENTITIES_TO_VERIFIERS.put(entityType, ExtractVerifierFactory.simpleExtractVerifier);
            }
        }
    }

    @Autowired
    @Qualifier("pathExtractVerifier")
    public void setPathExtractVerifier(ExtractVerifier pathExtractVerifier) {
        ExtractVerifierFactory.pathExtractVerifier = pathExtractVerifier;

        for (String entityType : EntityDates.ENTITY_PATH_FIELDS.keySet()) {
            ENTITIES_TO_VERIFIERS.put(entityType, ExtractVerifierFactory.pathExtractVerifier);
        }
    }

    @Autowired
    @Qualifier("teacherSchoolAssociationExtractVerifier")
    public void setTeacherSchoolAssociationExtractVerifier(ExtractVerifier teacherSchoolAssociationExtractVerifier) {
        ExtractVerifierFactory.teacherSchoolAssociationExtractVerifier = teacherSchoolAssociationExtractVerifier;

        ENTITIES_TO_VERIFIERS.put(EntityNames.TEACHER_SCHOOL_ASSOCIATION, ExtractVerifierFactory.teacherSchoolAssociationExtractVerifier);
    }

    @Autowired
    @Qualifier("attendanceExtractVerifier")
    public void setAttendanceExtractVerifier(ExtractVerifier attendanceExtractVerifier) {
        ExtractVerifierFactory.attendanceExtractVerifier = attendanceExtractVerifier;

        ENTITIES_TO_VERIFIERS.put(EntityNames.ATTENDANCE, ExtractVerifierFactory.attendanceExtractVerifier);
    }

    @Autowired
    @Qualifier("datelessExtractVerifier")
    public void setDatelessExtractVerifier(ExtractVerifier datelessExtractVerifier) {
        ExtractVerifierFactory.datelessExtractVerifier = datelessExtractVerifier;

        for (String entityType : EntityDates.NON_DATED_ENTITIES) {
            ENTITIES_TO_VERIFIERS.put(entityType, ExtractVerifierFactory.datelessExtractVerifier);
        }
    }

}
