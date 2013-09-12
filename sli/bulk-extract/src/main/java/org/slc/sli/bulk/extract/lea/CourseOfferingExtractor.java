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
import org.slc.sli.bulk.extract.util.LocalEdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

public class CourseOfferingExtractor {
    private EntityExtractor extractor;
    private LEAExtractFileMap map;
    private Repository<Entity> repo;
    private EntityToLeaCache courseCache;
    private LocalEdOrgExtractHelper localEdOrgExtractHelper;
    
    public CourseOfferingExtractor(EntityExtractor extractor, LEAExtractFileMap map, Repository<Entity> repo, LocalEdOrgExtractHelper localEdOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        courseCache = new EntityToLeaCache();
        this.localEdOrgExtractHelper = localEdOrgExtractHelper;
    }

    
    public void extractEntities(EntityToLeaCache edorgCache, EntityToLeaCache courseOfferingCache) {
        localEdOrgExtractHelper.logSecurityEvent(map.getLeas(), EntityNames.COURSE_OFFERING, this.getClass().getName());
        Iterator<Entity> cursor = repo.findEach(EntityNames.COURSE_OFFERING, new Query());
        while (cursor.hasNext()) {
            Entity e = cursor.next();
            String courseOfferingId = e.getEntityId();
            String schoolId = e.getBody().get(ParameterConstants.SCHOOL_ID).toString();
            String courseId = e.getBody().get(ParameterConstants.COURSE_ID).toString();
            Set<String> leasForCourseOffering = edorgCache.leaFromEdorg(schoolId);
            Set<String> leas = courseOfferingCache.getEntriesById(courseOfferingId);

            for(String leaForCourseOffering:leasForCourseOffering) {
                if(!leas.contains(leaForCourseOffering)){
                    extractor.extractEntity(e, map.getExtractFileForLea(leaForCourseOffering), EntityNames.COURSE_OFFERING);
                    courseCache.addEntry(courseId, leaForCourseOffering);
                }
            }
            for (String lea : leas) {
                extractor.extractEntity(e, map.getExtractFileForLea(lea), EntityNames.COURSE_OFFERING);
                courseCache.addEntry(courseId, lea);
            }
        }
    }
    
    public EntityToLeaCache getCourseCache(){
        return courseCache;
    }
}
