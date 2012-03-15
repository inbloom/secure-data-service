package org.slc.sli.api.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;
import org.slc.sli.api.security.schema.SchemaDataProvider;
import org.slc.sli.api.service.query.QueryConverter;
import org.slc.sli.api.service.query.SortOrder;
import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.SmartQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of EntityService that can be used for most entities.
 *
 * It is very important this bean prototype scope, since one service is needed per
 * entity/association.
 */
@Scope("prototype")
@Component("basicService")
public class BasicService implements EntityService {

    private static final String ADMIN_SPHERE = "Admin";

    private static final Logger LOG = LoggerFactory.getLogger(BasicService.class);

    private static final int MAX_RESULT_SIZE = 9999;
    
    private String collectionName;
    private List<Treatment> treatments;
    private EntityDefinition defn;

    @Autowired
    private Repository<Entity> repo;

    @Autowired
    private ContextResolverStore contextResolverStore;

    @Autowired
    private SchemaDataProvider provider;

    @Autowired
    private IdConverter idConverter;

    @Autowired
    private QueryConverter queryConverter;

    public BasicService(String collectionName, List<Treatment> treatments) {
        this.collectionName = collectionName;
        this.treatments = treatments;
    }

    @Override
    public String create(EntityBody content) {
        LOG.debug("Creating a new entity in collection {} with content {}", new Object[] { collectionName, content });

        checkRights(determineWriteAccess(content, ""));

        return repo.create(defn.getType(), sanitizeEntityBody(content), collectionName).getEntityId();
    }

    @Override
    public void delete(String id) {
        LOG.debug("KM Deleting {} in {}", new String[] { id, collectionName });

        checkAccess(Right.WRITE_GENERAL, id);

        cascadeDelete(id);

        if (!repo.delete(collectionName, id)) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }

    }

    @Override
    public boolean update(String id, EntityBody content) {
        LOG.debug("Updating {} in {}", new String[] { id, collectionName });

        checkAccess(determineWriteAccess(content, ""), id);

        Entity entity = repo.find(collectionName, id);
        if (entity == null) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }

        EntityBody sanitized = sanitizeEntityBody(content);
        if (entity.getBody().equals(sanitized)) {
            LOG.info("No change detected to {}", id);
            return false;
        }

        LOG.info("new body is {}", sanitized);
        entity.getBody().clear();
        entity.getBody().putAll(sanitized);
        repo.update(collectionName, entity);

        return true;
    }

    @Override
    public EntityBody get(String id) {
        checkAccess(Right.READ_GENERAL, id);
        Entity entity = getRepo().find(collectionName, id);
        if (entity == null) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }
        return makeEntityBody(entity);
    }

    @Override
    public EntityBody get(String id, Map<String, String> queryParameters) {

        checkAccess(Right.READ_GENERAL, id);

        queryParameters.put("_id", id);
        Entity entity = repo.find(collectionName, queryParameters);

        if (entity == null) {
            throw new EntityNotFoundException(id);
        }

        //

        return makeEntityBody(entity);
    }
    
    @Override
    public Iterable<EntityBody> list(Map<String, String> queryParameters) {
        
        checkRights(Right.READ_GENERAL);
        List<String> allowed = findAccessible();
        
        if (allowed.isEmpty()) {
            return noEntitiesFound(queryParameters);
        }
        
        if (queryParameters.containsKey("_id") && (allowed.size() > 0)) {
            Set<String> idList = new HashSet<String>(Arrays.asList(queryParameters.get("_id").split(",")));
            
            Set<String> retainList = new HashSet<String>(allowed);
            retainList.retainAll(idList);
            
            allowed = new ArrayList<String>(retainList);
        }
        
        SmartQuery query = decorateQueryWithAccessibleIds(queryParameters, allowed);
        List<Entity> entities = makeEntityList(repo.findAll(this.collectionName, query));
        
        if (entities.size() == 0) {
            return noEntitiesFound(queryParameters);
        }
        
        List<EntityBody> results = new ArrayList<EntityBody>();
        for (Entity entity : entities) {
            results.add(makeEntityBody(entity));
        }
        return results;
    }
    
    private SmartQuery decorateQueryWithAccessibleIds(Map<String, String> queryParameters, List<String> allowed) {
        SmartQuery query = createQuery(queryParameters);
        if (allowed.size() > 0) {
            query.getFields().put("_id", implode(allowed));
        }
        return query;
    }
    
    private Iterable<EntityBody> noEntitiesFound(Map<String, String> queryParameters) {
        if (makeEntityList(repo.findAll(collectionName, queryParameters)).isEmpty()) {
            return new ArrayList<EntityBody>();
        } else {
            throw new AccessDeniedException("Access to resource denied.");
        }
    }
    
    private String implode(List<String> allowed) {
        
        StringBuffer commaDelimitedString = new StringBuffer(37 * allowed.size());
        int count = 0;
        for (String id : allowed) {
            commaDelimitedString.append(id);
            if (count != allowed.size() - 1)
                commaDelimitedString.append(",");
            count++;
        }
        return commaDelimitedString.toString();
    }

    private List<Entity> makeEntityList(Iterable<Entity> items) {
        List<Entity> myList = new ArrayList<Entity>();
        for (Entity item : items) {
            myList.add(item);
        }
        return myList;
    }
    
    @Override
    public Iterable<EntityBody> get(Iterable<String> ids) {
        return get(ids, null, null);
    }

    @Override
    public Iterable<EntityBody> get(Iterable<String> ids, String sortBy, SortOrder sortOrder) {
        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        checkRights(Right.READ_GENERAL);

        List<String> allowed = findAccessible();

        // Compute intersection of requested and allowed and encode
        Set<Object> binIds = new HashSet<Object>();
        for (String id : ids) {
            if (allowed.contains(id)) {
                binIds.add(idConverter.toDatabaseId(id));
            }
        }

        if (!binIds.isEmpty()) {
            Query query = queryConverter.stringToQuery(collectionName, null, sortBy, sortOrder);
            Iterable<Entity> entities = repo.findByQuery(collectionName, query.addCriteria(Criteria.where("_id").in(binIds)), 0, MAX_RESULT_SIZE);

            List<EntityBody> results = new ArrayList<EntityBody>();
            for (Entity e : entities) {
                results.add(makeEntityBody(e));
            }

            return results;
        }
        return Collections.emptyList();
    }

    @Override
    public Iterable<String> list(int start, int numResults) {
        return list(start, numResults, null, null, null);
    }

    @Override
    public Iterable<String> list(int start, int numResults, String queryString) {
        return list(start, numResults, queryString, null, null);
    }

    @Override
    public Iterable<String> list(int start, int numResults, String queryString, String sortBy, SortOrder sortOrder) {
        checkRights(Right.READ_GENERAL);

        Query query = queryConverter.stringToQuery(defn.getType(), queryString, sortBy, sortOrder);

        List<String> allowed = findAccessible();

        if (allowed.size() > 0) {
            Set<Object> binIds = new HashSet<Object>();
            for (String id : allowed) {
                binIds.add(idConverter.toDatabaseId(id));
            }
            query = query.addCriteria(Criteria.where("_id").in(binIds));

            List<String> results = new ArrayList<String>();
            Iterable<Entity> entities = repo.findByQuery(collectionName, query, start, numResults);

            for (Entity entity : entities) {
                results.add(entity.getEntityId());
            }

            return results;
            
        } else if (allowed.size() == 0) {
            return Collections.emptyList();
            
        } else { // super list logic --> only true when using DefaultEntityContextResolver
            List<String> results = new ArrayList<String>();
            Iterable<Entity> entities = repo.findByQuery(collectionName, query, start, numResults);

            for (Entity entity : entities) {
                results.add(entity.getEntityId());
            }

            return results;
        }
    }

    @Override
    public boolean exists(String id) {
        checkRights(Right.READ_GENERAL);

        boolean exists = false;
        if (repo.find(collectionName, id) != null) {
            exists = true;
        }

        return exists;
    }

    /**
     * given an entity, make the entity body to expose
     *
     * @param entity
     * @return
     */
    private EntityBody makeEntityBody(Entity entity) {
        EntityBody toReturn = new EntityBody(entity.getBody());
        for (Treatment treatment : treatments) {
            toReturn = treatment.toExposed(toReturn, defn, entity.getEntityId());
        }

        // Blank out fields inaccessible to the user
        // @@@ Temporarily comment this out because this apparently would filter out fields even for Educators who should have access
        // @@@ , to unblock teams requiring their users to have access to these fields (03/02/2012)
        // filterFields(toReturn, "");

        return toReturn;
    }

    /**
     * given an entity body that was exposed, return the version with the treatments reversed
     *
     * @param content
     * @return
     */
    private EntityBody sanitizeEntityBody(EntityBody content) {
        EntityBody sanitized = new EntityBody(content);
        for (Treatment treatment : treatments) {
            sanitized = treatment.toStored(sanitized, defn);
        }
        return sanitized;
    }

    /**
     * Deletes any object with a reference to the given sourceId. Assumes that the sourceId
     * still exists so that authorization/context can be checked.
     *
     * @param sourceId ID that was deleted, where anything else with that ID should also be deleted
     */
    private void cascadeDelete(String sourceId) {
        // loop for every EntityDefinition that references the deleted entity's type
        for (EntityDefinition referencingEntity : this.defn.getReferencingEntities()) {
            // loop for every reference field that COULD reference the deleted ID
            for (String referenceField : referencingEntity.getReferenceFieldNames(this.defn.getStoredCollectionName())) {
                EntityService referencingEntityService = referencingEntity.getService();
                Map<String, String> referenceQuery = new HashMap<String, String>();
                referenceQuery.put(referenceField, sourceId);
                try {
                    // list all entities that have the deleted entity's ID in their reference field
                    for (EntityBody entityBody : referencingEntityService.list(referenceQuery)) {
                        String idToBeDeleted = (String) entityBody.get("id");
                        // delete that entity as well
                        referencingEntityService.delete(idToBeDeleted);
                    }
                } catch (AccessDeniedException ade) {
                    LOG.debug("No " + referencingEntity.getResourceName() + " have " + referenceField + " = " + sourceId);
                }
            }
        }
    }

    /**
     * Checks that Actor has the appropriate Rights and linkage to access given entity
     * Also checks for existence of the given entity
     *
     * @param right
     *            needed Right for action
     * @param entityId
     *            id of the entity to access
     * @throws InsufficientAuthenticationException
     *             if authentication is required
     * @throws EntityNotFoundException
     *             if requested entity doesn't exist
     * @throws AccessDeniedException
     *             if actor doesn't have association path to given entity
     */
    private void checkAccess(Right right, String entityId) throws InsufficientAuthenticationException, EntityNotFoundException, AccessDeniedException {

        // Check that user has the needed right
        checkRights(right);

        // Check that target entity actually exists
        if (repo.find(collectionName, entityId) == null) {
            LOG.warn("Could not find {}", entityId);
            throw new EntityNotFoundException(entityId);
        }

        // Check that target entity is accessible to the actor
        if (entityId != null && !findAccessible().contains(entityId)) {
            throw new AccessDeniedException("No association between the user and target entity");
        }

    }

    private void checkRights(Right neededRight) {

        if (ADMIN_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            neededRight = Right.ADMIN_ACCESS;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException("Login Required");
        }

        Collection<GrantedAuthority> auths = auth.getAuthorities();

        if (auths.contains(Right.FULL_ACCESS)) {
            LOG.debug("User has full access");
        } else if (auths.contains(neededRight)) {
            LOG.debug("User has needed right: " + neededRight);
        } else {
            throw new AccessDeniedException("Insufficient Privileges");
        }
    }

    private List<String> findAccessible() {

        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) throw new AccessDeniedException("Principal cannot be found");
        EntityContextResolver resolver = contextResolverStore.findResolver(principal.getEntity().getType(), defn.getType());
        
        return resolver.findAccessible(principal.getEntity());
    }

    /**
     * Removes fields user isn't entitled to see
     *
     * @param eb
     */
    @SuppressWarnings("unchecked")
    private void filterFields(Map<String, Object> eb, String prefix) {

        Collection<GrantedAuthority> auths = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (!auths.contains(Right.FULL_ACCESS)) {

            List<String> toRemove = new LinkedList<String>();
            for (Map.Entry<String, Object> entry : eb.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof Map) {
                    filterFields((Map<String, Object>) value, prefix + "." + fieldName + ".");
                } else {
                    String fieldPath = prefix + fieldName;
                    Right neededRight = provider.getRequiredReadLevel(defn.getType(), fieldPath);
                    LOG.debug("Field {} requires {}", fieldPath, neededRight);

                    if (!auths.contains(neededRight)) {
                        toRemove.add(fieldName);
                    }
                }
            }

            for (String fieldName : toRemove) {
                eb.remove(fieldName);
            }
        }
    }

    /**
     * Figures out if writing to restricted fields
     *
     * @param eb
     *            data currently being passed in
     * @return WRITE_RESTRICTED if restricted fields are being written, WRITE_GENERAL otherwise
     */
    @SuppressWarnings("unchecked")
    private Right determineWriteAccess(Map<String, Object> eb, String prefix) {
        Right toReturn = Right.WRITE_GENERAL;
        if (ADMIN_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            toReturn = Right.ADMIN_ACCESS;
        } else {

            for (Map.Entry<String, Object> entry : eb.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof Map) {
                    filterFields((Map<String, Object>) value, prefix + "." + fieldName + ".");
                } else {
                    String fieldPath = prefix + fieldName;
                    Right neededRight = provider.getRequiredReadLevel(defn.getType(), fieldPath);
                    LOG.debug("Field {} requires {}", fieldPath, neededRight);

                    if (neededRight == Right.WRITE_RESTRICTED) {
                        toReturn = Right.WRITE_RESTRICTED;
                        break;
                    }
                }
            }
        }

        return toReturn;
    }

    /**
     * Set the entity definition for this service.
     * There is a circular dependency between BasicService and EntityDefinition, so they both can't
     * have it be a constructor arg.
     */
    public void setDefn(EntityDefinition defn) {
        this.defn = defn;
    }

    @Override
    public EntityDefinition getEntityDefinition() {
        return defn;
    }

    protected String getCollectionName() {
        return collectionName;
    }

    protected List<Treatment> getTreatments() {
        return treatments;
    }

    protected Repository<Entity> getRepo() {
        return repo;
    }
    
    /**
     * This should have its own class (QueryConverter)
     * TODO refactor
     * 
     * @param queryParameters
     * @return
     */
    protected SmartQuery createQuery(Map<String, String> queryParameters) {
        SmartQuery.SmartQueryBuilder queryBuilder = new SmartQuery.SmartQueryBuilder();
        
        if (queryParameters == null) {
            return queryBuilder.build();
        }
        
        // read each entry in map
        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            String key = entry.getKey();
            
            if (key.equals(ParameterConstants.INCLUDE_FIELDS)) { // specific field(s) to include in result set
                String includeFields = entry.getValue();
                if (includeFields != null) {
                    queryBuilder.setIncludeFields(includeFields);
                }
            } else if (key.equals(ParameterConstants.EXCLUDE_FIELDS)) { // specific field(s) to exclude from result set
                String excludeFields = entry.getValue();
                if (excludeFields != null) {
                    queryBuilder.setExcludeFields(excludeFields);
                }
            } else if (key.equals(ParameterConstants.OFFSET)) { // skip to record X instead of starting at the beginning
                String offset = entry.getValue();
                if (offset != null) {
                    queryBuilder.setOffset(Integer.parseInt(offset));
                }
            } else if (key.equals(ParameterConstants.LIMIT)) { // display X results instead of all of them
                String limit = entry.getValue();
                if (limit != null) {
                    queryBuilder.setLimit(Integer.parseInt(limit));
                }
            } else if (key.equals(ParameterConstants.SORT_BY)) { // sort by a field
                String sortBy = entry.getValue();
                if (sortBy != null) {
                    queryBuilder.setSortBy(sortBy);
                }
            } else if (key.equals(ParameterConstants.SORT_ORDER)) { // define the sort order (ascending or descending)
                String sortOrder = entry.getValue();
                if (sortOrder != null) {
                    SmartQuery.SortOrder order =
                            sortOrder.equals(ParameterConstants.SORT_ASCENDING) ? SmartQuery.SortOrder.ascending : SmartQuery.SortOrder.descending;
                    queryBuilder.setSortOrder(order);
                }
            } else { // query param on record
                String value = entry.getValue();
                if (value != null) {
                    queryBuilder.addField(key, value);
                }
            }
        }
        
        return queryBuilder.build();
    }
}
