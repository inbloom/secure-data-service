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
package org.slc.sli.api.security.service.mangler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Mangles queries based on paging, etc.
 *
 */
public class DefaultQueryMangler extends Mangler {
    
    public NeutralQuery mangleQuery(NeutralQuery query, NeutralCriteria securityCriteria) {
        setTheQuery(query);
        setSecurityCriteria(securityCriteria);
        // Is this a  list query or a specific one?
        boolean isList = true;
        boolean isQueried = false;
        NeutralCriteria idCriteria = null;
        for (NeutralCriteria criteria : query.getCriteria()) {
            if (criteria.getKey().equals("_id")) {
                idCriteria = criteria;
                isList = false;
            }
        }
        if (isList) {
            if (!isQueried) {
                adjustSecurityForPaging();
            }
            query.addOrQuery(new NeutralQuery(securityCriteria));
            return query;
        } else {
            Set<String> finalIdSet = new HashSet<String>((Collection) securityCriteria.getValue());
            finalIdSet.retainAll((Collection) idCriteria.getValue());
            finalIdSet = new HashSet<String>(adjustIdListForPaging(new ArrayList<String>(finalIdSet)));
            // They're asking for something they CAN see.
            query.removeCriteria(idCriteria);
            if (finalIdSet.size() > 0) {
                idCriteria.setValue(new ArrayList<String>(finalIdSet));
                query.addOrQuery(new NeutralQuery(idCriteria));
            }
            return query;
        }
    }
    
}
