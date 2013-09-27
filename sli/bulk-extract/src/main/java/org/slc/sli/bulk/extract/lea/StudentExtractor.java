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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 *
 *
 */
public class StudentExtractor implements EntityExtract {
    private ExtractFileMap map;
    private EntityExtractor extractor;
    private Repository<Entity> repo;
    private EntityToEdOrgCache studentCache;
    private EntityToEdOrgDateCache studentDatedCache;
    private EntityToEdOrgCache parentCache;
    private EdOrgExtractHelper edOrgExtractHelper;

    private ExtractorHelper helper;

    private EntityToEdOrgCache diCache = new EntityToEdOrgCache();

    public StudentExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo,
                            ExtractorHelper helper, EntityToEdOrgCache studentCache, EntityToEdOrgCache parentCache,
                            EdOrgExtractHelper edOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.helper = helper;
        this.studentCache = studentCache;
        this.parentCache = parentCache;
        this.edOrgExtractHelper = edOrgExtractHelper;
    }

    /* (non-Javadoc)
     * @see org.slc.sli.bulk.extract.lea.EntityExtract#extractEntities(java.util.Map)
     */
    @Override
    public void extractEntities(EntityToEdOrgCache entityToEdorgCache) {
        edOrgExtractHelper.logSecurityEvent(map.getEdOrgs(), EntityNames.STUDENT, this.getClass().getName());

        Iterator<Entity> cursor = repo.findEach("student", new NeutralQuery());

        while (cursor.hasNext()) {
            Entity e = cursor.next();
            Set<String> schools = helper.fetchCurrentSchoolsForStudent(e);
            Iterable<String> parents = helper.fetchCurrentParentsFromStudent(e);
            for (String edOrg : map.getEdOrgs()) {
                if (schools.contains(edOrg)) {
                    // Write
                    extractor.extractEntity(e, map.getExtractFileForEdOrg(edOrg), "student");
                    
                    // Update studentCache
                    studentCache.addEntry(e.getEntityId(), edOrg);

                    for (String parent : parents) {
                        parentCache.addEntry(parent, edOrg);
                    }
                }
            }

            List<Entity> sdias =  e.getEmbeddedData().get("studentDisciplineIncidentAssociation");

            if(sdias != null) {
                for(Entity sdia : sdias) {
                    String did = (String) sdia.getBody().get("disciplineIncidentId");
                    Set<String> edOrgs = studentCache.getEntriesById(e.getEntityId());

                    if(edOrgs != null) {
                        for(String edOrg : edOrgs) {
                            diCache.addEntry(did, edOrg);
                        }
                    } else {
                        diCache.addEntry(did, "marker");    // adding a marker that this DI is referenced by a student
                    }

                }
            }
        }
        
    }

    private void buildStudentDatedCache(Entity student) {
        Map<String, DateTime> edOrgDate = helper.fetchAllEdOrgsForStudent(student);
        for(String edOrg : map.getEdOrgs()) {
            if(edOrgDate.containsKey(edOrg)) {
                studentDatedCache.addEntry(student.getEntityId(), edOrg, edOrgDate.get(edOrg));
            }
        }
    }
    
    public void setEntityCache(EntityToEdOrgCache cache) {
        this.studentCache = cache;
    }
    
    public EntityToEdOrgCache getEntityCache() {
        return studentCache;
    }
    
    public EntityToEdOrgCache getParentCache() {
        return parentCache;
    }

    public EntityToEdOrgCache getDiCache() {
        return diCache;
    }
}
