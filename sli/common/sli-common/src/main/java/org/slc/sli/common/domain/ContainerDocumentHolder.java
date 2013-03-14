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

package org.slc.sli.common.domain;

import org.slc.sli.api.constants.EntityNames;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jstokes
 * @author pghosh
 */
@Component
public class ContainerDocumentHolder {
    private final Map<String, ContainerDocument> containerDocumentMap = new HashMap<String, ContainerDocument>();

    public ContainerDocumentHolder() {
        init();
    }

    public ContainerDocumentHolder(final Map<String, ContainerDocument> containerDocumentMap) {
        this.containerDocumentMap.putAll(containerDocumentMap);
    }

    public ContainerDocument getContainerDocument(final String entityName) {
        return containerDocumentMap.get(entityName);
    }

    public boolean isContainerDocument(final String entityName) {
        return containerDocumentMap.containsKey(entityName);
    }

    private void init() {
        final List<String> parentKeys = Arrays.asList("studentId", "schoolId", "schoolYear");
        final ContainerDocument attendance = ContainerDocument.builder()
                .forCollection(EntityNames.ATTENDANCE)
                .forField("attendanceEvent")
                .persistAs(EntityNames.ATTENDANCE)
                .asContainerSubdoc(false)
                .withParent(parentKeys).build();

        final List<String> parentKeysForGRCSAR = Arrays.asList("studentId", "schoolYear");
        final ContainerDocument reportCard = ContainerDocument.builder()
                .forCollection(EntityNames.REPORT_CARD)
                .forField(EntityNames.REPORT_CARD)
                .withParent(parentKeysForGRCSAR)
                .persistAs("yearlyTranscript")
                .asContainerSubdoc(true)
                .build();
        final ContainerDocument grade = ContainerDocument.builder()
                .forCollection(EntityNames.GRADE)
                .forField(EntityNames.GRADE)
                .withParent(parentKeysForGRCSAR)
                .persistAs("yearlyTranscript")
                .asContainerSubdoc(true).build();
        final ContainerDocument studentAcademicRecord = ContainerDocument.builder()
                .forCollection(EntityNames.STUDENT_ACADEMIC_RECORD)
                .forField(EntityNames.STUDENT_ACADEMIC_RECORD)
                .withParent(parentKeysForGRCSAR)
                .persistAs("yearlyTranscript")
                .asContainerSubdoc(true)
                .build();

        containerDocumentMap.put(EntityNames.ATTENDANCE, attendance);
        containerDocumentMap.put(EntityNames.REPORT_CARD, reportCard);
        containerDocumentMap.put(EntityNames.GRADE, grade);
        containerDocumentMap.put(EntityNames.STUDENT_ACADEMIC_RECORD, studentAcademicRecord);
    }
}
