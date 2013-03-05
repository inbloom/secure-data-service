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
        final ContainerDocument attendance = ContainerDocument.builder().forCollection("attendance")
                .forField("attendanceEvent")
                .withParent(parentKeys).build();

        final List<String> parentKeysForGRCSAR = Arrays.asList("studentId", "schoolYear");
        final ContainerDocument reportCard = ContainerDocument.builder().forCollection("reportCard")
                .forField("reportCard")
                .withParent(parentKeysForGRCSAR)
                .persistAs("yearlyTranscript")
                .asContainerSubdoc(true).build();
        final ContainerDocument grade = ContainerDocument.builder().forCollection("grade")
                .forField("grade")
                .withParent(parentKeysForGRCSAR)
                .persistAs("yearlyTranscript")
                .asContainerSubdoc(true).build();
        final ContainerDocument studentAcademicRecord = ContainerDocument.builder().forCollection("studentAcademicRecord")
                .forField("studentAcademicRecord")
                .withParent(parentKeysForGRCSAR)
                .persistAs("yearlyTranscript")
                .asContainerSubdoc(true).build();

        containerDocumentMap.put("attendance", attendance);
        containerDocumentMap.put("reportCard", reportCard);
        containerDocumentMap.put("grade", grade);
        containerDocumentMap.put("studentAcademicRecord", studentAcademicRecord);
    }
}
