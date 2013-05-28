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

public class GraduationPlanExtractor {
    private EntityExtractor extractor;
    private LEAExtractFileMap map;
    private Repository<Entity> repo;
    private LocalEdOrgExtractHelper localEdOrgExtractHelper;
    
    public GraduationPlanExtractor(EntityExtractor extractor, LEAExtractFileMap map, Repository<Entity> repo, LocalEdOrgExtractHelper localEdOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.localEdOrgExtractHelper = localEdOrgExtractHelper;
    }
    
    public void extractEntities(EntityToLeaCache edorgCache, EntityToLeaCache graduationPlanCache) {
        localEdOrgExtractHelper.logSecurityEvent(map.getLeas(), EntityNames.GRADUATION_PLAN, this.getClass().getName());
        Iterator<Entity> cursor = repo.findEach(EntityNames.GRADUATION_PLAN, new Query());
        while (cursor.hasNext()) {
            Entity e = cursor.next();
            String graduationPlanId = e.getEntityId();
            Set<String> leas = graduationPlanCache.getEntriesById(graduationPlanId);
            String edorgId = (String)e.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_ID);
            
            if(edorgId !=null){
                String leaForGraduationPlan = edorgCache.leaFromEdorg(edorgId);
                if(!leas.contains(leaForGraduationPlan)){
                    extractor.extractEntity(e, map.getExtractFileForLea(leaForGraduationPlan), EntityNames.GRADUATION_PLAN);
                }
            }
            
            for (String lea : leas) {
                extractor.extractEntity(e, map.getExtractFileForLea(lea), EntityNames.GRADUATION_PLAN);
            }
        }
    }
}
