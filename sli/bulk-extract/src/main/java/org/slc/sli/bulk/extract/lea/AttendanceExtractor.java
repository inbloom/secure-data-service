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
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class AttendanceExtractor implements EntityDatedExtract {
    private EntityExtractor extractor;
    private ExtractFileMap map;
    private Repository<Entity> repo;
    private EdOrgExtractHelper edOrgExtractHelper;

    public AttendanceExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo,
            EdOrgExtractHelper edOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.edOrgExtractHelper = edOrgExtractHelper;
    }

    @Override
    public void extractEntities(EntityToEdOrgDateCache studentCache) {
        edOrgExtractHelper.logSecurityEvent(map.getEdOrgs(), EntityNames.ATTENDANCE, this.getClass().getName());
        Iterator<Entity> attendances = repo.findEach("attendance", new NeutralQuery());
        while (attendances.hasNext()) {
            Entity attendance = attendances.next();

            Map<String, DateTime> studentEdOrgDate = studentCache.getEntriesById((String) attendance.getBody().get("studentId"));
            String schoolYear = EntityDateHelper.retrieveDate(attendance);

            for (Map.Entry<String, DateTime> entry: studentEdOrgDate.entrySet()) {
                DateTime upToDate = entry.getValue();
                if (EntityDateHelper.shouldExtract(schoolYear, upToDate, EntityNames.ATTENDANCE)) {
                    extractor.extractEntity(attendance, map.getExtractFileForEdOrg(entry.getKey()), EntityNames.ATTENDANCE);
                }
            }
        }

    }

}
