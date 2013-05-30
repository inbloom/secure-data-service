/**
 * 
 */
package org.slc.sli.bulk.extract.context.resolver.impl;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.slc.sli.domain.utils.EdOrgHierarchyHelper;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Context Resolver for Simple Entity Type as listed below:
 * LearningObjective
 * LearningStandard
 * CompetencyLevelDescriptor
 * StudentCompetencyObjective
 * Program
 * 
 * @author lchen
 *
 */
@Component
public class SimpleEntityTypeContextResolver implements ContextResolver, InitializingBean {


	
	@Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> repo;
		
	private final Set<String> cache = new HashSet<String>();
	
	/**
	 * @param args
	 */
	
	@Override
    public void afterPropertiesSet() throws Exception {
		
		NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("type", NeutralCriteria.OPERATOR_EQUAL, "stateEducationAgency"));
    	
    	Iterable<Entity> edOrgs = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query); 
    	
    	for (Entity edOrg : edOrgs) {
    			cache.add(edOrg.getEntityId());
    	}	
	}
	
	
	
	@Override
    public Set<String> findGoverningEdOrgs(Entity entity) {
        if (entity == null) {
            return Collections.emptySet();
        }
		
    	return cache;		

	}


}
