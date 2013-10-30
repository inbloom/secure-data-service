package org.slc.sli.bulk.extract.lea;

import java.util.Iterator;
import java.util.Map;

import org.joda.time.DateTime;

import org.slc.sli.bulk.extract.date.EntityDateHelper;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

public class StudentSchoolAssociationExtractor implements EntityDatedExtract {
    private ExtractFileMap map;
    private EntityExtractor extractor;
    private Repository<Entity> repo;
    private EdOrgExtractHelper helper;

    public StudentSchoolAssociationExtractor(EntityExtractor extractor, ExtractFileMap map, Repository<Entity> repo, EdOrgExtractHelper helper) {
        this.extractor = extractor;
        this.map = map;
        this.repo = repo;
        this.helper = helper;
    }


    @Override
    public void extractEntities(EntityToEdOrgDateCache studentDateCache) {
        helper.logSecurityEvent(map.getEdOrgs(), EntityNames.STUDENT_SCHOOL_ASSOCIATION, this.getClass().getName());
        Iterator<Entity> cursor = repo.findEach("studentSchoolAssociation", new NeutralQuery());
        while(cursor.hasNext()) {
            Entity ssa = cursor.next();
            Map<String, DateTime> studentEdOrgDate = studentDateCache.getEntriesById((String) ssa.getBody().get(ParameterConstants.STUDENT_ID));

            for (Map.Entry<String, DateTime> entry: studentEdOrgDate.entrySet()) {
                DateTime upToDate = entry.getValue();
                if(EntityDateHelper.shouldExtract(ssa, upToDate)) {
                    extractor.extractEntity(ssa, map.getExtractFileForEdOrg(entry.getKey()), EntityNames.STUDENT_SCHOOL_ASSOCIATION);
                }
            }

        }
    }

}
