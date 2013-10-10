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
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.bulk.extract.date.EntityDateHelper;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class StudentCompetencyExtractor implements EntityDatedExtract {

    private static final Logger LOG = LoggerFactory.getLogger(EntityExtractor.class);

    private final Repository<Entity> repository;
    private EntityExtractor entityExtractor;
    private ExtractFileMap leaToExtractFileMap;

    public StudentCompetencyExtractor(EntityExtractor entityExtractor, ExtractFileMap leaToExtractFileMap, Repository<Entity> repository) {
        this.entityExtractor = entityExtractor;
        this.leaToExtractFileMap = leaToExtractFileMap;
        this.repository = repository;
    }

    @Override
    public void extractEntities(EntityToEdOrgDateCache studentSectionToEdOrgDateCache) {
        Iterator<Entity> studentCompetencies = repository.findEach(EntityNames.STUDENT_COMPETENCY, new NeutralQuery());
        while(studentCompetencies.hasNext()) {
            Entity studentCompetency = studentCompetencies.next();
            String ssaId = (String) studentCompetency.getBody().get(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID);
            Map<String, DateTime> edOrgDates = studentSectionToEdOrgDateCache.getEntriesById(ssaId);
            for (Map.Entry<String, DateTime> edOrgDate : edOrgDates.entrySet()) {
                DateTime upToDate = edOrgDate.getValue();
                if (shouldExtract(studentCompetency, upToDate)) {
                    entityExtractor.extractEntity(studentCompetency, leaToExtractFileMap.getExtractFileForEdOrg(edOrgDate.getKey()),
                            EntityNames.STUDENT_COMPETENCY);
                }
            }
        }
    }

    protected boolean shouldExtract(Entity input, DateTime upToDate) {
        return EntityDateHelper.shouldExtract(input, upToDate);
    }

}
