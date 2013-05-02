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

/**
 * 
 */
package org.slc.sli.bulk.extract.lea;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

/**
 *
 *
 */
public class StudentExtractor implements EntityExtract {
    private LEAExtractFileMap map;
    private EntityExtractor extractor;
    private Repository<Entity> repo;
    private EntityToLeaCache cache;
    
    private ExtractorHelper helper;

    public StudentExtractor(EntityExtractor extractor, LEAExtractFileMap map, Repository<Entity> repo,
            ExtractorHelper helper, EntityToLeaCache cache) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.helper = helper;
        this.cache = cache;
    }
    /* (non-Javadoc)
     * @see org.slc.sli.bulk.extract.lea.EntityExtract#extractEntities(java.util.Map)
     */
    @Override
    public void extractEntities(Map<String, Set<String>> leaToEdorgCache) {
        Iterator<Entity> cursor = repo.findEach("student", new Query());
        while (cursor.hasNext()) {
            Entity e = cursor.next();
            Set<String> schools = helper.fetchCurrentSchoolsFromStudent(e);
            for (String lea : map.getLeas()) {
                if (schools.contains(lea)) {
                    // Write
                    extractor.extractEntity(e, map.getExtractFileForLea(lea), "student");
                    
                    // Update cache
                    cache.addEntry(e.getEntityId(), lea);
                }
            }
        }
        
    }
    
    public void setEntityCache(EntityToLeaCache cache) {
        this.cache = cache;
    }
    
    public EntityToLeaCache getEntityCache() {
        return cache;
    }


}
