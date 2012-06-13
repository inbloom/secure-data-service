package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.service.BasicService;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author pwolf
 *
 */
@Component
public class ResolveCreatorsEntitiesHelper {

    @Autowired
    private Repository<Entity> repo;

    private final HashMap<String, String> entityCollectionTransform = new HashMap<String, String>();

    @PostConstruct
    public void init() {
        entityCollectionTransform.put("teacher", "staff");
        entityCollectionTransform.put("school", "educationOrganization");
    }

    public List<String> getAllowedForCreator(String toEntityIn) {
        String toEntity;

        toEntity = entityCollectionTransform.get(toEntityIn);
        if (toEntity == null) {
            toEntity = toEntityIn;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //would be null early in the oauth process before we've fully created an Authentication for the user
        if (auth == null) {
            return new ArrayList<String>();
        }
        SLIPrincipal user = (SLIPrincipal) auth.getPrincipal();
        String userId = user.getEntity().getEntityId();
        NeutralQuery nq = new NeutralQuery(new NeutralCriteria("metaData.createdBy", NeutralCriteria.OPERATOR_EQUAL, userId, false));
        nq.addCriteria(new NeutralCriteria("metaData.isOrphaned", NeutralCriteria.OPERATOR_EQUAL, "true", false));
//        BasicService.addDefaultQueryParams(nq, EntityNames.EDUCATION_ORGANIZATION);
        List<String> createdIds = (List<String>) repo.findAllIds(toEntity, nq);

        return createdIds;
    }

}
