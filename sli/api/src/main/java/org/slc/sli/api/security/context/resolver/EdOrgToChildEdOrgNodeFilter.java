package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 *
 *
 */
@Component
public class EdOrgToChildEdOrgNodeFilter extends NodeFilter {

    @Autowired
    private Repository<Entity> repo;

    @Override
    public List<String> filterIds(List<String> ids) {
        List<String> toReturn = new ArrayList<String>(ids);
        Queue<String> toResolve = new LinkedList<String>(ids);

        while (!toResolve.isEmpty()) {
            String edOrgId = toResolve.remove();
            NeutralQuery query = new NeutralQuery();
            query.addCriteria(new NeutralCriteria("educationOrganizationParentId", NeutralCriteria.OPERATOR_EQUAL, edOrgId));
            Iterable<Entity> ents = repo.findAll("educationOrganizationAssociation", query);
            for (Iterator<Entity> i = ents.iterator(); i.hasNext();) {
                String childEdOrg = (String) i.next().getBody().get("educationOrganizationChildId");
                toReturn.add(childEdOrg);
                toResolve.add(childEdOrg);
            }

        }
        return toReturn;
    }


}
