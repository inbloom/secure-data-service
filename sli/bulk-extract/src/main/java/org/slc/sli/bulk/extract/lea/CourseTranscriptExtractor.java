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
    private EntityToEdOrgDateCache studentDatedCache;

    public CourseTranscriptExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo,
            EntityToEdOrgCache edorgCache, EntityToEdOrgDateCache studentDatedCache) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.edorgCache = edorgCache;
        this.studentDatedCache = studentDatedCache;
    }

    @Override
    public void extractEntities(EntityToEdOrgDateCache studentAcademicRecordDateCache) {
        Iterator<Entity> cursor = repo.findEach(EntityNames.COURSE_TRANSCRIPT, new Query());
        while (cursor.hasNext()) {
            Entity courseTranscript = cursor.next();
            List<String> edorgReferences = (List<String>) courseTranscript.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE);
            String studentId = (String) courseTranscript.getBody().get(ParameterConstants.STUDENT_ID);
            String studentAcademicRecordReference = (String) courseTranscript.getBody().get(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID);

            //add directly references edorgs
            Set<String>leaSet = new HashSet<String>();
            if (edorgReferences != null){
                for (String edorg : edorgReferences) {
                    leaSet.addAll(edorgCache.ancestorEdorgs(edorg));
                }
            }

            //add the students leas
            Map<String, DateTime> studentEdOrgDate = studentDatedCache.getEntriesById(studentId);
            for (Map.Entry<String, DateTime> entry: studentEdOrgDate.entrySet()) {
                DateTime upToDate = entry.getValue();
                if (shouldExtract(courseTranscript, upToDate)) {
                    leaSet.add(entry.getKey());
                }
            }

            //add the studentAcademicReferences
            if (studentAcademicRecordReference != null) {
                leaSet.addAll(studentAcademicRecordDateCache.getEntriesById(studentAcademicRecordReference).keySet());
            }

            //extract this entity for all of the referenced LEAs
            Map<String, DateTime> sarEdOrgDate = studentAcademicRecordDateCache.getEntriesById(
                    (String) courseTranscript.getBody().get(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID));
            for (String lea : leaSet) {
                DateTime upToDate = sarEdOrgDate.get(lea);
                if (shouldExtract(courseTranscript, upToDate)) {
                    extractor.extractEntity(courseTranscript, map.getExtractFileForEdOrg(lea), EntityNames.COURSE_TRANSCRIPT);
                }
            }
        }
    }

    protected boolean shouldExtract(Entity input, DateTime upToDate) {
        return EntityDateHelper.shouldExtract(input, upToDate);
    }

}

