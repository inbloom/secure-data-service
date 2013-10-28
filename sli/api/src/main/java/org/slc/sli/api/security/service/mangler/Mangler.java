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

import java.util.List;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Mangles queries based on paging, etc.
 * 
 * @author kmyers
 *
 */
public abstract class Mangler {
    private NeutralCriteria securityCriteria;
    private NeutralQuery theQuery;
    public abstract NeutralQuery mangleQuery(NeutralQuery query, NeutralCriteria securityCriteria);
    
    public NeutralQuery mangleQuery() {
        return mangleQuery(theQuery, securityCriteria);
    }

    public void setSecurityCriteria(NeutralCriteria securityCriteria) {
        this.securityCriteria = securityCriteria;
    }
    
    public void setTheQuery(NeutralQuery theQuery) {
        this.theQuery = theQuery;
    }
    
    protected List<String> adjustIdListForPaging(List<String> securedIds) {
        //There're fewer IDs than we have limited ourselves by, so don't worry.
        if (securedIds.size() <= theQuery.getLimit()) {
            debug("We aren't paging the security criteria because there is less security than limit");
            return securedIds;
        } else if (theQuery.getLimit() == 0) {
          //They want it all, so we give it to them.
            debug("We aren't paging the security criteria because of a limit of 0");
            return securedIds;
        } else {
            return securedIds.subList(theQuery.getOffset(), theQuery.getOffset() + theQuery.getLimit());
        }
    }
    
    @SuppressWarnings("unchecked")
    protected void adjustSecurityForPaging() {
        List<String> fullIds = (List<String>) securityCriteria.getValue();
        fullIds = adjustIdListForPaging(fullIds);
        securityCriteria.setValue(fullIds);
    }
    
}
