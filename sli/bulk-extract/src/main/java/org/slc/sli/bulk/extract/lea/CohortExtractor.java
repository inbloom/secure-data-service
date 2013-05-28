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

package org.slc.sli.bulk.extract.lea;

import java.util.Iterator;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.LocalEdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

public class CohortExtractor implements EntityExtract {
    private EntityExtractor extractor;
    private LEAExtractFileMap map;
    private Repository<Entity> repo;
    private LocalEdOrgExtractHelper localEdOrgExtractHelper;
    
    public CohortExtractor(EntityExtractor extractor, LEAExtractFileMap map, Repository<Entity> repo, LocalEdOrgExtractHelper localEdOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.localEdOrgExtractHelper = localEdOrgExtractHelper;
    }

    @Override
    public void extractEntities(EntityToLeaCache edorgCache) {
        localEdOrgExtractHelper.logSecurityEvent(map.getLeas(), EntityNames.COHORT, this.getClass().getName());
        Iterator<Entity> cursor = repo.findEach(EntityNames.COHORT, new Query());
        while (cursor.hasNext()) {
            Entity e = cursor.next();
            String edorgId = e.getBody().get("educationOrgId").toString();
            String leaForCohort = edorgCache.leaFromEdorg(edorgId);
            extractor.extractEntity(e, map.getExtractFileForLea(leaForCohort), EntityNames.COHORT);
        }
    }
    
}
