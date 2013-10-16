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
import java.util.Set;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.bulk.extract.date.EntityDateHelper;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.ContainerEntityNames;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class YearlyTranscriptExtractor implements EntityDatedExtract {
    private EntityExtractor extractor;
    private ExtractFileMap map;
    private Repository<Entity> repo;
    private EdOrgExtractHelper edOrgExtractHelper;
    private EntityToEdOrgDateCache studentAcademicRecordDateCache;
    private static final Logger LOG = LoggerFactory.getLogger(YearlyTranscriptExtractor.class);

    public YearlyTranscriptExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo,
            EdOrgExtractHelper edOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.edOrgExtractHelper = edOrgExtractHelper;
        this.studentAcademicRecordDateCache = new EntityToEdOrgDateCache();
    }

    @Override
    public void extractEntities(EntityToEdOrgDateCache studentDatedCache) {
        edOrgExtractHelper.logSecurityEvent(map.getEdOrgs(), "yearlyTranscript", this.getClass().getName());
        Iterator<Entity> yearlyTranscripts = repo.findEach("yearlyTranscript", new NeutralQuery());

        while (yearlyTranscripts.hasNext()) {
            Entity yearlyTranscript = yearlyTranscripts.next();
            String studentId = (String) yearlyTranscript.getBody().get(ParameterConstants.STUDENT_ID);
            final Map<String, DateTime> studentEdOrgs = studentDatedCache.getEntriesById(studentId);
            Set<String> studentAcademicRecords = fetchStudentAcademicRecordsFromYearlyTranscript(yearlyTranscript);
            for (Map.Entry<String, DateTime> studentEdOrg : studentEdOrgs.entrySet()) {
                if (shouldExtract(yearlyTranscript, studentEdOrg.getValue())) {
                    extractor.extractEntity(yearlyTranscript, map.getExtractFileForEdOrg(studentEdOrg.getKey()), ContainerEntityNames.YEARLY_TRANSCRIPT);
                    for (String sarId : studentAcademicRecords) {
                        studentAcademicRecordDateCache.addEntry(sarId, studentEdOrg.getKey(), studentEdOrg.getValue());
                    }
                }
            }
        }
    }

    /**
     * returns all parents of the student
     * @param student
     * @return
     */
    private Set<String> fetchStudentAcademicRecordsFromYearlyTranscript(Entity yearlyTranscript) {
        Set<String> records = new TreeSet<String>();
        if (yearlyTranscript.getContainerData().containsKey(EntityNames.STUDENT_ACADEMIC_RECORD)) {
            for (Entity sar : yearlyTranscript.getContainerData().get(EntityNames.STUDENT_ACADEMIC_RECORD)) {
                records.add(sar.getEntityId());
            }
        }
        return records;
    }

    protected boolean shouldExtract(Entity input, DateTime upToDate) {
        return EntityDateHelper.shouldExtract(input, upToDate);
    }

    /**
     * Get the cache of studentAcademicRecordIds to a list of LEA IDs that these records were extracted to
     * @return
     */
    public EntityToEdOrgDateCache getStudentAcademicRecordDateCache(){
        return studentAcademicRecordDateCache;
    }
}
