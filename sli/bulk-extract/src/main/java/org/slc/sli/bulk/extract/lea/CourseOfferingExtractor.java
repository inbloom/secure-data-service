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
import java.util.Set;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

public class CourseOfferingExtractor {
    private EntityExtractor extractor;
    private LEAExtractFileMap map;
    private Repository<Entity> repo;
    private EntityToLeaCache courseCache;
    
    public CourseOfferingExtractor(EntityExtractor extractor, LEAExtractFileMap map, Repository<Entity> repo) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        courseCache = new EntityToLeaCache();
    }

    
    public void extractEntities(EntityToLeaCache edorgCache, EntityToLeaCache courseOfferingCache) {
        Iterator<Entity> cursor = repo.findEach("courseOffering", new Query());
        while (cursor.hasNext()) {
            Entity e = cursor.next();
            String courseOfferingId = e.getEntityId();
            String courseOfferingsEdorgId = e.getBody().get(ParameterConstants.SCHOOL_ID).toString();
            String courseId = e.getBody().get(ParameterConstants.COURSE_ID).toString();
            Set<String> leas = edorgCache.getEntriesById(courseOfferingsEdorgId);
            Set<String> leas2 = courseOfferingCache.getEntriesById(courseOfferingId);
            leas.addAll(leas2);
            
            for (String lea : leas) {
                extractor.extractEntity(e, map.getExtractFileForLea(lea), "courseOffering");
                courseCache.addEntry(courseId, lea);
            }
        }
    }
    
    public EntityToLeaCache getCourseCache(){
        return courseCache;
    }
}
