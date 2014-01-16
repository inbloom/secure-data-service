/**
 *
 */
package org.slc.sli.bulk.extract.context.resolver.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

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
public class SimpleEntityTypeContextResolver extends ReferrableResolver {

    @Override
    public Set<String> findGoverningEdOrgs(Entity entity) {
        
        return Collections.emptySet();
        
    }
    
    @Override
    public String getCollection() {
        return EntityNames.EDUCATION_ORGANIZATION;
    }

    @Override
    protected Set<String> resolve(Entity baseEntity, Entity entityToExtract) {
        return null;
    }
}
