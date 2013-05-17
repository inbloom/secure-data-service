package org.slc.sli.bulk.extract.lea;

import java.util.Iterator;
import java.util.Set;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.LocalEdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

public class StudentSchoolAssociationExtractor implements EntityExtract {
    private LEAExtractFileMap map;
    private EntityExtractor extractor;
    private EntityToLeaCache cache;
    private Repository<Entity> repo;
    private LocalEdOrgExtractHelper helper;
    
    public StudentSchoolAssociationExtractor(EntityExtractor extractor, LEAExtractFileMap map,  Repository<Entity> repo, 
    		EntityToLeaCache cache, LocalEdOrgExtractHelper helper) {
        this.extractor = extractor;
        this.map = map;
        this.cache = cache;
        this.repo = repo;
        this.helper = helper;
    }

	
	@Override
    public void extractEntities(EntityToLeaCache entityCache) {
        helper.logSecurityEvent(map.getLeas(), EntityNames.STUDENT_SCHOOL_ASSOCIATION, this.getClass().getName());
        Iterator<Entity> cursor = repo.findEach("studentSchoolAssociation", new NeutralQuery());
        while(cursor.hasNext()) {
        	Entity ssa = cursor.next();
        	Set<String> leas = cache.getEntriesById((String) ssa.getBody().get("studentId"));
        	for (String lea : leas) {
        		extractor.extractEntity(ssa, map.getExtractFileForLea(lea), "studentSchoolAssociation");
        	}
        }
	}
}
