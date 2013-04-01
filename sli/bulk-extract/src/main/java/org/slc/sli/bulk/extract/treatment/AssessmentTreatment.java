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
package org.slc.sli.bulk.extract.treatment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.domain.Entity;

/** Embed subdocs into Assessment and StudentAssessment entities.
 * 
 * @author tshewchuk
 *
 */
public class AssessmentTreatment implements Treatment{

    @Override
    public Entity apply(Entity entity) {
        // Assessment and StudentAssessment entities are special cases.
        if (entity.getType().equals("assessment") || entity.getType().equals("studentAssessment")) {
            Map<String, List<Entity>> subdocs = entity.getEmbeddedData();
            for (Map.Entry<String, List<Entity>> subdoc : subdocs.entrySet()) {
                List<Map<String, Object>> subdocList = new ArrayList<Map<String, Object>>();
                for (Entity subdocItem : subdoc.getValue()) {
                    if (subdocItem.getType().equals("objectiveAssessment")) {
                        subdocItem.getBody().remove("assessmentItemReference");
                        subdocItem.getBody().remove("assessmentId");
                    } else if (subdocItem.getType().equals("assessmentItem")) {
                        subdocItem.getBody().remove("assessmentId");
                    }
                    subdocList.add(subdocItem.getBody());
                }
                entity.getBody().put(subdoc.getKey(), subdocList);
            }
        }

        return entity;
    }

}
