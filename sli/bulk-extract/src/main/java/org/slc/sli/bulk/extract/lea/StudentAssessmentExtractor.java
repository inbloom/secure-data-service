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
import java.util.Set;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * @author rlatta
 *
 */
public class StudentAssessmentExtractor implements EntityExtract {
    private EntityExtractor extractor;
    private ExtractFileMap map;
    private Repository<Entity> repo;
    private EdOrgExtractHelper edOrgExtractHelper;
    
    public StudentAssessmentExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
        this.repo = repo;
        this.extractor = extractor;
        this.map = map;
        this.edOrgExtractHelper = edOrgExtractHelper;
    }

    /* (non-Javadoc)
     * @see org.slc.sli.bulk.extract.lea.EntityExtract#extractEntities(org.slc.sli.bulk.extract.lea.EntityToLeaCache)
     */
    @Override
    public void extractEntities(EntityToEdOrgCache entityToEdorgCache) {
        edOrgExtractHelper.logSecurityEvent(map.getEdOrgs(), EntityNames.STUDENT_ASSESSMENT, this.getClass().getName());
        Iterator<Entity> assessments = repo.findEach(EntityNames.STUDENT_ASSESSMENT, new NeutralQuery());
        while (assessments.hasNext()) {
            Entity assessment = assessments.next();
            String studentId = (String) assessment.getBody().get(ParameterConstants.STUDENT_ID);
            Set<String> studentEdOrg = entityToEdorgCache.getEntriesById(studentId);
            for (String stidentEdOrg : studentEdOrg) {
                extractor.extractEntity(assessment, map.getExtractFileForEdOrg(stidentEdOrg), EntityNames.STUDENT_ASSESSMENT);
            }
            
        }
        
    }
    
}
