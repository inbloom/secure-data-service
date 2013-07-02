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

package org.slc.sli.api.security.service;

import java.util.List;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ResponseTooLargeException;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Encapsulates authentication and context for entities.
 * 
 * 
 * @author kmyers
 *
 */
public class SecurityCriteria {
    // The collection this query pertains to
    private String collectionName;
    
    // main security criteria
    private NeutralCriteria securityCriteria;
    
    private long inClauseSize = 100000;

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
     * @param query
     *            The query to manipulate
     * @return
     * @throws ResponseTooLargeException
     */
    public NeutralQuery applySecurityCriteria(NeutralQuery query) {

        if (securityCriteria != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            SLIPrincipal user = (SLIPrincipal) auth.getPrincipal();
            
            // Check the type of who we are and if we're a teacher, handle it differently.
            if (EntityNames.TEACHER.equals(user.getEntity().getType())) {
                // Check the in clause size and throw 413 if it's too large
                List<String> ids = (List) securityCriteria.getValue();
                if (ids.size() > inClauseSize) {
                    // Throw 413 because security in clause is too large
                    throw new ResponseTooLargeException();
                }
            }
            query.addOrQuery(new NeutralQuery(securityCriteria));
        }

        return query;
    }
    
    public void setInClauseSize(Long size) {
        this.inClauseSize = size;
    }

}
