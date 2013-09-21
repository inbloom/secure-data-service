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

import com.google.common.base.Function;
import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

import java.util.Iterator;
import java.util.Set;

public class StaffEdorgAssignmentExtractor implements EntityExtract {

    private EntityExtractor extractor;
    private ExtractFileMap map;
    private Repository<Entity> repo;
    private ExtractorHelper extractorHelper;
    private EntityToEdOrgCache cache;
    private EdOrgExtractHelper edOrgExtractHelper;

    public StaffEdorgAssignmentExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo,
                                         ExtractorHelper extractorHelper, EntityToEdOrgCache entityToEdOrgCache, EdOrgExtractHelper edOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.extractorHelper = extractorHelper;
        this.cache = entityToEdOrgCache;
        this.edOrgExtractHelper = edOrgExtractHelper;
    }

    @Override
    public void extractEntities(EntityToEdOrgCache entityToEdorgCache) {
        edOrgExtractHelper.logSecurityEvent(map.getEdOrgs(), EntityNames.STAFF_ED_ORG_ASSOCIATION, this.getClass().getName());
        doIterate(entityToEdorgCache, new Function<Pair<String, Entity>, Boolean>() {
            @Override
            public Boolean apply(Pair<String, Entity> input) {
                cache.addEntry((String) input.getRight().getBody().get(ParameterConstants.STAFF_REFERENCE), input.getLeft());
                return true;
            }
        });

        Iterator<Entity> associations = repo.findEach(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());
        while (associations.hasNext()) {
            Entity e = associations.next();
            Set<String> edOrgs = cache.getEntriesById((String) e.getBody().get(ParameterConstants.STAFF_REFERENCE));

            for (String edOrg : edOrgs) {
                extractor.extractEntity(e, map.getExtractFileForEdOrg(edOrg), EntityNames.STAFF_ED_ORG_ASSOCIATION);
            }
        }

    }

    private void doIterate(EntityToEdOrgCache entityToEdorgCache, Function<Pair<String, Entity>, Boolean> func) {
        Iterator<Entity> associations = repo.findEach(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());
        while (associations.hasNext()) {
            Entity association = associations.next();
            if (!extractorHelper.isStaffAssociationCurrent(association)) {
                continue;
            }
            String edorg = (String) association.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE);
            Set<String> edOrgs = entityToEdorgCache.ancestorEdorgs(edorg);
            for(String edOrg:edOrgs) {
                func.apply(Pair.of(edOrg, association));
            }
        }

    }

    public EntityToEdOrgCache getEntityCache() {
        return this.cache;
    }

}
