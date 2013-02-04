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

public final class LearningObjectiveMeta {

    public final String id;
    public final List<String> learningStandardIds;
    public final List<String> learningObjectiveMetaIds;

    private LearningObjectiveMeta(String id) {
        this.id = id;
        this.learningStandardIds = new ArrayList<String>();
        this.learningObjectiveMetaIds = new ArrayList<String>();
    }

    public static LearningObjectiveMeta create(String id) {
        return new LearningObjectiveMeta(id);
    }

    @Override
    public String toString() {
        return "LearningObjectiveMeta [id=" + id + ", learningStandardIds=" + learningStandardIds
                + ", learningObjectiveMetaIds=" + learningObjectiveMetaIds + "]";
    }

}
