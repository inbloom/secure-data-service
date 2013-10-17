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
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class StaffEdorgAssignmentExtractor implements EntityExtract {

    private EntityExtractor extractor;
    private ExtractFileMap map;
    private Repository<Entity> repo;
    private ExtractorHelper extractorHelper;
    private EntityToEdOrgCache cache;
    private EdOrgExtractHelper edOrgExtractHelper;
    private EntityToEdOrgDateCache staffDatedCache;

    public StaffEdorgAssignmentExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo,
                                         ExtractorHelper extractorHelper, EntityToEdOrgCache entityToEdOrgCache, EdOrgExtractHelper edOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.extractorHelper = extractorHelper;
        this.cache = entityToEdOrgCache;
        this.edOrgExtractHelper = edOrgExtractHelper;
        this.staffDatedCache = new EntityToEdOrgDateCache();
    }

    @Override
    public void extractEntities(EntityToEdOrgCache entityToEdorgCache) {
        edOrgExtractHelper.logSecurityEvent(map.getEdOrgs(), EntityNames.STAFF_ED_ORG_ASSOCIATION, this.getClass().getName());
        buildStaffCaches(entityToEdorgCache);

        Iterator<Entity> associations = repo.findEach(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());
        while (associations.hasNext()) {
            Entity e = associations.next();
            Set<String> edOrgs = cache.getEntriesById((String) e.getBody().get(ParameterConstants.STAFF_REFERENCE));

            for (String edOrg : edOrgs) {
                extractor.extractEntity(e, map.getExtractFileForEdOrg(edOrg), EntityNames.STAFF_ED_ORG_ASSOCIATION);
            }
        }

    }

    private void buildStaffCaches(EntityToEdOrgCache entityToEdorgCache) {
        Iterator<Entity> associations = repo.findEach(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());

        while (associations.hasNext()) {
            Entity association = associations.next();
            String staffId = (String) association.getBody().get(ParameterConstants.STAFF_REFERENCE);

            Map<String, DateTime> edOrgDates = staffDatedCache.getEntriesById(staffId);
            extractorHelper.buildEdorgToDateMap(association.getBody(), edOrgDates,
                ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, ParameterConstants.BEGIN_DATE, ParameterConstants.END_DATE);
            for (Map.Entry<String, DateTime> edOrgDate : edOrgDates.entrySet()) {
                staffDatedCache.addEntry(staffId, edOrgDate.getKey(), edOrgDate.getValue());
            }

            if (!extractorHelper.isStaffAssociationCurrent(association)) {
                continue;
            }

            //TODO: Old pipeline, remove after F316 is done
            String edorg = (String) association.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE);
            Set<String> edOrgs = entityToEdorgCache.ancestorEdorgs(edorg);
            for(String edOrg:edOrgs) {
                cache.addEntry((String) association.getBody().get(ParameterConstants.STAFF_REFERENCE), edOrg);
            }
        }

    }

    public EntityToEdOrgCache getEntityCache() {
        return this.cache;
    }

    public EntityToEdOrgDateCache getStaffDatedCache() {
        return staffDatedCache;
    }

}
