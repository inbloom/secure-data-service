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
    private EntityToEdOrgDateCache studentDatedCache;

    public CourseTranscriptExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo,
            EntityToEdOrgDateCache studentDatedCache) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.studentDatedCache = studentDatedCache;
    }

    @Override
    public void extractEntities(EntityToEdOrgDateCache studentAcademicRecordDateCache) {
        Iterator<Entity> cursor = repo.findEach(EntityNames.COURSE_TRANSCRIPT, new Query());
        while (cursor.hasNext()) {
            Entity courseTranscript = cursor.next();
            String studentId = (String) courseTranscript.getBody().get(ParameterConstants.STUDENT_ID);
            String studentAcademicRecordId = (String) courseTranscript.getBody().get(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID);
            Set<String>leaSet = new HashSet<String>();

            // Add the student's LEAs.
            Map<String, DateTime> studentEdOrgDate = studentDatedCache.getEntriesById(studentId);
            for (Map.Entry<String, DateTime> entry : studentEdOrgDate.entrySet()) {
                DateTime upToDate = entry.getValue();
                if (shouldExtract(courseTranscript, upToDate)) {
                    leaSet.add(entry.getKey());
                }
            }

            // Add the studentAcademicReference's LEAs.
            if (studentAcademicRecordId != null) {
                leaSet.addAll(studentAcademicRecordDateCache.getEntriesById(studentAcademicRecordId).keySet());
            }

            // Extract this entity for all of the referenced LEAs.
            Map<String, DateTime> sarEdOrgDate = studentAcademicRecordDateCache.getEntriesById(studentAcademicRecordId);
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

