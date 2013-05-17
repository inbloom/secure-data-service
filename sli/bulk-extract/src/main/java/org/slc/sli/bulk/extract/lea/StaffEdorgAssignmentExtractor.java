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
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

public class StaffEdorgAssignmentExtractor implements EntityExtract {
    
    private EntityExtractor extractor;
    private LEAExtractFileMap map;
    private Repository<Entity> repo;
    private ExtractorHelper extractorHelper;
    private EntityToLeaCache cache;
    private LocalEdOrgExtractHelper localEdOrgExtractHelper;
    
    public StaffEdorgAssignmentExtractor(EntityExtractor extractor, LEAExtractFileMap map, Repository<Entity> repo,
            ExtractorHelper extractorHelper, EntityToLeaCache entityToLeaCache, LocalEdOrgExtractHelper localEdOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.extractorHelper = extractorHelper;
        this.cache = entityToLeaCache;
        this.localEdOrgExtractHelper = localEdOrgExtractHelper;
    }

    @Override
    public void extractEntities(EntityToLeaCache entityToEdorgCache) {
        localEdOrgExtractHelper.logSecurityEvent(map.getLeas(), EntityNames.STAFF_ED_ORG_ASSOCIATION, this.getClass().getName());
        Iterator<Entity> associations = repo.findEach(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());
        while (associations.hasNext()) {
            Entity association = associations.next();
            if (!extractorHelper.isStaffAssociationCurrent(association)) {
                continue;
            }
            for (String lea : entityToEdorgCache.getEntityIds()) {
                String edorg = (String) association.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE);
                if (entityToEdorgCache.getEntriesById(lea).contains(edorg)) {
                    // Write it
                    extractor.extractEntity(association, map.getExtractFileForLea(lea),
                            EntityNames.STAFF_ED_ORG_ASSOCIATION);
                    
                    // Cache it
                    cache.addEntry((String) association.getBody().get(ParameterConstants.STAFF_REFERENCE), lea);
                }
            }


        }
        
    }
    
    public EntityToLeaCache getEntityCache() {
        return this.cache;
    }

}
