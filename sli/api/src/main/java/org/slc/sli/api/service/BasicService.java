package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.config.BasicDefinitionStore;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.CallingApplicationInfoProvider;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.resolver.AllowAllEntityContextResolver;
import org.slc.sli.api.security.context.resolver.EdOrgContextResolver;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;
import org.slc.sli.api.security.schema.SchemaDataProvider;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Implementation of EntityService that can be used for most entities.
 *
 * <p/>
 * It is very important this bean prototype scope, since one service is needed per
 * entity/association.
 */
@Scope("prototype")
@Component("basicService")
public class BasicService implements EntityService {

    private static final String ADMIN_SPHERE = "Admin";
    private static final String PUBLIC_SPHERE = "Public";

    private static final int MAX_RESULT_SIZE = 9999;

    private static final String CUSTOM_ENTITY_COLLECTION = "custom_entities";
    private static final String CUSTOM_ENTITY_CLIENT_ID = "clientId";
    private static final String CUSTOM_ENTITY_ENTITY_ID = "entityId";
    private static final String METADATA = "metaData";
    private static final String[] collectionsExcluded = {"tenant" ,"userSession","realm","userAccount","roles","application","applicationAuthorization"};
    private static final Set<String> NOT_BY_TENANT = new HashSet<String>(Arrays.asList(collectionsExcluded));

    private String collectionName;
    private List<Treatment> treatments;
    private EntityDefinition defn;

    private Right readRight;
    private Right writeRight; // this is possibly the worst named variable ever

    @Autowired
    private Repository<Entity> repo;

    @Autowired
    private ContextResolverStore contextResolverStore;

    @Autowired
    private SchemaDataProvider provider;

    @Autowired
    private IdConverter idConverter;

    @Autowired
    private CallingApplicationInfoProvider clientInfo;

    @Autowired
    private EdOrgContextResolver edOrgContextResolver;
    
    @Autowired
    private BasicDefinitionStore definitionStore;
    
    public BasicService(String collectionName, List<Treatment> treatments, Right readRight, Right writeRight) {
        this.collectionName = collectionName;
        this.treatments = treatments;
        this.readRight = readRight;
        this.writeRight = writeRight;
    }

    public BasicService(String collectionName, List<Treatment> treatments) {
        this(collectionName, treatments, Right.READ_GENERAL, Right.WRITE_GENERAL);
    }

    @Override
    public long count(NeutralQuery neutralQuery) {
        checkRights(readRight);
        checkFieldAccess(neutralQuery);


        NeutralCriteria securityCriteria = findAccessible(defn.getType());
        List<String> allowed = (List<String>) securityCriteria.getValue();
        
        if (allowed.isEmpty() && readRight != Right.ANONYMOUS_ACCESS) {
            return 0;
        }

        Set<String> ids = new HashSet<String>();
        List<NeutralCriteria> criterias = neutralQuery.getCriteria();
        for (NeutralCriteria criteria : criterias) {
            if (criteria.getKey().equals("_id")) {
                @SuppressWarnings("unchecked")
                List<String> idList = (List<String>) criteria.getValue();
                ids.addAll(idList);
            }
        }
        NeutralQuery localNeutralQuery = new NeutralQuery(neutralQuery);

        if (securityCriteria.getKey().equals("_id")) {
            if (allowed.size() >= 0 && readRight != Right.ANONYMOUS_ACCESS) {
                if (!ids.isEmpty()) {
                    ids.retainAll(new HashSet<String>(allowed)); // retain only those IDs that area
                                                                 // allowed
                    //update the security criteria
                    securityCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, new ArrayList<String>(ids));
                }
            }
        }

        if (allowed.size() > 0) {
            //add the security criteria
            localNeutralQuery.addCriteria(securityCriteria);
        }
        this.addDefaultQueryParams(localNeutralQuery, collectionName);
        return repo.count(collectionName, localNeutralQuery);
    }

    /**
     * Retrieves an entity from the data store with certain fields added/removed.
     *
     * @param neutralQuery all parameters to be included in query
     * @return the body of the entity
     */
    @Override
    public Iterable<String> listIds(NeutralQuery neutralQuery) {
        checkRights(readRight);
        checkFieldAccess(neutralQuery);

        NeutralCriteria securityCriteria = findAccessible(defn.getType());
        List<String> allowed = (List<String>) securityCriteria.getValue();
        
        if (allowed.isEmpty()) {
            return Collections.emptyList();
        }

        // super list logic --> only true when using DefaultEntityContextResolver
        List<String> results = new ArrayList<String>();

        //not a super list so add the criteria
        //not sure if this ever happen
        if (allowed.size() > 0) {
            //add the security criteria
            neutralQuery.addCriteria(securityCriteria);
        }
        this.addDefaultQueryParams(neutralQuery, collectionName);
        Iterable<Entity> entities = repo.findAll(collectionName, neutralQuery);

        for (Entity entity : entities) {
            results.add(entity.getEntityId());
        }

        return results;
    }

    @Override
    public String create(EntityBody content) {
        // DE260 - Logging of possibly sensitive data
        // LOG.debug("Creating a new entity in collection {} with content {}", new Object[] {
        // collectionName, content });

        // if service does not allow anonymous write access, check user rights
        if (writeRight != Right.ANONYMOUS_ACCESS) {
            checkRights(determineWriteAccess(content, ""));
        }

        checkReferences(content);
        
        return repo.create(defn.getType(), sanitizeEntityBody(content), createMetadata(), collectionName).getEntityId();
    }

    @Override
    public void delete(String id) {
        // DE260 - Logging of possibly sensitive data
        // LOG.debug("Deleting {} in {}", new String[] { id, collectionName });

        checkAccess(writeRight, id);

        try {
            cascadeDelete(id);
        } catch (RuntimeException re) {
            debug(re.toString());
        }

        if (!repo.delete(collectionName, id)) {
            info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }
        deleteAttachedCustomEntities(id);
    }

    @Override
    public boolean update(String id, EntityBody content) {
        debug("Updating {} in {}", id, collectionName);

        if (writeRight != Right.ANONYMOUS_ACCESS) {
            checkAccess(determineWriteAccess(content, ""), id);
        }


        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));
        this.addDefaultQueryParams(query, collectionName);
        Entity entity = repo.findOne(collectionName, query);
        //Entity entity = repo.findById(collectionName, id);
        if (entity == null) {
            info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }

        EntityBody sanitized = sanitizeEntityBody(content);
        if (entity.getBody().equals(sanitized)) {
            info("No change detected to {}", id);
            return false;
        }
        
        checkReferences(content);

        info("new body is {}", sanitized);
        entity.getBody().clear();
        entity.getBody().putAll(sanitized);
        repo.update(collectionName, entity);

        return true;
    }

    @Override
    public EntityBody get(String id) {
        checkAccess(readRight, id);
        // change to accommodate tenantId:
        // findById does not support NeutralQuery and therefore cannot be used any more
        // Entity entity = getRepo().findById(collectionName, id);
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("_id", "=", id));
        this.addDefaultQueryParams(neutralQuery, collectionName);

        Entity entity = getRepo().findOne(collectionName, neutralQuery);

        if (entity == null) {
            info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }
        return makeEntityBody(entity);
    }

    @Override
    public EntityBody get(String id, NeutralQuery neutralQuery) {
        checkAccess(readRight, id);
        checkFieldAccess(neutralQuery);

        if (neutralQuery == null) {
            neutralQuery = new NeutralQuery();
        }
        neutralQuery.addCriteria(new NeutralCriteria("_id", "=", id));
        this.addDefaultQueryParams(neutralQuery, collectionName);

        Entity entity = repo.findOne(collectionName, neutralQuery);

        if (entity == null) {
            throw new EntityNotFoundException(id);
        }

        return makeEntityBody(entity);
    }

    /**
     * The purpose of this method is to add the default parameters to a neutral query. At inception,
     * this method
     * add the Tenant ID to a neutral query.
     *
     * @param query
     *            The query returned is the same as the query passed.
     * @return
     *         The modified neutral query
     */
    protected NeutralQuery addDefaultQueryParams(NeutralQuery query, String collectionName) {
        if (query == null) {
            query = new NeutralQuery();
        }

        // Add tenant ID
        if (!NOT_BY_TENANT.contains(collectionName)) {
            SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal();
            if(principal == null || principal.getTenantId() == null) {
                debug("A user is attempting to access collection: " + collectionName + "with null tenantId." );
                return query;
            }
            // make sure a criterion for tenantId has not already been added to this query
            boolean addCrit = true;
            List<NeutralCriteria> criteria = query.getCriteria();
            if (criteria != null) {
                ListIterator<NeutralCriteria> li = criteria.listIterator();
                while (li.hasNext()) {
                    if ("metaData.tenantId".equalsIgnoreCase(li.next().getKey())) {
                        addCrit = false;
                        break;
                    }
                }
            }
            // add the tenant ID if it's not already there
            if (addCrit) {
                query.addCriteria(new NeutralCriteria("metaData.tenantId", "=", principal.getTenantId(), false));
            }
        }
        return query;
    }

    private Iterable<EntityBody> noEntitiesFound(NeutralQuery neutralQuery) {
        //this.addDefaultQueryParams(neutralQuery, collectionName);
        if (makeEntityList(repo.findAll(collectionName, neutralQuery)).isEmpty()) {
            return new ArrayList<EntityBody>();
        } else {
            throw new AccessDeniedException("Access to resource denied.");
        }
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

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(MAX_RESULT_SIZE);
        this.addDefaultQueryParams(neutralQuery, collectionName);


        return get(ids, neutralQuery);
    }

    @Override
    public Iterable<EntityBody> get(Iterable<String> ids, NeutralQuery neutralQuery) {
        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        checkRights(readRight);
        checkFieldAccess(neutralQuery);


        NeutralCriteria securityCriteria = findAccessible(defn.getType());
        List<String> allowed = (List<String>) securityCriteria.getValue();

        List<String> idList = new ArrayList<String>();

        for (String id : ids) {
            idList.add(id);
        }

        if (!idList.isEmpty()) {
            if (neutralQuery == null) {
                neutralQuery = new NeutralQuery();
                neutralQuery.setOffset(0);
                neutralQuery.setLimit(MAX_RESULT_SIZE);
            }

            if (allowed.size() > 0) {
                //add the security criteria
                neutralQuery.addCriteria(securityCriteria);
            }

            //add the ids requested
            neutralQuery.addCriteria(new NeutralCriteria("_id", "in", idList));
            this.addDefaultQueryParams(neutralQuery, collectionName);


            Iterable<Entity> entities = repo.findAll(collectionName, neutralQuery);

            List<EntityBody> results = new ArrayList<EntityBody>();
            for (Entity e : entities) {
                results.add(makeEntityBody(e));
            }

            return results;
        }

        return Collections.emptyList();
    }

    @Override
    public Iterable<EntityBody> list(NeutralQuery neutralQuery) {
        checkRights(readRight);
        checkFieldAccess(neutralQuery);

        NeutralCriteria securityCriteria = findAccessible(defn.getType());
        List<String> allowed = (List<String>) securityCriteria.getValue();

        NeutralQuery localNeutralQuery = new NeutralQuery(neutralQuery);

        if (readRight == Right.ANONYMOUS_ACCESS) {
            debug("super list logic --> {} service allows anonymous access", collectionName);
        } else if (allowed.isEmpty()) {
            return Collections.emptyList();
        } else if (allowed.size() < 0) {
            debug("super list logic --> only true when using DefaultEntityContextResolver");
        } else {
            if (securityCriteria.getKey().equals("_id")) {
                Set<String> ids = new HashSet<String>();
                List<NeutralCriteria> criterias = neutralQuery.getCriteria();
                for (NeutralCriteria criteria : criterias) {
                    if (criteria.getKey().equals("_id")) {
                        @SuppressWarnings("unchecked")
                        List<String> idList = (List<String>) criteria.getValue();
                        ids.addAll(idList);
                    }
                }

                if (!ids.isEmpty()) {
                    Set<String> allowedSet = new HashSet<String>(allowed);
                    ids.retainAll(allowedSet);

                    //update the security criteria to only include the needed ids
                    securityCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, new ArrayList<String>(ids));
                }
            }
        }

        if (allowed.size() > 0) {
            //add the security criteria
            localNeutralQuery.addCriteria(securityCriteria);
        }
            
        List<EntityBody> results = new ArrayList<EntityBody>();
        this.addDefaultQueryParams(localNeutralQuery, collectionName);

        Collection<Entity> entities = (Collection<Entity>) repo.findAll(collectionName, localNeutralQuery);
        for (Entity entity : entities) {
            results.add(makeEntityBody(entity));
        }

        if (results.isEmpty()) {
            return noEntitiesFound(neutralQuery);
        }

        return results;
    }

    @Override
    public boolean exists(String id) {
        checkRights(readRight);

        boolean exists = false;
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("_id", "=", id));
        this.addDefaultQueryParams(query, collectionName);
        
        Iterable<Entity> entities = repo.findAll(collectionName, query);

        if (entities != null && entities.iterator().hasNext()) {
            exists = true;
        }

        return exists;
    }

    /**
     * TODO: refactor clientId, entityId out of body into root of mongo document
     * TODO: entity collection should be per application
     */
    @Override
    public EntityBody getCustom(String id) {
        checkAccess(readRight, id);

        String clientId = getClientId();


        debug("Reading custom entity: entity={}, entityId={}, clientId={}", new String[] {
                getEntityDefinition().getType(), id, clientId });

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_CLIENT_ID, "=", clientId, false));
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_ENTITY_ID, "=", id, false));
        this.addDefaultQueryParams(query, collectionName);


        Entity entity = getRepo().findOne(CUSTOM_ENTITY_COLLECTION, query);
        if (entity != null) {
            EntityBody clonedBody = new EntityBody(entity.getBody());
            return clonedBody;
        } else {
            return null;
        }
    }

    /**
     * TODO: refactor clientId, entityId out of body into root of mongo document
     * TODO: entity collection should be per application
     */
    @Override
    public void deleteCustom(String id) {
        checkAccess(writeRight, id);

        String clientId = getClientId();

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_CLIENT_ID, "=", clientId, false));
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_ENTITY_ID, "=", id, false));
        this.addDefaultQueryParams(query, collectionName);
        
        Entity entity = getRepo().findOne(CUSTOM_ENTITY_COLLECTION, query);

        if (entity == null) {
            throw new EntityNotFoundException(id);
        }

        boolean deleted = getRepo().delete(CUSTOM_ENTITY_COLLECTION, entity.getEntityId());

        debug("Deleting custom entity: entity={}, entityId={}, clientId={}, deleted?={}", new String[] {
                getEntityDefinition().getType(), id, clientId, String.valueOf(deleted) });
    }

    /**
     * TODO: refactor clientId, entityId out of body into root of mongo document
     * TODO: entity collection should be per application
     */
    @Override
    public void createOrUpdateCustom(String id, EntityBody customEntity) {
        checkAccess(writeRight, id);

        String clientId = getClientId();

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_CLIENT_ID, "=", clientId, false));
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_ENTITY_ID, "=", id, false));
        this.addDefaultQueryParams(query, collectionName);
        
        Entity entity = getRepo().findOne(CUSTOM_ENTITY_COLLECTION, query);

        if (entity != null && entity.getBody().equals(customEntity)) {
            debug("No change detected to custom entity, ignoring update: entity={}, entityId={}, clientId={}",
                    new String[]{ getEntityDefinition().getType(), id, clientId });
            return;
        }

        EntityBody clonedEntity = new EntityBody(customEntity);

        if (entity != null) {
            debug("Overwriting existing custom entity: entity={}, entityId={}, clientId={}", new String[]{
                    getEntityDefinition().getType(), id, clientId });
            entity.getBody().clear();
            entity.getBody().putAll(clonedEntity);
            getRepo().update(CUSTOM_ENTITY_COLLECTION, entity);
        } else {
            debug("Creating new custom entity: entity={}, entityId={}, clientId={}", new String[]{
                    getEntityDefinition().getType(), id, clientId });
            EntityBody metaData = new EntityBody();
            
            Map<String, Object> metadata = new HashMap<String, Object>();
            SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            metaData.put(CUSTOM_ENTITY_CLIENT_ID, clientId);
            metaData.put(CUSTOM_ENTITY_ENTITY_ID, id);
            metaData.put("tenantId", principal.getTenantId());
            getRepo().create(CUSTOM_ENTITY_COLLECTION, clonedEntity, metaData, CUSTOM_ENTITY_COLLECTION);
        }
    }
    
    private void checkReferences(EntityBody eb) {
        for (Map.Entry<String, Object> entry : eb.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                continue;
            }
            
            String fieldPath = fieldName;
            String entityType = provider.getReferencingEntity(defn.getType(), fieldPath);
            if (entityType != null) {
                debug("Field {} is referencing {}", fieldPath, entityType);
                String collectionName = definitionStore.lookupByEntityType(entityType).getStoredCollectionName();
                entityType = collectionName;
                SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication()
                        .getPrincipal();
                EntityContextResolver resolver = contextResolverStore.findResolver(principal.getEntity().getType(),
                        collectionName);
                List<String> accessible = resolver.findAccessible(principal.getEntity());

                if (!principal.getEntity().getType().equals(EntityNames.STAFF)) {
                    if (value instanceof List) {
                        List<String> valuesList = (List<String>) value;
                        for (String cur : valuesList) {
                            if (!accessible.contains(cur) && repo.findById(collectionName, cur) != null) {
                                debug("{} in {} is not accessible", value, collectionName);
                                throw new AccessDeniedException(
                                        "Cannot create an association to an entity you don't have access to");
                            }
                        }
                    } else {
                        if (value != null && !accessible.contains(value) && repo.findById(collectionName, (String) value) != null) {
                            debug("{} in {} is not accessible", value, collectionName);
                            throw new AccessDeniedException(
                                    "Cannot create an association to an entity you don't have access to");
                        }
                    }
                } else {
                    //need to refactor this again when we have time
                    //working on a P1 on the due date is not the time to do it
                    if (value instanceof List) {
                        List<String> valuesList = (List<String>) value;
                        for (String cur : valuesList) {
                            if (!isEntityAllowed(cur, collectionName, entityType) && repo.findById(collectionName, cur) != null) {
                                debug("{} in {} is not accessible", value, collectionName);
                                throw new AccessDeniedException(
                                        "Cannot create an association to an entity you don't have access to");
                            }
                        }
                    } else {
                        if (!isEntityAllowed((String) value, collectionName, entityType) && repo.findById(collectionName, (String) value) != null) {
                            debug("{} in {} is not accessible", value, collectionName);
                            throw new AccessDeniedException(
                                    "Cannot create an association to an entity you don't have access to");
                        }
                    }
                }
                
            }
        }
    }
        
    
    private String getClientId() {
        String clientId = clientInfo.getClientId();
        if (clientId == null) {
            throw new AccessDeniedException("No Application Id");
        }
        return clientId;
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
            toReturn = treatment.toExposed(toReturn, defn, entity);
        }

        if (readRight != Right.ANONYMOUS_ACCESS) {
            // Blank out fields inaccessible to the user
            filterFields(toReturn);
        }

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
        for (EntityDefinition referencingEntity : defn.getReferencingEntities()) {
            // loop for every reference field that COULD reference the deleted ID
            for (String referenceField : referencingEntity.getReferenceFieldNames(defn.getStoredCollectionName())) {
                EntityService referencingEntityService = referencingEntity.getService();
                NeutralQuery neutralQuery = new NeutralQuery();
                neutralQuery.addCriteria(new NeutralCriteria(referenceField + "=" + sourceId));
                this.addDefaultQueryParams(neutralQuery, collectionName);
                try {
                    // list all entities that have the deleted entity's ID in their reference field
                    for (EntityBody entityBody : referencingEntityService.list(neutralQuery)) {
                        String idToBeDeleted = (String) entityBody.get("id");
                        // delete that entity as well
                        referencingEntityService.delete(idToBeDeleted);
                        // delete custom entities attached to this entity
                        deleteAttachedCustomEntities(idToBeDeleted);
                    }
                } catch (AccessDeniedException ade) {
                    debug("No {} have {}={}", new Object[]{ referencingEntity.getResourceName(), referenceField,
                            sourceId });
                }
            }
        }
    }

    private void deleteAttachedCustomEntities(String sourceId) {
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_ENTITY_ID, "=", sourceId, false));
        this.addDefaultQueryParams(query, collectionName);
        Iterable<String> ids = getRepo().findAllIds(CUSTOM_ENTITY_COLLECTION, query);
        for (String id : ids) {
            getRepo().delete(CUSTOM_ENTITY_COLLECTION, id);
        }
    }

    /**
     * Checks that Actor has the appropriate Rights and linkage to access given entity
     * Also checks for existence of the given entity
     *
     * @param right    needed Right for action
     * @param entityId id of the entity to access
     * @throws EntityNotFoundException if requested entity doesn't exist
     * @throws AccessDeniedException   if actor doesn't have association path to given entity
     */
    private void checkAccess(Right right, String entityId) {

        // Check that user has the needed right
        checkRights(right);

        // Check that target entity actually exists
        if (repo.findById(collectionName, entityId) == null) {
            warn("Could not find {}", entityId);
            throw new EntityNotFoundException(entityId);
        }

        if (right != Right.ANONYMOUS_ACCESS) {
            // Check that target entity is accessible to the actor
            if (entityId != null && !isEntityAllowed(entityId, collectionName, defn.getType())) {
                throw new AccessDeniedException("No association between the user and target entity");
            }
        }
    }


    /**
     * Checks to see if the entity id is allowed by security
     * @param entityId The id to check
     * @return
     */
    private boolean isEntityAllowed(String entityId, String collectionName, String toType) {
        NeutralCriteria securityCriteria = findAccessible(toType);
        List<String> allowed = (List<String>) securityCriteria.getValue();

        if (securityCriteria.getKey().equals("_id")) {
            return allowed.contains(entityId);
        } else {
            NeutralQuery query = new NeutralQuery();

            //account for super list
            if (allowed.size() > 0) {
                query.addCriteria(securityCriteria);
            }
            query.addCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(entityId)));
            this.addDefaultQueryParams(query, collectionName);
            Entity entity = repo.findOne(collectionName, query);

            return (entity != null);
        }
    }
    
    private void checkRights(Right neededRight) {

        // anonymous access is always granted
        if (neededRight == Right.ANONYMOUS_ACCESS) {
            return;
        }

        if (ADMIN_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            neededRight = Right.ADMIN_ACCESS;
        }

        if (PUBLIC_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            if (Right.READ_GENERAL.equals(neededRight)) {
                neededRight = Right.READ_PUBLIC;
            }
        }

        Collection<GrantedAuthority> auths = getAuths();

        if (auths.contains(Right.FULL_ACCESS)) {
            debug("User has full access");
        } else if (auths.contains(neededRight)) {
            debug("User has needed right: {}", neededRight);
        } else {
            throw new AccessDeniedException("Insufficient Privileges");
        }
    }

    private Collection<GrantedAuthority> getAuths() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (readRight != Right.ANONYMOUS_ACCESS) {
            SecurityUtil.ensureAuthenticated();
        }
        return auth.getAuthorities();
    }

    private NeutralCriteria findAccessible(String toType) {

        String securityField = "_id";
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal == null) {
            throw new AccessDeniedException("Principal cannot be found");
        }

        Entity entity = principal.getEntity();
        String type = entity != null ? entity.getType() : null;   // null for super admins because
        // they don't contain mongo
        // entries

        if (isPublic()) {
            return new NeutralCriteria(securityField, NeutralCriteria.CRITERIA_IN, AllowAllEntityContextResolver.SUPER_LIST);
        }

        EntityContextResolver resolver = contextResolverStore.findResolver(type, toType);
        List<String> allowed = resolver.findAccessible(principal.getEntity());

        if (type != null && type.equals(EntityNames.STAFF) &&
            !((toType.equals(EntityNames.SCHOOL)) || (toType.equals(EntityNames.EDUCATION_ORGANIZATION)))) {
            securityField = "metaData.edOrgs";
        }

        NeutralCriteria securityCriteria = new NeutralCriteria(securityField, NeutralCriteria.CRITERIA_IN, allowed, false);

        return securityCriteria;
    }

    private boolean isPublic() {
        if (getAuths().contains(Right.FULL_ACCESS)) {
            return getAuths().contains(Right.FULL_ACCESS);
        } else {
            return (defn.getType().equals(EntityNames.LEARNINGOBJECTIVE) || defn.getType().equals(
                    EntityNames.LEARNINGSTANDARD))
                    || defn.getType().equals(EntityNames.EDUCATION_ORGANIZATION);
        }
    }


    /**
     * Removes fields user isn't entitled to see
     *
     * @param eb
     */
    @SuppressWarnings("unchecked")
    private void filterFields(Map<String, Object> eb) {
        filterFields(eb, "");
        complexFilter(eb);
    }

    private void complexFilter(Map<String, Object> eb) {
        Collection<GrantedAuthority> auths = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (!auths.contains(Right.READ_RESTRICTED) && defn.getType().equals(EntityNames.STAFF)) {
            final String work = "Work";
            final String telephoneNumberType = "telephoneNumberType";
            final String emailAddressType = "emailAddressType";
            final String telephone = "telephone";
            final String electronicMail = "electronicMail";

            List<Map<String, Object>> telephones = (List<Map<String, Object>>) eb.get(telephone);
            if (telephones != null) {

                for (Iterator<Map<String, Object>> it = telephones.iterator(); it.hasNext(); ) {
                    if (!work.equals(it.next().get(telephoneNumberType))) {
                        it.remove();
                    }
                }

            }

            List<Map<String, Object>> emails = (List<Map<String, Object>>) eb.get(electronicMail);
            if (emails != null) {

                for (Iterator<Map<String, Object>> it = emails.iterator(); it.hasNext(); ) {
                    if (!work.equals(it.next().get(emailAddressType))) {
                        it.remove();
                    }
                }

            }

        }
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

                String fieldPath = prefix + fieldName;
                Right neededRight = getNeededRight(fieldPath);

                debug("Field {} requires {}", fieldPath, neededRight);
                SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication()
                        .getPrincipal();
                if (!auths.contains(neededRight) && !principal.getEntity().getEntityId().equals(eb.get("id"))) {
                    toRemove.add(fieldName);
                } else if (value instanceof Map) {
                    filterFields((Map<String, Object>) value, prefix + "." + fieldName + ".");
                }
            }

            for (String fieldName : toRemove) {
                eb.remove(fieldName);
            }
        }
    }

    /**
     * Returns the needed right for a field by examining the schema
     *
     * @param fieldPath The field name
     * @return
     */
    protected Right getNeededRight(String fieldPath) {
        Right neededRight = provider.getRequiredReadLevel(defn.getType(), fieldPath);

        if (ADMIN_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            neededRight = Right.ADMIN_ACCESS;
        }

        if (PUBLIC_SPHERE.equals(provider.getDataSphere(defn.getType()))) {
            if (Right.READ_GENERAL.equals(neededRight)) {
                neededRight = Right.READ_PUBLIC;
            }
        }

        return neededRight;
    }

    /**
     * Checks query params for access restrictions
     *
     * @param query The query to check
     */
    protected void checkFieldAccess(NeutralQuery query) {

        if (query != null) {
            // get the authorities
            Collection<GrantedAuthority> auths = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities();

            if (!auths.contains(Right.FULL_ACCESS) && !auths.contains(Right.ANONYMOUS_ACCESS)) {
                for (NeutralCriteria criteria : query.getCriteria()) {
                    // get the needed right for the field
                    Right neededRight = getNeededRight(criteria.getKey());

                    if (!auths.contains(neededRight)) {
                        throw new QueryParseException("Cannot search on restricted field", criteria.getKey());
                    }
                }
            }
        }
    }

    /**
     * Figures out if writing to restricted fields
     *
     * @param eb data currently being passed in
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
                    debug("Field {} requires {}", fieldPath, neededRight);

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
     * Creates the metaData HashMap to be added to the entity created in mongo.
     *
     * @return Map containing important metadata for the created entity.
     */
    private Map<String, Object> createMetadata() {
        Map<String, Object> metadata = new HashMap<String, Object>();
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String createdBy = principal.getEntity().getEntityId();
        if (createdBy != null && createdBy.equals("-133")) {
            createdBy = principal.getExternalId();
        }
        metadata.put("isOrphaned", "true");
        metadata.put("createdBy", createdBy);
        metadata.put("tenantId", principal.getTenantId());
        //add the edorgs for staff
        createEdOrgMetaDataForStaff(principal, metadata);

        return metadata;
    }


    /**
     * Add the list of ed orgs a principal entity can see
     * Needed for staff security
     * @param principal
     * @param metaData
     */
    //DE-719 - need to check this one
    private void createEdOrgMetaDataForStaff(SLIPrincipal principal, Map<String, Object> metaData) {
        //get all the edorgs this principal can see
        List<String> edOrgIds = edOrgContextResolver.findAccessible(principal.getEntity());

        if (!edOrgIds.isEmpty()) {
            debug("Updating metadData with edOrg ids");
            metaData.put("edOrgs", edOrgIds);
        }
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

    protected void setClientInfo(CallingApplicationInfoProvider clientInfo) {
        this.clientInfo = clientInfo;
    }
}
