package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 *
 */
@Component
public class EdOrgToChildEdOrgNodeFilter extends NodeFilter {

    private static final String REFERENCE = "parentEducationAgencyReference";
    @Autowired
    private Repository<Entity> repo;

    @Override
    public List<String> filterIds(List<String> ids) {
        Set<String> parents = fetchParents(new HashSet<String>(ids));
        Set<String> toReturn = new HashSet<String>(ids);
        Queue<String> toResolve = new LinkedList<String>(ids);

        while (!toResolve.isEmpty()) {
            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria(REFERENCE, NeutralCriteria.CRITERIA_IN, toResolve));
            Iterable<Entity> ents = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query);
            toResolve.clear();
            for (Iterator<Entity> i = ents.iterator(); i.hasNext();) {
                String childEdOrg = (String) i.next().getEntityId();
                toReturn.add(childEdOrg);
                toResolve.add(childEdOrg);
            }

        }
        toReturn.addAll(parents);
        return new ArrayList<String>(toReturn);
    }
    
    private Set<String> fetchParents(Set<String> ids) {
        Set<String> returned = new HashSet<String>(ids);
        String toResolve = "";
        for (String id : ids) {
            if (repo.exists(EntityNames.EDUCATION_ORGANIZATION, id)) {
                debug("We found a valid ED-ORG id {}", id);
                toResolve = id;
            }
        }
        while (toResolve.length() > 0) {
            Entity edOrg = repo.findById(EntityNames.EDUCATION_ORGANIZATION, toResolve);
            Map<String, Object> body = edOrg.getBody();
            if (body.containsKey(REFERENCE)) {
                toResolve = (String) body.get(REFERENCE);
            } else {
                toResolve = "";
            }
            debug("Adding a parent Ed-Org {}", (String) edOrg.getEntityId());
            returned.add((String) edOrg.getEntityId());
        }

        return returned;
    }


}
