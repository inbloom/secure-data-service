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

import com.google.common.base.Predicate;
import org.slc.sli.bulk.extract.BulkExtractEntity;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.LocalEdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dkornishev
 */
public class SectionExtractor implements EntityExtract {
    private final EntityExtractor entityExtractor;
    private final LEAExtractFileMap leaToExtractFileMap;
    private final Repository<Entity> repository;
    private final EntityToLeaCache studentCache;
    private final EntityToLeaCache edorgCache;
    private final EntityToLeaCache courseOfferingCache = new EntityToLeaCache();
    private final EntityToLeaCache ssaCache = new EntityToLeaCache();
    private final LocalEdOrgExtractHelper localEdOrgExtractHelper;


    public SectionExtractor(EntityExtractor entityExtractor, LEAExtractFileMap leaToExtractFileMap, Repository<Entity> repository, EntityToLeaCache studentCache, EntityToLeaCache edorgCache, LocalEdOrgExtractHelper localEdOrgExtractHelper) {

        this.entityExtractor = entityExtractor;
        this.leaToExtractFileMap = leaToExtractFileMap;
        this.repository = repository;
        this.studentCache = studentCache;
        this.edorgCache = edorgCache;
        this.localEdOrgExtractHelper = localEdOrgExtractHelper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void extractEntities(EntityToLeaCache entityToEdorgCache) {
        localEdOrgExtractHelper.logSecurityEvent(leaToExtractFileMap.getLeas(), EntityNames.SECTION, this.getClass().getName());
        Iterator<Entity> sections = this.repository.findEach("section", new NeutralQuery());

        while (sections.hasNext()) {
            Entity section = sections.next();
            String lea = this.edorgCache.leaFromEdorg((String) section.getBody().get("schoolId"));

            if (null != lea) {  // Edorgs way
                extract(section, lea, new Predicate<Entity>() {
                    @Override
                    public boolean apply(Entity input) {
                        return true;
                    }
                });

            } else {    // Student way
                List<Map<String, Object>> assocs = section.getDenormalizedData().get("studentSectionAssociation");

                if (null != assocs) {

                    for (Map<String, Object> assoc : assocs) {
                        Map<String, String> body = (Map<String, String>) assoc.get("body");
                        final String studentId = body.get("studentId");
                        Set<String> leas = this.studentCache.getEntriesById(studentId);
                        for (String lea2 : leas) {
                            Predicate<Entity> filter = new Predicate<Entity>() {
                                @Override
                                public boolean apply(Entity input) {
                                    Map<String, Object> body = input.getBody();
                                    return body.keySet().contains("studentId") && studentId.equals(body.get("studentId"));
                                }
                            };

                            extract(section, lea2, filter);
                        }
                    }
                }

            }


        }
    }

    @SuppressWarnings("unchecked")
    private void extract(Entity section, String lea, Predicate<Entity> filter) {
        this.entityExtractor.extractEntity(section, this.leaToExtractFileMap.getExtractFileForLea(lea), "section", filter);
        this.courseOfferingCache.addEntry((String) section.getBody().get("courseOfferingId"), lea);

        List<Map<String, Object>> ssas = section.getDenormalizedData().get("studentSectionAssociation");

        if (null != ssas) {
            for (Map<String, Object> ssa : ssas) {
                if (filter.apply(new BulkExtractEntity((Map<String, Object>) ssa.get("body"), (String) ssa.get("_id")))) {
                    this.ssaCache.addEntry((String) ssa.get("_id"), lea);
                }
            }
        }
    }

    public EntityToLeaCache getCourseOfferingCache() {
        return courseOfferingCache;
    }

    public EntityToLeaCache getSsaCache() {
        return ssaCache;
    }
}
