package org.slc.sli.api.security.context;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.api.security.context.traversal.graph.NodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Resolves Context based permissions.
 * Determines if an associative path exists between a source and target entity.
 */
@Component
public class AssociativeContextHelper {

    @Autowired
    private Repository<Entity> repository;

    @Autowired
    private EntityDefinitionStore definitions;

    /**
     * Provides list of entity ids that given actor has access to
     *
     * @param principal user currently accessing the system
     * @return List of string ids
     */
    public List<String> findAccessible(final Entity principal, final List<String> associativeResourceNames) {
        return findAccessible(Arrays.asList(principal.getEntityId()), principal.getType(), associativeResourceNames);
    }

    public List<String> findAccessible(final Collection<String> ids, final String idsEntityName, final List<String> associativeNames) {
        List<AssociationDefinition> associativeContextPath = getDefinitions(associativeNames);

        List<String> foundIds = new ArrayList<String>(ids);
        String searchType = idsEntityName;
        for (AssociationDefinition ad : associativeContextPath) {
            List<String> keys = getAssocKeys(searchType, ad);
            String sourceKey = keys.get(0);
            String targetKey = keys.get(1);
            NeutralQuery neutralQuery = new NeutralQuery();
            neutralQuery.setOffset(0);
            neutralQuery.setLimit(0);
            neutralQuery.addCriteria(new NeutralCriteria(sourceKey, "in", foundIds));
            Iterable<Entity> entities = this.repository.findAll(ad.getStoredCollectionName(), neutralQuery);

            foundIds.clear();
            for (Entity e : entities) {
                foundIds.add((String) e.getBody().get(targetKey));
            }
            searchType = getTargetType(searchType, ad);
        }

        return foundIds;
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

    public List<String> getAssocKeys(String entityType, AssociationDefinition ad) {

        if (ad.getSourceEntity().getType().equals(entityType)
            || ad.getSourceEntity().getStoredCollectionName().equals(entityType)) {
            return Arrays.asList(ad.getSourceKey(), ad.getTargetKey());
        } else if (ad.getTargetEntity().getType().equals(entityType)
            || ad.getTargetEntity().getStoredCollectionName().equals(entityType)) {
            return Arrays.asList(ad.getTargetKey(), ad.getSourceKey());
        } else {
            throw new IllegalArgumentException("Entity is not a member of association " + entityType + " " + ad.getType());
        }
    }

    /**
     *
     * @param collectionName
     * @param referenceLocation
     * @param referenceIds
     * @return
     */
    public List<String> findEntitiesContainingReference(String collectionName, String referenceLocation,
                                                        List<String> referenceIds){
        return  findEntitiesContainingReference(collectionName,referenceLocation,referenceIds,null);
    }

    /**
     * Searches a collection to find entities that contain a reference form a list
     *
     * @param collectionName    collection to query
     * @param referenceLocation location of the reference in the collection (eg "body.referenceId")
     * @param referenceIds      reference values to query
     * @return Ids of entities containing a referenceId at the referenceLocation
     */
    public List<String> findEntitiesContainingReference(String collectionName, String referenceLocation,
                                                        List<String> referenceIds,List<NodeFilter> filterList) {
        List<Entity> entitiesToResolve= new ArrayList<Entity>();
        Iterable<Entity> entityIterableList = getReferenceEntities(collectionName, referenceLocation, referenceIds);
        for (Entity entityInList: entityIterableList){
            entitiesToResolve.add(entityInList);
        }
        entitiesToResolve = filterEntities(entitiesToResolve,filterList,"");

        List<String> foundIds = new ArrayList<String>();
        for (Entity e : entitiesToResolve) {
            foundIds.add(e.getEntityId());
        }
        return foundIds;
    }

    /**
     * Searches a collection to find entities that contain a reference form a list
     *
     * @param collectionName    collection to query
     * @param referenceLocation location of the reference in the collection (eg "body.referenceId")
     * @param referenceIds      reference values to query
     * @return entities containing a referenceId at the referenceLocation
     */
    public Iterable<Entity> getReferenceEntities(String collectionName, String referenceLocation,
            List<String> referenceIds) {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria(referenceLocation, "in", referenceIds));
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(0);
//        BasicService.addDefaultQueryParams(neutralQuery, collectionName);
        Iterable<Entity> entities = repository.findAll(collectionName, neutralQuery);
        return entities;
    }

    /**
     *
     * @param collectionName
     * @param referenceLocation
     * @param returnedReference
     * @param referenceIds
     * @return
     */
    public List<String> findEntitiesContainingReference(String collectionName, String referenceLocation,
                                                        String returnedReference, List<String> referenceIds){
        return  findEntitiesContainingReference(collectionName,referenceLocation,returnedReference,referenceIds,null);
    }
    /**
     * Searches an associative collection to return a list of referenced Ids.
     *
     * @param collectionName
     *            collection to Query
     * @param referenceLocation
     *            the ids to query against
     * @param returnedReference
     *            the field of ids you want to return
     * @param referenceIds
     *            the list of ids to query with
     * @return ids contained in the returnedReference field
     */
    public List<String> findEntitiesContainingReference(String collectionName, String referenceLocation,
            String returnedReference, List<String> referenceIds,List<NodeFilter> filterList) {
        List<Entity> entitiesToResolve= new ArrayList<Entity>();
        Iterable<Entity> entityIterableList = getReferenceEntities(collectionName, referenceLocation, referenceIds);
        for (Entity entityInList: entityIterableList){
            entitiesToResolve.add(entityInList);
        }
        entitiesToResolve = filterEntities(entitiesToResolve,filterList,returnedReference);
        List<String> foundIds = new ArrayList<String>();
        for (Entity e : entitiesToResolve) {
            Map<String, Object> body = e.getBody();
            foundIds.add((String) body.get(returnedReference));
        }
        return foundIds;
    }

    /**
     * Returns a date depending on the grace period
     * @param gracePeriod
     * @return
     */
    public String getFilterDate(String gracePeriod, Calendar calendar) {
        if (gracePeriod != null && !gracePeriod.equals("")) {
            int numDays = Integer.parseInt(gracePeriod) * -1;
            calendar.add(Calendar.DATE, numDays);
        }

        return String.format("%1$tY-%1$tm-%1$td", calendar);
    }

    /**
     *
     * @param entitiList
     * @param filterList
     * @return
     */
    public List<Entity> filterEntities(List<Entity> entitiList , List<NodeFilter> filterList,String referenceField){
        if(filterList != null && entitiList != null && entitiList.size() != 0) {
            for (NodeFilter filter : filterList) {
                entitiList = filter.filterEntities(entitiList,referenceField);
            }
        }
        return entitiList;
    }
}
