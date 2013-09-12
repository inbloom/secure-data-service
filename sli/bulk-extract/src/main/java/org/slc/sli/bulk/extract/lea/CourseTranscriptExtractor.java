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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

public class CourseTranscriptExtractor {
    private EntityExtractor extractor;
    private LEAExtractFileMap map;
    private Repository<Entity> repo;
    
    public CourseTranscriptExtractor(EntityExtractor extractor, LEAExtractFileMap map, Repository<Entity> repo) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
    }

    
    public void extractEntities(EntityToLeaCache edorgCache, EntityToLeaCache studentCache, EntityToLeaCache studentAcademicRecordCache) {
        Iterator<Entity> cursor = repo.findEach(EntityNames.COURSE_TRANSCRIPT, new Query());
        while (cursor.hasNext()) {
            Entity e = cursor.next();
            List<String> edorgReferences = (List<String>)e.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE);
            String studentId = (String)e.getBody().get(ParameterConstants.STUDENT_ID);
            String studentAcademicRecordReference = (String)e.getBody().get(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID);
            
            //add directly references edorgs
            Set<String> leaSet = new HashSet<String>();
            if(edorgReferences!=null){
                for ( String edorg : edorgReferences ){
                    leaSet.addAll(edorgCache.leaFromEdorg(edorg));
                }
            }
            
            //add the students leas
            leaSet.addAll(studentCache.getEntriesById(studentId));
            
            //add the studentAcademicReferences
            if(studentAcademicRecordReference!=null) {
                leaSet.addAll(studentAcademicRecordCache.getEntriesById(studentAcademicRecordReference));
            }
            
            //extract this entity for all of the referenced LEAs
            for (String lea : leaSet) {
                extractor.extractEntity(e, map.getExtractFileForLea(lea), EntityNames.COURSE_TRANSCRIPT);
            }
        }
    }
}

