package org.slc.sli.api.security.context.resolver;

import java.util.List;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ResolveCreatorsEntitiesHelper {

    @Autowired
    private Repository<Entity> repo;
    
    public List<String> getAllowedForCreator(String toEntity) {
        SLIPrincipal user = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = user.getEntity().getEntityId();
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("metaData.createdBy", NeutralCriteria.OPERATOR_EQUAL, userId, false));
        nq.addCriteria(new NeutralCriteria("metaData.isOrphaned", NeutralCriteria.OPERATOR_EQUAL, "true", false));
        List<String> createdIds = (List<String>) repo.findAllIds(toEntity, nq);
        
        return createdIds;
    }

}
