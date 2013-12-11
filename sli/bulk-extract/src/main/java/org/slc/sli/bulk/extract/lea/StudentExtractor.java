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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;

import org.joda.time.DateTime;

import org.slc.sli.bulk.extract.date.EntityDateHelper;
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
    private static final List<String> DATED_SUBDOCS = Arrays.asList(EntityNames.STUDENT_PROGRAM_ASSOCIATION,
            EntityNames.STUDENT_COHORT_ASSOCIATION, EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION);
    private ExtractFileMap map;
    private EntityExtractor extractor;
    private Repository<Entity> repo;
    private EntityToEdOrgDateCache studentDatedCache;
    private EntityToEdOrgCache parentCache;
    private EdOrgExtractHelper edOrgExtractHelper;

    private ExtractorHelper helper;

    private EntityToEdOrgDateCache diDateCache = new EntityToEdOrgDateCache();

    public StudentExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo,
                            ExtractorHelper helper, EdOrgExtractHelper edOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.helper = helper;
        this.parentCache = new EntityToEdOrgCache();
        this.edOrgExtractHelper = edOrgExtractHelper;
        this.studentDatedCache = new EntityToEdOrgDateCache();
    }

    /* (non-Javadoc)
     * @see org.slc.sli.bulk.extract.lea.EntityExtract#extractEntities(java.util.Map)
     */
    @Override
    public void extractEntities(EntityToEdOrgCache dummyCache) {
        edOrgExtractHelper.logSecurityEvent(map.getEdOrgs(), EntityNames.STUDENT, this.getClass().getName());

        Iterator<Entity> cursor = repo.findEach(EntityNames.STUDENT, new NeutralQuery());

        while (cursor.hasNext()) {
            Entity student = cursor.next();
            buildStudentDatedCache(student);
            final Map<String, DateTime> datedEdOrgs = studentDatedCache.getEntriesById(student.getEntityId());

            for (final String edOrg : map.getEdOrgs()) {
                if (datedEdOrgs.containsKey(edOrg)) {
                    // Write
                    extractor.extractEntity(student, map.getExtractFileForEdOrg(edOrg), EntityNames.STUDENT, new Predicate<Entity>() {
                        @Override
                        public boolean apply(Entity input) {
                            boolean shouldExtract = true;
                            if (DATED_SUBDOCS.contains(input.getType())) {
                                DateTime upToDate = datedEdOrgs.get(edOrg);
                                shouldExtract = EntityDateHelper.shouldExtract(input, upToDate);
                            }

                            return shouldExtract;
                        }
                    });

                    Iterable<String> parents = helper.fetchCurrentParentsFromStudent(student);
                    for (String parent : parents) {
                        parentCache.addEntry(parent, edOrg);
                    }
                }
            }

            List<Entity> sdias =  student.getEmbeddedData().get("studentDisciplineIncidentAssociation");

            if (sdias != null) {
                for (Entity sdia : sdias) {
                    String did = (String) sdia.getBody().get("disciplineIncidentId");
                    Map<String, DateTime> edOrgsDate = studentDatedCache.getEntriesById(student.getEntityId());

                    for (Map.Entry<String, DateTime> entry : edOrgsDate.entrySet()) {
                        diDateCache.addEntry(did, entry.getKey(), entry.getValue());
                    }
                }
            }
        }

    }

    private void buildStudentDatedCache(Entity student) {
        Map<String, DateTime> edOrgDate = helper.fetchAllEdOrgsForStudent(student);
        for (String edOrg : map.getEdOrgs()) {
            if (edOrgDate.containsKey(edOrg)) {
                studentDatedCache.addEntry(student.getEntityId(), edOrg, edOrgDate.get(edOrg));
            }
        }
    }

    public EntityToEdOrgCache getParentCache() {
        return parentCache;
    }

    public EntityToEdOrgDateCache getStudentDatedCache() {
        return studentDatedCache;
    }

    public EntityToEdOrgDateCache getDiDateCache() {
        return diDateCache;
    }
}
