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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.extractor.LocalEdOrgExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * @author dkornishev
 */
public class SectionExtractor implements EntityDatedExtract {

    private static final Logger LOG = LoggerFactory.getLogger(LocalEdOrgExtractor.class);

    private final EntityExtractor entityExtractor;
    private final ExtractFileMap leaToExtractFileMap;
    private final Repository<Entity> repository;
    private final EntityToEdOrgCache edorgCache;
    private final EntityToEdOrgCache courseOfferingCache = new EntityToEdOrgCache();
    private final EntityToEdOrgDateCache studentSectionAssociationDateCache = new EntityToEdOrgDateCache();
    private final EdOrgExtractHelper edOrgExtractHelper;


    public SectionExtractor(EntityExtractor entityExtractor, ExtractFileMap leaToExtractFileMap, Repository<Entity> repository,
            EntityToEdOrgCache edorgCache, EdOrgExtractHelper edOrgExtractHelper) {
        this.entityExtractor = entityExtractor;
        this.leaToExtractFileMap = leaToExtractFileMap;
        this.repository = repository;
        this.edorgCache = edorgCache;
        this.edOrgExtractHelper = edOrgExtractHelper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void extractEntities(final EntityToEdOrgDateCache studentDateCache) {
        Iterator<Entity> sections = this.repository.findEach("section", new NeutralQuery());

        while (sections.hasNext()) {
            final Entity section = sections.next();
            String schoolId = (String) section.getBody().get("schoolId");
            final Set<String> allEdOrgs = this.edorgCache.ancestorEdorgs(schoolId);

            if (null != allEdOrgs && allEdOrgs.size() != 0) {  // Edorgs way
                for(final String edOrg: allEdOrgs) {
                    extract(section, edOrg, new Predicate<Entity>() {
                        @Override
                        public boolean apply(Entity input) {
                            boolean shouldExtract = true;
                            String studentId = (String) input.getBody().get("studentId");
                            if (studentId != null) {    // Validate that referenced student is visible to given lea
                                shouldExtract = studentDateCache.getEntriesById(studentId).keySet().contains(edOrg);
                            }

                            return shouldExtract;
                        }
                    }, studentDateCache);
                }

            } else {    // Student way
                List<Entity> assocs = section.getEmbeddedData().get("studentSectionAssociation");

                if (null != assocs) {
                    for (Entity assoc : assocs) {
                        Map<String, Object> body =  assoc.getBody();
                        final String studentId = (String) body.get("studentId");
                        Set<String> edOrgs = studentDateCache.getEntriesById(studentId).keySet();
                        for (final String edOrg : edOrgs) {
                            Predicate<Entity> filter = new Predicate<Entity>() {
                                @Override
                                public boolean apply(Entity input) {
                                    Map<String, Object> body = input.getBody();
                                    return body.keySet().contains("studentId") && studentId.equals(body.get("studentId"));
                                }
                            };

                            extract(section, edOrg, filter, studentDateCache);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void extract(Entity section, final String edOrg, Predicate<Entity> filter, EntityToEdOrgDateCache studentDateCache) {
        //Extract only the section's sub doc entities
        this.entityExtractor.extractEmbeddedEntities(section, this.leaToExtractFileMap.getExtractFileForEdOrg(edOrg), "section", filter);
        this.courseOfferingCache.addEntry((String) section.getBody().get("courseOfferingId"), edOrg);

        List<Entity> ssas = section.getEmbeddedData().get("studentSectionAssociation");

        LOG.info("SSAs is {}", ssas);
        LOG.info("Embedded data is {}", section.getEmbeddedData());
        if (null != ssas) {
        	LOG.info("SSAs size is {}", ssas.size());
            for (Entity ssa : ssas) {
                if (filter.apply(ssa)) {
                	LOG.info("ssa Body is {} and entityId is {}", ssa.getBody(), ssa.getEntityId());
                    String studentId = (String) ssa.getBody().get("studentId");
                    DateTime upToDate = studentDateCache.getEntriesById(studentId).get(edOrg);
                    this.studentSectionAssociationDateCache.addEntry(ssa.getEntityId(), edOrg, upToDate);
                    LOG.info("Now the SSA cache size is {}", this.studentSectionAssociationDateCache.getEntityIds().size());
                }
            }
        }
    }

    public EntityToEdOrgCache getCourseOfferingCache() {
        return courseOfferingCache;
    }

    public EntityToEdOrgDateCache getStudentSectionAssociationDateCache() {
        return studentSectionAssociationDateCache;
    }

}
