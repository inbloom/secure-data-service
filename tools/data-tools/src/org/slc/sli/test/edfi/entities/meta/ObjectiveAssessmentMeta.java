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

import java.util.ArrayList;
import java.util.List;

public final class ObjectiveAssessmentMeta {

    public final String id;
    public final List<String> learningObjectiveIds;
    public final List<String> assessmentItemIds;
    public final List<String> learningStandardIds;
    public final List<String> performanceLevelDescriptorIds;
    public final List<String> relatedObjectiveAssessmentIds;

    private ObjectiveAssessmentMeta(String id) {
        this.id = id;
        this.learningObjectiveIds = new ArrayList<String>();
        this.assessmentItemIds = new ArrayList<String>();
        this.learningStandardIds = new ArrayList<String>();
        this.performanceLevelDescriptorIds = new ArrayList<String>();
        this.relatedObjectiveAssessmentIds = new ArrayList<String>();
    }

    public static ObjectiveAssessmentMeta create(String id) {
        return new ObjectiveAssessmentMeta(id);
    }

    @Override
    public String toString() {
        return "ObjectiveAssessmentMeta [id=" + id + ", learningObjectiveIds=" + learningObjectiveIds
                + ", assessmentItemIds=" + assessmentItemIds + ", learningStandardIds=" + learningStandardIds
                + ", performanceLevelDescriptorIds=" + performanceLevelDescriptorIds
                + ", relatedObjectiveAssessmentIds=" + relatedObjectiveAssessmentIds + "]";
    }

}
