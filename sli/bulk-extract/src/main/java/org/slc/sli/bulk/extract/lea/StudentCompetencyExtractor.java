package org.slc.sli.bulk.extract.lea;

import java.util.Iterator;
import java.util.Set;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudentCompetencyExtractor implements EntityExtract {
    private static final Logger LOG = LoggerFactory.getLogger(EntityExtractor.class);

    private final Repository<Entity> repository;
    private EntityExtractor entityExtractor; 
    private ExtractFileMap leaToExtractFileMap;
    
    public StudentCompetencyExtractor(EntityExtractor entityExtractor, ExtractFileMap leaToExtractFileMap, Repository<Entity> repository) {
    	this.entityExtractor = entityExtractor;
    	this.leaToExtractFileMap = leaToExtractFileMap;
    	this.repository = repository;
    }

	@Override
	public void extractEntities(EntityToEdOrgCache studentSectionToEdOrgCache) {
		Iterator<Entity> studentCompetencies = repository.findEach(EntityNames.STUDENT_COMPETENCY, new NeutralQuery());
		while(studentCompetencies.hasNext()) {
			Entity studentCompetency = studentCompetencies.next();
			Set<String> edOrgs = studentSectionToEdOrgCache.getEntriesById(
					(String) studentCompetency.getBody().get(ParameterConstants.STUDENT_SECTION_ASSOCIATION_ID));
			for (String edOrg : edOrgs) {
				entityExtractor.extractEntity(studentCompetency, leaToExtractFileMap.getExtractFileForEdOrg(edOrg), EntityNames.STUDENT_COMPETENCY);
			}
		}
	}

}
