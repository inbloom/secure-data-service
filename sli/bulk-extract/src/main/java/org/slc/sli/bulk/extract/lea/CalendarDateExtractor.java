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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.LocalEdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;

public class CalendarDateExtractor implements EntityExtract {
	
    private static final Logger LOG = LoggerFactory.getLogger(EntityExtractor.class);
	private EntityExtractor extractor;
    private Repository<Entity> repo;
    private LEAExtractFileMap map;
    private ExtractorHelper helper;
    private LocalEdOrgExtractHelper localEdOrgExtractHelper;

	public CalendarDateExtractor(EntityExtractor extractor,  LEAExtractFileMap map, Repository<Entity> repo, 
			ExtractorHelper helper, EntityToLeaCache entityToLeaCache, LocalEdOrgExtractHelper localEdOrgExtractHelper) {
		this.extractor = extractor;
		this.repo = repo;
        this.map = map;
        this.helper = helper;
        this.localEdOrgExtractHelper = localEdOrgExtractHelper;
	}

	@Override
	public void extractEntities(EntityToLeaCache entityToEdorgCache) {
		Set<String> LEAs = map.getLeas();
		Set<String> edOrgSeen = new HashSet<String>();
        localEdOrgExtractHelper.logSecurityEvent(LEAs, EntityNames.CALENDAR_DATE, this.getClass().getName());
		Map<String, Collection<String>> schoolToLea = helper.buildSubToParentEdOrgCache(entityToEdorgCache);
		Iterator<Entity> calendarDates = repo.findEach(EntityNames.CALENDAR_DATE, new Query());
		while(calendarDates.hasNext()) {
			Entity calendarDate = calendarDates.next();
			
			// Calendar date entry can be tied to an LEA (any level) or a school
			String edOrgId = (String) calendarDate.getBody().get(ParameterConstants.EDUCATION_ORGANIZATION_ID);

			if(edOrgId != null) {
                Set<String> parents = entityToEdorgCache.leaFromEdorg(edOrgId);
                if(parents != null) {
                    for(String lea:parents) {
                        extractor.extractEntity(calendarDate, map.getExtractFileForLea(lea), EntityNames.CALENDAR_DATE);
                    }
                }
            }
		}
	}
}
