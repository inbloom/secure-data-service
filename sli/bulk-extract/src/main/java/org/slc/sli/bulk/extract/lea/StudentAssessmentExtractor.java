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
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

/**
 * @author rlatta
 *
 */
public class StudentAssessmentExtractor implements EntityExtract {
    private EntityExtractor extractor;
    private LEAExtractFileMap map;
    private Repository<Entity> repo;
    
    public StudentAssessmentExtractor(EntityExtractor extractor, LEAExtractFileMap map, Repository<Entity> repo) {
        this.repo = repo;
        this.extractor = extractor;
        this.map = map;
    }

    /* (non-Javadoc)
     * @see org.slc.sli.bulk.extract.lea.EntityExtract#extractEntities(org.slc.sli.bulk.extract.lea.EntityToLeaCache)
     */
    @Override
    public void extractEntities(EntityToLeaCache entityToEdorgCache) {
        Iterator<Entity> assessments = repo.findEach(EntityNames.STUDENT_ASSESSMENT, new NeutralQuery());
        while (assessments.hasNext()) {
            Entity assessment = assessments.next();
            String studentId = (String) assessment.getBody().get(ParameterConstants.STUDENT_ID);
            Set<String> studentLeas = entityToEdorgCache.getEntriesById(studentId);
            for (String lea : studentLeas) {
                extractor.extractEntity(assessment, map.getExtractFileForLea(lea), EntityNames.STUDENT_ASSESSMENT);
            }
            
        }
        
    }
    
}
