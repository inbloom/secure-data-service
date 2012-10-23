/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.api.security.service;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

public class SecurityCriteria {
    //The collection this query pertains to
    private String collectionName;

    //main security criteria
    private NeutralCriteria securityCriteria;
    

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
    
    public NeutralCriteria getSecurityCriteria() {
        return securityCriteria;
    }

    public void setSecurityCriteria(NeutralCriteria securityCriteria) {
        this.securityCriteria = securityCriteria;
    }
    /**
     * Apply the security criteria to the given query
     *
     * @param query The query to manipulate
     * @return
     */
    public NeutralQuery applySecurityCriteria(NeutralQuery query) {

        if (securityCriteria != null) {
            query.addOrQuery(new NeutralQuery(securityCriteria));
        }

        return query;
    }
    
}
