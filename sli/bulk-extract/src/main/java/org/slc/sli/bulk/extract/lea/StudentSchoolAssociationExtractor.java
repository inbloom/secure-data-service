package org.slc.sli.bulk.extract.lea;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.date.EntityDateHelper;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class StudentSchoolAssociationExtractor implements EntityDatedExtract {
    private ExtractFileMap map;
    private EntityExtractor extractor;
    private EntityToEdOrgDateCache cache;
    private EntityToEdOrgCache graduationPlanCache;
    private Repository<Entity> repo;
    private EdOrgExtractHelper helper;
    
    public StudentSchoolAssociationExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo,
                                             EntityToEdOrgDateCache cache, EdOrgExtractHelper helper) {
        this.extractor = extractor;
        this.map = map;
        this.cache = cache;
        this.graduationPlanCache = new EntityToEdOrgCache();
        this.repo = repo;
        this.helper = helper;
    }


    @Override
    public void extractEntities(EntityToEdOrgDateCache entityCache) {
        helper.logSecurityEvent(map.getEdOrgs(), EntityNames.STUDENT_SCHOOL_ASSOCIATION, this.getClass().getName());
        Iterator<Entity> cursor = repo.findEach("studentSchoolAssociation", new NeutralQuery());
        while(cursor.hasNext()) {
            Entity ssa = cursor.next();
            String graduationPlanId = (String) ssa.getBody().get("graduationPlanId");
            Map<String, DateTime> studentEdOrgDate = cache.getEntriesById((String) ssa.getBody().get("studentId"));

            for (Map.Entry<String, DateTime> entry: studentEdOrgDate.entrySet()) {
                DateTime upToDate = entry.getValue();
                if(EntityDateHelper.shouldExtract(ssa, upToDate)) {
                    extractor.extractEntity(ssa, map.getExtractFileForEdOrg(entry.getKey()), EntityNames.STUDENT_SCHOOL_ASSOCIATION);
                    graduationPlanCache.addEntry(graduationPlanId, entry.getKey());
                }
            }

        }
    }

    public EntityToEdOrgCache getGraduationPlanCache() {
        return graduationPlanCache;
    }

}
