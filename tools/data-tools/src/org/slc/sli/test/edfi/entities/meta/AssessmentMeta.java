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

public final class AssessmentMeta {

    public final String id;
    public final List<String> objectiveAssessmentIds;
    public final List<String> assessmentItemIds;
    public final List<String> performanceLevelDescriptorIds;
    public String assessmentFamilyId;
    public String assessmentPeriodDescriptorId;
    public final List<String> sectionIds;

    private AssessmentMeta(String id) {
        this.id = id;
        this.objectiveAssessmentIds = new ArrayList<String>();
        this.assessmentItemIds = new ArrayList<String>();
        this.performanceLevelDescriptorIds = new ArrayList<String>();
        this.sectionIds = new ArrayList<String>();
    }

    public static AssessmentMeta create(String id) {
        return new AssessmentMeta(id);
    }

    @Override
    public String toString() {
        return "AssessmentMeta [id=" + id + ", objectiveAssessmentIds=" + objectiveAssessmentIds
                + ", assessmentItemIds=" + assessmentItemIds + ", performanceLevelDescriptorIds="
                + performanceLevelDescriptorIds + ", assessmentFamilyId=" + assessmentFamilyId
                + ", assessmentPeriodDescriptorId=" + assessmentPeriodDescriptorId + ", sectionIds=" + sectionIds + "]";
    }

}
