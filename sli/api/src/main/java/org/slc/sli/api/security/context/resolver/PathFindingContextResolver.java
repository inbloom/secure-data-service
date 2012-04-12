/**
 * 
 */
package org.slc.sli.api.security.context.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.BrutePathFinder;
import org.slc.sli.api.security.context.traversal.graph.SecurityNode;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class to adapt our path finding technique to our context resolving system.
 * 
 * @author rlatta
 */
@Component
public class PathFindingContextResolver implements EntityContextResolver {
    
    @Autowired
    private BrutePathFinder pathFinder;
    
    @Autowired
    private AssociativeContextHelper helper;
    
    @Autowired
    private Repository<Entity> repository;

    private String fromEntity;
    private String toEntity;
    
    /*
     * @see
     * org.slc.sli.api.security.context.resolver.EntityContextResolver#canResolve(java.lang.String,
     * java.lang.String)
     */
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        this.fromEntity = fromEntityType;
        this.toEntity = toEntityType;
        Set<String> entities = pathFinder.getNodeMap().keySet();
        return (entities.contains(fromEntityType) && entities.contains(toEntityType));
    }
    
    /*
     * @see
     * org.slc.sli.api.security.context.resolver.EntityContextResolver#findAccessible(org.slc.sli
     * .domain.Entity)
     */
    @Override
    public List<String> findAccessible(Entity principal) {
        List<SecurityNode> path = pathFinder.find(fromEntity, toEntity);
        if (path == null)
            return new ArrayList<String>();
        List<String> ids = new ArrayList<String>(Arrays.asList(principal.getEntityId()));
        SecurityNode current = path.get(0);
        for (int i = 1; i< path.size(); ++i) {
            SecurityNode next = path.get(i);
            Map<String, String> connection = current.getConnectionForEntity(next.getName());
            String repoName = getResourceName(next, connection);
            Iterable<String> idSet = repository.findAllIds(repoName, buildQuery(ids, connection));
            fixIds(ids, idSet);
            current = path.get(i);
        }
        return ids;
    }

    private NeutralQuery buildQuery(List<String> ids, Map<String, String> connection) {
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria(connection.get(SecurityNode.CONNECTION_FIELD_NAME),
                NeutralCriteria.CRITERIA_IN, ids));
        return query;
    }

    private String getResourceName(SecurityNode next, Map<String, String> connection) {
        return connection.get(SecurityNode.CONNECTION_ASSOCIATION).length() != 0 ? (String) connection
                .get(SecurityNode.CONNECTION_ASSOCIATION) : next.getName();
    }

    private void fixIds(List<String> ids, Iterable<String> idSet) {
        ids.clear();
        for (String id : idSet) {
            ids.add(id);
        }
    }
    
    /**
     * @param pathFinder
     *            the pathFinder to set
     */
    public void setPathFinder(BrutePathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }
    
    /**
     * @param repository
     *            the repository to set
     */
    public void setRepository(Repository<Entity> repository) {
        this.repository = repository;
    }

}
