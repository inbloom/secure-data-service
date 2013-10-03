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
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
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
    private ExtractFileMap map;
    private EntityToEdOrgCache cache;
    private ExtractorHelper helper;
    private EdOrgExtractHelper edOrgExtractHelper;


	public SessionExtractor(EntityExtractor extractor,  ExtractFileMap map, Repository<Entity> repo,
			ExtractorHelper helper, EntityToEdOrgCache entityToEdOrgCache, EdOrgExtractHelper edOrgExtractHelper) {
		this.extractor = extractor;
		this.repo = repo;
        this.map = map;
        this.cache = entityToEdOrgCache;
        this.helper = helper;
        this.edOrgExtractHelper = edOrgExtractHelper;
	}

	@Override
	public void extractEntities(EntityToEdOrgCache entityToEdorgCache) {
        edOrgExtractHelper.logSecurityEvent(map.getEdOrgs(), EntityNames.SESSION, this.getClass().getName());
		Map<String, Collection<String>> schoolToLea = helper.buildSubToParentEdOrgCache(entityToEdorgCache);
		Iterator<Entity> sessions = repo.findEach(EntityNames.SESSION, new Query());
		while(sessions.hasNext()) {
			Entity session = sessions.next();
			String schoolId = (String) session.getBody().get(ParameterConstants.SCHOOL_ID);
			Collection<String> gradingPeriods = (Collection<String>) session.getBody().get("gradingPeriodReference");

			Collection<String> leas = schoolToLea.get(schoolId);
			if (leas == null) {
				LOG.warn("There is no LEA for school {}", schoolId);
				continue;
			}
            for(String lea:leas) {
                extractor.extractEntity(session, map.getExtractFileForEdOrg(lea),
                        EntityNames.SESSION);

                for (String gradingPeriod : gradingPeriods) {
                    cache.addEntry(gradingPeriod, lea);
                }
            }
		}
	}
	
	public EntityToEdOrgCache getEntityToLeaCache() {
		return cache;
	}

}
