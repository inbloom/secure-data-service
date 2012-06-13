/**
 *
 */
package org.slc.sli.api.security.context.resolver;

import java.util.*;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.security.context.traversal.BrutePathFinder;
import org.slc.sli.api.security.context.traversal.cache.SecurityCachingStrategy;
import org.slc.sli.api.security.context.traversal.graph.SecurityNode;
import org.slc.sli.api.security.context.traversal.graph.SecurityNodeConnection;
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
    private ResolveCreatorsEntitiesHelper creatorResolverHelper;

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    private Repository<Entity> repository;

    @Autowired
    private Repository<Entity> repo;

    private String fromEntity;
    private String toEntity;

    @Autowired
    private SecurityCachingStrategy securityCachingStrategy;

    /*
     * @see
     * org.slc.sli.api.security.context.resolver.EntityContextResolver#canResolve(java.lang.String,
     * java.lang.String)
     */
    @Override
    public boolean canResolve(String fromEntityType, String toEntityType) {
        this.fromEntity = fromEntityType;
        this.toEntity = toEntityType;
        if (pathFinder.isPathExcluded(fromEntityType, toEntityType)) {
            return false;
        }
        Set<String> entities = pathFinder.getNodeMap().keySet();
        return (entities.contains(fromEntityType) && entities.contains(toEntityType));
    }

    /*
     * @see
     * org.slc.sli.api.security.context.resolver.EntityContextResolver#findAccessible(org.slc.sli
     * .domain.Entity)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> findAccessible(Entity principal) {

        List<SecurityNode> path = new ArrayList<SecurityNode>();
        path = pathFinder.getPreDefinedPath(fromEntity, toEntity);
        if (path.size() == 0) {
            path = pathFinder.find(fromEntity, toEntity);
        }

        Set<String> ids = new HashSet<String>(Arrays.asList(principal.getEntityId()));
        List<String> previousIdSet = Collections.emptyList();
        SecurityNode current = path.get(0);
        for (int i = 1; i < path.size(); ++i) {
            SecurityNode next = path.get(i);
            SecurityNodeConnection connection = current.getConnectionForEntity(next.getName());
            List<String> idSet = new ArrayList<String>();

            //look up the cache if it exists
            if (securityCachingStrategy.contains(current.getName() + next.getName())) {
                ids.addAll(securityCachingStrategy.retrieve(current.getName() + next.getName()));
                current = path.get(i);
                continue;
            }

            if (connection.isResolver()) {
                idSet = connection.getResolver().findAccessible(principal);
            } else {
                String repoName = getResourceName(current, next, connection);
                debug("Getting Ids From {}", repoName);
                if (connection.isReferenceInSelf()) {
                    NeutralQuery neutralQuery = new NeutralQuery();
                    neutralQuery.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, previousIdSet));
                    Iterable<Entity> entityIterableList = repository.findAll(repoName, neutralQuery);
                    List<Entity> entitiesToResolve = new ArrayList<Entity>();
                    for (Entity entityInList : entityIterableList) {
                        entitiesToResolve.add(entityInList);
                    }
                    entitiesToResolve = helper.filterEntities(entitiesToResolve, connection.getFilter(), "");
                    for (Entity entity : entitiesToResolve) {
                        Object fieldData = entity.getBody().get(connection.getFieldName());
                        if (fieldData != null) {
                            if (fieldData instanceof String) {
                                String id = (String) fieldData;
                                if (!id.isEmpty()) {
                                    idSet.add(id);
                                }
                            } else if (fieldData instanceof ArrayList) {
                                ids.addAll((ArrayList<String>) fieldData);
                            }
                        }
                    }
                } else if (isAssociative(next, connection)) {
                    AssociationDefinition ad = (AssociationDefinition) store.lookupByResourceName(repoName);
                    List<String> keys = new ArrayList<String>();
                    try {
                        keys = helper.getAssocKeys(current.getName(), ad);
                    } catch (IllegalArgumentException e) {
                        keys = helper.getAssocKeys(current.getType(), ad);
                    }
                    idSet = helper.findEntitiesContainingReference(ad.getStoredCollectionName(), keys.get(0),
                            connection.getFieldName(), new ArrayList<String>(ids), connection.getFilter());
                } else if (connection.getAssociationNode().length() != 0) {
                    idSet = helper.findEntitiesContainingReference(repoName, "_id", connection.getFieldName(),
                            new ArrayList<String>(ids), connection.getFilter());

                } else {
                    idSet = helper.findEntitiesContainingReference(repoName, connection.getFieldName(),
                            new ArrayList<String>(ids), connection.getFilter());
                    }
            }

            if (connection.getAggregator() != null) {
                idSet = connection.getAggregator().addAssociatedIds(idSet);
            }

            previousIdSet = idSet;
            ids.addAll(idSet);
            //add the new ids to the cache
            securityCachingStrategy.warm(current.getName() + next.getName(), new HashSet<String>(idSet));

            current = path.get(i);
        }
        debug("We found {} ids", ids);

        //  Allow creator access
        ids.addAll(creatorResolverHelper.getAllowedForCreator(toEntity));
        return new ArrayList<String>(ids);
    }

    private boolean isAssociative(SecurityNode next, SecurityNodeConnection connection) {
        return connection.getAssociationNode().length() != 0 && connection.getAssociationNode().endsWith("ssociations");
    }

    protected String getResourceName(SecurityNode current, SecurityNode next, SecurityNodeConnection connection) {

        String resourceName;

        if (!connection.getAssociationNode().isEmpty())
            resourceName = connection.getAssociationNode();
        else if (connection.isReferenceInSelf())
            resourceName = current.getType();
        else
            resourceName = next.getType();

        return resourceName;
    }

    /**
     * @param pathFinder the pathFinder to set
     */
    public void setPathFinder(BrutePathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    /**
     * @param helper the helper to set
     */
    public void setHelper(AssociativeContextHelper helper) {
        this.helper = helper;
    }

    public void setRepository(Repository<Entity> repo) {
        this.repository = repo;
    }
}
