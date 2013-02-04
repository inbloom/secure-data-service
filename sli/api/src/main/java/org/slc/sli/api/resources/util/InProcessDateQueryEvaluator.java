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

package org.slc.sli.api.resources.util;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

/**
 * Provides a way to evaluate a limited set of NeutralCriteria. Used in evaluating
 * whether an entity matches a granular access date filter, if it can't be done in the database
 * 
 * @author: sashton
 */
@Component
public class InProcessDateQueryEvaluator {
    
    /**
     * Determines whether a query matches an entity. This does NOT handle all operators,
     * and it assumes that all the fields are strings. This is a minimal filter function
     * that is used to apply the granular access query in Java in the case of embedded
     * docs that are loaded in a batch. This is not intended to be a general purpose query
     * handler.
     * 
     * @param entity
     * @param query
     * @return
     */
    public boolean entitySatisfiesDateQuery(EntityBody entity, NeutralQuery query) {
        
        // look at AND criteria
        for (NeutralCriteria andCriteria : query.getCriteria()) {
            String fieldName = andCriteria.getKey();
            String operator = andCriteria.getOperator();
            
            if (NeutralCriteria.CRITERIA_EXISTS.equals(operator)) {
                boolean shouldExist = (Boolean) andCriteria.getValue();
                Object actualValue = entity.get(fieldName);
                if (shouldExist && actualValue == null) {
                    return false;
                } else if (!shouldExist && actualValue != null) {
                    return false;
                }
            } else {
                String fieldValue = (String) entity.get(fieldName);
                if (fieldValue == null) {
                    return false;
                }
                String expectedValue = (String) andCriteria.getValue();
                int comparison = fieldValue.compareTo(expectedValue);
                if (NeutralCriteria.CRITERIA_LT.equals(operator)) {
                    if (comparison >= 0) {
                        return false;
                    }
                } else if (NeutralCriteria.CRITERIA_LTE.equals(operator)) {
                    if (comparison > 0) {
                        return false;
                    }
                } else if (NeutralCriteria.CRITERIA_GT.equals(operator)) {
                    if (comparison <= 0) {
                        return false;
                    }
                } else if (NeutralCriteria.CRITERIA_GTE.equals(operator)) {
                    if (comparison < 0) {
                        return false;
                    }
                }
            }
        }
        
        // look at OR criteria
        if (query.getOrQueries().size() > 0) {
            for (NeutralQuery orQuery : query.getOrQueries()) {
                if (entitySatisfiesDateQuery(entity, orQuery)) {
                    return true;
                }
            }
            // if one had matched, would have returned true already (short-circuit)
            return false;
        } else {
            return true;
        }
    }
}
