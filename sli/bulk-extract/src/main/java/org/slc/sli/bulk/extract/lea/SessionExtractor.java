package org.slc.sli.bulk.extract.lea;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
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


	public SessionExtractor(EntityExtractor extractor,  LEAExtractFileMap map, Repository<Entity> repo, 
			ExtractorHelper helper, EntityToLeaCache entityToLeaCache) {
		this.extractor = extractor;
		this.repo = repo;
        this.map = map;
        this.cache = entityToLeaCache;
        this.helper = helper;
	}

	@Override
	public void extractEntities(EntityToLeaCache entityToEdorgCache) {
		Map<String, String> schoolToLea = helper.buildSubToParentEdOrgCache(entityToEdorgCache);
		Iterator<Entity> sessions = repo.findEach(EntityNames.SESSION, new Query());
		while(sessions.hasNext()) {
			Entity session = sessions.next();
			String schoolId = (String) session.getBody().get(ParameterConstants.SCHOOL_ID);
			Collection<String> gradingPeriods = (Collection<String>) session.getBody().get("gradingPeriodReference");

			String lea = schoolToLea.get(schoolId);
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
