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
import java.util.Set;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

public class GradingPeriodExtractor implements EntityExtract {
	private EntityExtractor extractor;
	private ExtractFileMap map;
	private Repository<Entity> repo;
    private EdOrgExtractHelper edOrgExtractHelper;
	
	public GradingPeriodExtractor(EntityExtractor extractor,  ExtractFileMap map, Repository<Entity> repo, EdOrgExtractHelper edOrgExtractHelper) {
		this.extractor = extractor;
		this.map = map;
		this.repo = repo;
        this.edOrgExtractHelper = edOrgExtractHelper;
	}

	@Override
	public void extractEntities(EntityToEdOrgCache gradingPeriodToEdOrgCache) {
        edOrgExtractHelper.logSecurityEvent(map.getEdOrgs(), EntityNames.GRADING_PERIOD, this.getClass().getName());
		Iterator<Entity> gradingPeriods = repo.findEach(EntityNames.GRADING_PERIOD, new Query());
		while(gradingPeriods.hasNext()) {
			Entity gradingPeriod = gradingPeriods.next();
			Set<String> edOrgs = gradingPeriodToEdOrgCache.getEntriesById(gradingPeriod.getEntityId());
			for (String edOrg : edOrgs) {
				extractor.extractEntity(gradingPeriod, map.getExtractFileForEdOrg(edOrg), EntityNames.GRADING_PERIOD);
			}
		}
	}

}
