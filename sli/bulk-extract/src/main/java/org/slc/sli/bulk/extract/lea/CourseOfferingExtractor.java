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
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

public class CourseOfferingExtractor {
    private EntityExtractor extractor;
    private ExtractFileMap map;
    private Repository<Entity> repo;
    private EntityToEdOrgCache courseCache;
    private EdOrgExtractHelper edOrgExtractHelper;
    
    public CourseOfferingExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        courseCache = new EntityToEdOrgCache();
        this.edOrgExtractHelper = edOrgExtractHelper;
    }

    
    public void extractEntities(EntityToEdOrgCache edorgCache, EntityToEdOrgCache courseOfferingCache) {
        edOrgExtractHelper.logSecurityEvent(map.getEdOrgs(), EntityNames.COURSE_OFFERING, this.getClass().getName());
        Iterator<Entity> cursor = repo.findEach(EntityNames.COURSE_OFFERING, new Query());
        while (cursor.hasNext()) {
            Entity e = cursor.next();
            String courseOfferingId = e.getEntityId();
            String schoolId = e.getBody().get(ParameterConstants.SCHOOL_ID).toString();
            String courseId = e.getBody().get(ParameterConstants.COURSE_ID).toString();
            Set<String> edOrgsForCourseOffering = edorgCache.ancestorEdorgs(schoolId);
            Set<String> edOrgs = courseOfferingCache.getEntriesById(courseOfferingId);

            for(String edOrgForCourseOffering:edOrgsForCourseOffering) {
                if(!edOrgs.contains(edOrgForCourseOffering)){
                    extractor.extractEntity(e, map.getExtractFileForEdOrg(edOrgForCourseOffering), EntityNames.COURSE_OFFERING);
                    courseCache.addEntry(courseId, edOrgForCourseOffering);
                }
            }
            for (String edOrg : edOrgs) {
                extractor.extractEntity(e, map.getExtractFileForEdOrg(edOrg), EntityNames.COURSE_OFFERING);
                courseCache.addEntry(courseId, edOrg);
            }
        }
    }
    
    public EntityToEdOrgCache getCourseCache(){
        return courseCache;
    }
}
