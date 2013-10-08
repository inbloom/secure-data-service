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
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.bulk.extract.date.EntityDateHelper;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

public class CourseTranscriptExtractor implements EntityDatedExtract {
    private EntityExtractor extractor;
    private ExtractFileMap map;
    private Repository<Entity> repo;
    private EntityToEdOrgCache edorgCache;
    private EntityToEdOrgCache studentCache;
    private EntityToEdOrgCache studentAcademicRecordCache;

    public CourseTranscriptExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo,
            EntityToEdOrgCache edorgCache, EntityToEdOrgCache studentCache, EntityToEdOrgCache studentAcademicRecordCache) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.edorgCache = edorgCache;
        this.studentCache = studentCache;
        this.studentAcademicRecordCache = studentAcademicRecordCache;
    }

    @Override
    public void extractEntities(EntityToEdOrgDateCache datedCache) {
        Iterator<Entity> cursor = repo.findEach(EntityNames.COURSE_TRANSCRIPT, new Query());
        while (cursor.hasNext()) {
            Entity e = cursor.next();
            List<String> edorgReferences = (List<String>)e.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE);
            String studentId = (String)e.getBody().get(ParameterConstants.STUDENT_ID);
            String studentAcademicRecordReference = (String)e.getBody().get(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID);

            //add directly references edorgs
            Set<String>leaSet = new HashSet<String>();
            if(edorgReferences!=null){
                for ( String edorg : edorgReferences ){
                    leaSet.addAll(edorgCache.ancestorEdorgs(edorg));
                }
            }

            //add the students leas
            leaSet.addAll(studentCache.getEntriesById(studentId));

            //add the studentAcademicReferences
            if(studentAcademicRecordReference!=null) {
                leaSet.addAll(studentAcademicRecordCache.getEntriesById(studentAcademicRecordReference));
            }

            //extract this entity for all of the referenced LEAs
            Map<String, DateTime> studentEdOrgDate = datedCache.getEntriesById((String) e.getBody().get("studentId"));
            for (String lea : leaSet) {
                DateTime upToDate = studentEdOrgDate.get(lea);
                if (shouldExtract(e, upToDate)) {
                    extractor.extractEntity(e, map.getExtractFileForEdOrg(lea), EntityNames.COURSE_TRANSCRIPT);
                }
            }
        }
    }

    protected boolean shouldExtract(Entity input, DateTime upToDate) {
        return EntityDateHelper.shouldExtract(input, upToDate);
    }

}

