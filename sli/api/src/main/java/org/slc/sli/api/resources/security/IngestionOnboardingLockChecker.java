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

package org.slc.sli.api.resources.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * TODO git rm me
 * Temporary check to make sure ingestion has not been locked for this tenant
 * Remove this once ingestion is fixed to be able to run concurrent jobs
 */
@Component
public class IngestionOnboardingLockChecker {
    private final Repository<Entity> repo;

    @Autowired
    public IngestionOnboardingLockChecker(@Qualifier("validationRepo") Repository<Entity> repo) {
        super();
        this.repo = repo;
    }

    public boolean ingestionLocked(String tenantId) {
        NeutralQuery query = new NeutralQuery(new NeutralCriteria("tenantId", "=", tenantId));
        query.addCriteria(new NeutralCriteria("tenantIsReady", "=", false));
        return repo.findOne("tenant", query) != null;
    }

}
