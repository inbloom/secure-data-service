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

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

public class AttendanceExtractor implements EntityExtract {
    private EntityExtractor extractor;
    private LEAExtractFileMap map;
    private Repository<Entity> repo;
    private ExtractorHelper helper;
    private EntityToLeaCache studentCache;
    
    public AttendanceExtractor(EntityExtractor extractor, LEAExtractFileMap map, Repository<Entity> repo,
            ExtractorHelper extractorHelper, EntityToLeaCache studentCache) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.helper = extractorHelper;
        this.studentCache = studentCache;
    }

    @Override
    public void extractEntities(Map<String, Set<String>> leaToEdorgCache) {
        Iterator<Entity> attendances = repo.findEach("attendance", new Query());
        while (attendances.hasNext()) {
            Entity attendance = attendances.next();
            Set<String> leas = studentCache.getEntriesById((String) attendance.getBody().get("studentId"));
            for (String lea : leas) {
                extractor.extractEntity(attendance, map.getExtractFileForLea(lea), "attendance");
            }
        }
        
    }
    
}
