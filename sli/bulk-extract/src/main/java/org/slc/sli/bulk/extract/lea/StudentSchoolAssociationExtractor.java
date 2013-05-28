package org.slc.sli.bulk.extract.lea;

import java.util.Iterator;
import java.util.Set;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.LocalEdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class StudentSchoolAssociationExtractor implements EntityExtract {
    private LEAExtractFileMap map;
    private EntityExtractor extractor;
    private EntityToLeaCache cache;
    private EntityToLeaCache graduationPlanCache;
    private Repository<Entity> repo;
    private LocalEdOrgExtractHelper helper;
    
    public StudentSchoolAssociationExtractor(EntityExtractor extractor, LEAExtractFileMap map, Repository<Entity> repo,
                                             EntityToLeaCache cache, LocalEdOrgExtractHelper helper) {
        this.extractor = extractor;
        this.map = map;
        this.cache = cache;
        this.graduationPlanCache = new EntityToLeaCache();
        this.repo = repo;
        this.helper = helper;
    }

	
	@Override
    public void extractEntities(EntityToLeaCache entityCache) {
        helper.logSecurityEvent(map.getLeas(), EntityNames.STUDENT_SCHOOL_ASSOCIATION, this.getClass().getName());
        Iterator<Entity> cursor = repo.findEach("studentSchoolAssociation", new NeutralQuery());
        while(cursor.hasNext()) {
        	Entity ssa = cursor.next();
            String graduationPlanId = (String) ssa.getBody().get("graduationPlanId");
        	Set<String> leas = cache.getEntriesById((String) ssa.getBody().get("studentId"));
        	for (String lea : leas) {
        		extractor.extractEntity(ssa, map.getExtractFileForLea(lea), "studentSchoolAssociation");

                graduationPlanCache.addEntry(graduationPlanId, lea);

            }
        }
	}

    public EntityToLeaCache getGraduationPlanCache() {
        return graduationPlanCache;
    }

}
