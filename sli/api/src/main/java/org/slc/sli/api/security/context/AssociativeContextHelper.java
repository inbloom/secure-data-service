package org.slc.sli.api.security.context;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Resolves Context based permissions.
 * Determines if an associative path exists between a source and target entity.
 */
@Component
public class AssociativeContextHelper {

    @Autowired
    private EntityRepository repository;

    @Autowired
    private EntityDefinitionStore definitions;

    /**
     * Provides list of entity ids that given actor has access to
     *
     * @param principal user currently accessing the system
     * @return List of string ids
     */
    public List<String> findAccessible(Entity principal, List<String> associativeNames) {

        List<AssociationDefinition> associativeContextPath = getDefinitions(associativeNames);

        List<String> ids = new ArrayList<String>(Arrays.asList(principal.getEntityId()));
        String searchType = principal.getType();
        for (AssociationDefinition ad : associativeContextPath) {
            List<String> keys = getAssocKeys(searchType, ad);
            String sourceKey = keys.get(0);
            String targetKey = keys.get(1);
            Query query = new Query(Criteria.where("body." + sourceKey).in(ids));
            query.fields().include(targetKey);
            Iterable<Entity> entities = this.repository.findByQuery(ad.getStoredCollectionName(), query, 0, 9999);

            ids.clear();
            for (Entity e : entities) {
                ids.add((String) e.getBody().get(targetKey));
            }
            searchType = getTargetType(searchType, ad);
        }

        return ids;
    }

    private List<AssociationDefinition> getDefinitions(List<String> associativeNames) {
        List<AssociationDefinition> adl = new ArrayList<AssociationDefinition>();

        for (String name : associativeNames) {
            adl.add((AssociationDefinition) this.definitions.lookupByResourceName(name));
        }

        return adl;
    }

    private String getTargetType(String searchEntityType, AssociationDefinition ad) {
        if (ad.getSourceEntity().getType().equals(searchEntityType)) {
            return ad.getTargetEntity().getType();
        } else if (ad.getTargetEntity().getType().equals(searchEntityType)) {
            return ad.getSourceEntity().getType();
        } else {
            throw new IllegalArgumentException("Entity is not a member of association " + searchEntityType + " " + ad.getType());
        }

    }

    private List<String> getAssocKeys(String entityType, AssociationDefinition ad) {

        if (ad.getSourceEntity().getType().equals(entityType)) {
            return Arrays.asList(ad.getSourceKey(), ad.getTargetKey());
        } else if (ad.getTargetEntity().getType().equals(entityType)) {
            return Arrays.asList(ad.getTargetKey(), ad.getSourceKey());
        } else {
            throw new IllegalArgumentException("Entity is not a member of association " + entityType + " " + ad.getType());
        }
    }

    /**
     * Searches a collection to find entities that contain a reference form a list
     *
     * @param collectionName    collection to query
     * @param referenceLocation location of the reference in the collection (eg "body.referenceId")
     * @param referenceIds      reference values to query
     * @return Ids of entities containing a referenceId at the referenceLocation
     */
    public List<String> findEntitiesContainingReference(String collectionName, String referenceLocation, List<String> referenceIds) {
        Query query = new Query(Criteria.where(referenceLocation).in(referenceIds));
        query.fields().include("_id");
        Iterable<Entity> entities = repository.findByQuery(collectionName, query, 0, 9999);

        List<String> foundIds = new ArrayList<String>();
        for (Entity e : entities) {
            foundIds.add(e.getEntityId());
        }
        return foundIds;
    }
}
