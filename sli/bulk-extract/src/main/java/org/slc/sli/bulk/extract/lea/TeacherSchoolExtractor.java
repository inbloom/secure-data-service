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

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.date.EntityDateHelper;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class TeacherSchoolExtractor implements EntityDatedExtract {
    private EntityExtractor extractor;
    private ExtractFileMap map;
    private Repository<Entity> repo;
    private EdOrgExtractHelper edOrgExtractHelper;
    
    public TeacherSchoolExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.edOrgExtractHelper = edOrgExtractHelper;
    }

    @Override
    public void extractEntities(EntityToEdOrgDateCache staffToEdorgDatedCache) {
        edOrgExtractHelper.logSecurityEvent(map.getEdOrgs(), EntityNames.TEACHER_SCHOOL_ASSOCIATION, this.getClass().getName());
        Iterator<Entity> teachers = repo.findEach(EntityNames.TEACHER_SCHOOL_ASSOCIATION, new NeutralQuery());
        while (teachers.hasNext()) {
            Entity tsa = teachers.next();
            String teacherId = (String) tsa.getBody().get(ParameterConstants.TEACHER_ID);
            Map<String, DateTime> edOrgDates = staffToEdorgDatedCache.getEntriesById(teacherId);
            if (edOrgDates == null || edOrgDates.size() == 0) {
                continue;
            }

            String edorgId = (String) tsa.getBody().get(ParameterConstants.SCHOOL_ID);
            Iterable<Entity> seaos = retrieveSEOAS(teacherId, edorgId);
            for (Map.Entry<String, DateTime> edOrgDate : edOrgDates.entrySet()) {
                DateTime upToDate = edOrgDate.getValue();

                if (shouldExtract(seaos, upToDate)) {
                    extractor.extractEntity(tsa, map.getExtractFileForEdOrg(edOrgDate.getKey()), EntityNames.TEACHER_SCHOOL_ASSOCIATION);
                }
            }
            
        }
        
    }

    protected boolean shouldExtract(Iterable<Entity> seaos, DateTime upToDate) {
        boolean shouldExtract = false;
        for (Entity seao : seaos) {
            if (EntityDateHelper.shouldExtract(seao, upToDate)) {
                shouldExtract = true;
                break;
            }
        }
        return shouldExtract;
    }

    protected Iterable<Entity> retrieveSEOAS(String teacherId, String edorgId) {
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, NeutralCriteria.OPERATOR_EQUAL, edorgId));
        query.addCriteria(new NeutralCriteria(ParameterConstants.STAFF_REFERENCE, NeutralCriteria.OPERATOR_EQUAL, teacherId));

        return repo.findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, query);
    }
}
