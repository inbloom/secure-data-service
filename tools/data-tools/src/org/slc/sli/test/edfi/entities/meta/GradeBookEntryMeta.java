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

import java.util.List;

public class GradeBookEntryMeta {

    String id;
    List<String> learningObjectiveIds;
    GradingPeriodMeta gradingPeriod;
    SectionMeta section;
    String gradebookEntryType;
    String dateAssigned;

    public void setLearningObjectiveIds(List<String> learningObjectiveIds) {
        this.learningObjectiveIds = learningObjectiveIds;
    }

    public List<String> getLearningObjectiveIds() {
        return learningObjectiveIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GradingPeriodMeta getGradingPeriod() {
        return gradingPeriod;
    }

    public void setGradingPeriod(GradingPeriodMeta gradingPeriod) {
        this.gradingPeriod = gradingPeriod;
    }

    public SectionMeta getSection() {
        return section;
    }

    public void setSection(SectionMeta section) {
        this.section = section;
    }

    public String getGradebookEntryType() {
        return gradebookEntryType;
    }

    public void setGradebookEntryType(String gradebookEntryType) {
        this.gradebookEntryType = gradebookEntryType;
    }

    public String getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(String dateAssigned) {
        this.dateAssigned = dateAssigned;
    }
     
}
