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

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.LocalEdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;

public class SessionExtractor implements EntityExtract {
	
    private static final Logger LOG = LoggerFactory.getLogger(EntityExtractor.class);
	private EntityExtractor extractor;
    private Repository<Entity> repo;
    private LEAExtractFileMap map;
    private EntityToLeaCache cache;
    private ExtractorHelper helper;
    private LocalEdOrgExtractHelper localEdOrgExtractHelper;


	public SessionExtractor(EntityExtractor extractor,  LEAExtractFileMap map, Repository<Entity> repo, 
			ExtractorHelper helper, EntityToLeaCache entityToLeaCache, LocalEdOrgExtractHelper localEdOrgExtractHelper) {
		this.extractor = extractor;
		this.repo = repo;
        this.map = map;
        this.cache = entityToLeaCache;
        this.helper = helper;
        this.localEdOrgExtractHelper = localEdOrgExtractHelper;
	}

	@Override
	public void extractEntities(EntityToLeaCache entityToEdorgCache) {
        localEdOrgExtractHelper.logSecurityEvent(map.getLeas(), EntityNames.SESSION, this.getClass().getName());
		Map<String, String> schoolToLea = helper.buildSubToParentEdOrgCache(entityToEdorgCache);
		Iterator<Entity> sessions = repo.findEach(EntityNames.SESSION, new Query());
		while(sessions.hasNext()) {
			Entity session = sessions.next();
			String schoolId = (String) session.getBody().get(ParameterConstants.SCHOOL_ID);
			Collection<String> gradingPeriods = (Collection<String>) session.getBody().get("gradingPeriodReference");

			String lea = schoolToLea.get(schoolId);
			if (lea == null) {
				LOG.warn("There is no LEA for school {}", schoolId);
				continue;
			}
			extractor.extractEntity(session, map.getExtractFileForLea(lea),
					EntityNames.SESSION);

			for (String gradingPeriod : gradingPeriods) {
				cache.addEntry(gradingPeriod, lea);
			}		
		}
	}
	
	public EntityToLeaCache getEntityToLeaCache() {
		return cache;
	}

}
