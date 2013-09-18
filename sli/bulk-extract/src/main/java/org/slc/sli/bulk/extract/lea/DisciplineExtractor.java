/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * User: dkornishev
 */
public class DisciplineExtractor implements EntityExtract {
    private final EntityExtractor entityExtractor;
    private final ExtractFileMap extractFileMap;
    private final Repository<Entity> repository;
    private final EntityToEdOrgCache studentCache;
    private final EntityToEdOrgCache edorgCache;

    public DisciplineExtractor(EntityExtractor entityExtractor, ExtractFileMap extractFileMap, Repository<Entity> repository, EntityToEdOrgCache studentCache, EntityToEdOrgCache edorgCache) {
        this.entityExtractor = entityExtractor;
        this.extractFileMap = extractFileMap;
        this.repository = repository;
        this.studentCache = studentCache;
        this.edorgCache = edorgCache;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void extractEntities(final EntityToEdOrgCache diCache) {
        extract(EntityNames.DISCIPLINE_INCIDENT, new Function<Entity, Set<String>>() {
            @Override
            public Set<String> apply(Entity input) {
                String id = input.getEntityId();
                Set<String> edOrgs = diCache.getEntriesById(id);

                if (edOrgs.isEmpty()) {
                   String schoolId = (String) input.getBody().get("schoolId");
                   edOrgs.addAll(edorgCache.ancestorEdorgs(schoolId));
                }

                return edOrgs;
            }
        });

        extract(EntityNames.DISCIPLINE_ACTION, new Function<Entity, Set<String>>() {
            @Override
            public Set<String> apply(Entity input) {
                Set<String> edOrgs = new HashSet<String>();

                List<String> students = (List<String>) input.getBody().get("studentId");

                for (String student : students) {
                    edOrgs.addAll(studentCache.getEntriesById(student));
                }

                return edOrgs;
            }
        });

    }

    private void extract(String entityType, Function<Entity, Set<String>> moreLeas) {
        Iterator<Entity> it = this.repository.findEach(entityType, new NeutralQuery());

        while (it.hasNext()) {
            Entity e = it.next();

            Set<String> edOrgs = new HashSet<String>();
            edOrgs.addAll(moreLeas.apply(e));

            edOrgs.remove("marker");
            for (String edOrg : edOrgs) {
                this.entityExtractor.extractEntity(e, this.extractFileMap.getExtractFileForEdOrg(edOrg), entityType);
            }
        }
    }
}
