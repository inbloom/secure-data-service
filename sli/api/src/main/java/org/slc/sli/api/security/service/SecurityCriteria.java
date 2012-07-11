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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Encapsulates security criteria used by queries
 *
 * @author srupasinghe
 *
 */
public class SecurityCriteria {
    //main security criteria
    private NeutralCriteria securityCriteria;
    //black list criteria
    private NeutralCriteria blacklistCriteria;

    public NeutralCriteria getSecurityCriteria() {
        return securityCriteria;
    }

    public NeutralCriteria getBlacklistCriteria() {
        return blacklistCriteria;
    }

    public void setSecurityCriteria(NeutralCriteria securityCriteria) {
        this.securityCriteria = securityCriteria;
    }

    public void setBlacklistCriteria(NeutralCriteria blacklistCriteria) {
        this.blacklistCriteria = blacklistCriteria;
    }

    /**
     * Apply the security criteria to the given query
     * @param query The query to manipulate
     * @return
     */
    public NeutralQuery applySecurityCriteria(NeutralQuery query) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SLIPrincipal user = (SLIPrincipal) auth.getPrincipal();
        String userId = user.getEntity().getEntityId();
        NeutralQuery createdByQuery = new NeutralQuery(new NeutralCriteria("metaData.createdBy", NeutralCriteria.OPERATOR_EQUAL, userId, false));
        createdByQuery.addCriteria(new NeutralCriteria("metaData.isOrphaned", NeutralCriteria.OPERATOR_EQUAL, "true", false));
        
        if (blacklistCriteria != null) {
            query.addCriteria(blacklistCriteria);
        }

        query.addOrQuery(new NeutralQuery(securityCriteria));
        query.addOrQuery(createdByQuery);
        

        
        
        return query;
    }
}
