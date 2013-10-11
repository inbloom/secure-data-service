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
import org.slc.sli.bulk.extract.date.EntityDateHelper;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class StudentGradebookEntryExtractor implements EntityDatedExtract {
    private EntityExtractor extractor;
    private ExtractFileMap map;
    private Repository<Entity> repo;
    private EdOrgExtractHelper edOrgExtractHelper;
    private EntityToEdOrgDateCache gradebookEntryCache;
    
    public StudentGradebookEntryExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo,
            EdOrgExtractHelper edOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.edOrgExtractHelper = edOrgExtractHelper;
        gradebookEntryCache = new EntityToEdOrgDateCache();
    }
    
    @Override
    public void extractEntities(EntityToEdOrgDateCache studentCache) {
        edOrgExtractHelper.logSecurityEvent(map.getEdOrgs(), EntityNames.STUDENT_GRADEBOOK_ENTRY, this.getClass().getName());
        Iterator<Entity> studentGradebookEntries = repo.findEach(EntityNames.STUDENT_GRADEBOOK_ENTRY, new NeutralQuery());
        
        while (studentGradebookEntries.hasNext()) {
            Entity studentGradebookEntry = studentGradebookEntries.next();
            String studentId = (String) studentGradebookEntry.getBody().get(ParameterConstants.STUDENT_ID);
            String gradebookEntryId = (String) studentGradebookEntry.getBody().get(ParameterConstants.GRADEBOOK_ENTRY_ID);

            Map<String, DateTime> studentEdOrgs = studentCache.getEntriesById(studentId);
            for (Map.Entry<String, DateTime> entry: studentEdOrgs.entrySet()) {
                DateTime upToDate = entry.getValue();
                if(shouldExtract(studentGradebookEntry, upToDate)) {
                    extractor.extractEntity(studentGradebookEntry, map.getExtractFileForEdOrg(entry.getKey()), EntityNames.STUDENT_GRADEBOOK_ENTRY);
                    gradebookEntryCache.addEntry(gradebookEntryId, entry.getKey(), upToDate);
                }
            }
        }
        
    }

    protected boolean shouldExtract(Entity input, DateTime upToDate) {
        return EntityDateHelper.shouldExtract(input, upToDate);
    }

    public EntityToEdOrgDateCache getGradebookEntryCache() {
        return gradebookEntryCache;
    }
    
}
