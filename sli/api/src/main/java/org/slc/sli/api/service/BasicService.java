package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.CallingApplicationInfoProvider;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.resolver.AllowAllEntityContextResolver;
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
        
        List<String> allowed = findAccessible();
        
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
        
        if (allowed.size() >= 0 && readRight != Right.ANONYMOUS_ACCESS) {
            if (ids.isEmpty()) {
                localNeutralQuery.addCriteria(new NeutralCriteria("_id", "in", allowed));
            } else {
                ids.retainAll(new HashSet<String>(allowed)); // retain only those IDs that area
                                                             // allowed
                localNeutralQuery.addCriteria(new NeutralCriteria("_id", "in", new ArrayList<String>(ids)));
            }
        }
        
        return repo.count(collectionName, localNeutralQuery);
    }
    
    /**
     * Retrieves an entity from the data store with certain fields added/removed.
     * 
     * @param neutralQuery
     *            all parameters to be included in query
     * @return the body of the entity
     */
    @Override
    public Iterable<String> listIds(NeutralQuery neutralQuery) {
        checkRights(readRight);
        checkFieldAccess(neutralQuery);
        
        List<String> allowed = findAccessible();
        
        if (allowed.isEmpty()) {
            return Collections.emptyList();
        }
        
        // super list logic --> only true when using DefaultEntityContextResolver
        List<String> results = new ArrayList<String>();
        Iterable<Entity> entities = repo.findAll(collectionName, neutralQuery);
        
        for (Entity entity : entities) {
            if (allowed.contains(entity.getEntityId())) {
                results.add(entity.getEntityId());
            }
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
        
        Entity entity = repo.findById(collectionName, id);
        if (entity == null) {
            info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }
        
        EntityBody sanitized = sanitizeEntityBody(content);
        if (entity.getBody().equals(sanitized)) {
            info("No change detected to {}", id);
            return false;
        }
        
        info("new body is {}", sanitized);
        entity.getBody().clear();
        entity.getBody().putAll(sanitized);
        repo.update(collectionName, entity);
        
        return true;
    }
    
    @Override
    public EntityBody get(String id) {
        checkAccess(readRight, id);
        Entity entity = getRepo().findById(collectionName, id);
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
        
        Entity entity = repo.findOne(collectionName, neutralQuery);
        
        if (entity == null) {
            throw new EntityNotFoundException(id);
        }
        
        //
        
        return makeEntityBody(entity);
    }
    
    private Iterable<EntityBody> noEntitiesFound(NeutralQuery neutralQuery) {
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
        
        return get(ids, neutralQuery);
    }
    
    @Override
    public Iterable<EntityBody> get(Iterable<String> ids, NeutralQuery neutralQuery) {
        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }
        
        checkRights(readRight);
        checkFieldAccess(neutralQuery);
        
        List<String> allowed = findAccessible();
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
                neutralQuery.addCriteria(new NeutralCriteria("_id", "in", allowed));
            }
            
            neutralQuery.addCriteria(new NeutralCriteria("_id", "in", idList));
            
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
        
        List<String> allowed = findAccessible();
        NeutralQuery localNeutralQuery = new NeutralQuery(neutralQuery);
        
        if (readRight == Right.ANONYMOUS_ACCESS) {
            debug("super list logic --> {} service allows anonymous access", collectionName);
        } else if (allowed.isEmpty()) {
            return Collections.emptyList();
        } else if (allowed.size() < 0) {
            debug("super list logic --> only true when using DefaultEntityContextResolver");
        } else {
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
                
                List<String> finalIds = new ArrayList<String>(ids);
                localNeutralQuery.addCriteria(new NeutralCriteria("_id", "in", finalIds));
            } else {
                localNeutralQuery.addCriteria(new NeutralCriteria("_id", "in", allowed));
            }
        }
        
        List<EntityBody> results = new ArrayList<EntityBody>();
        
        for (Entity entity : repo.findAll(collectionName, localNeutralQuery)) {
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
        if (repo.findById(collectionName, id) != null) {
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
        
        Entity entity = getRepo().findOne(CUSTOM_ENTITY_COLLECTION, query);
        
        if (entity != null && entity.getBody().equals(customEntity)) {
            debug("No change detected to custom entity, ignoring update: entity={}, entityId={}, clientId={}",
                    new String[] { getEntityDefinition().getType(), id, clientId });
            return;
        }
        
        EntityBody clonedEntity = new EntityBody(customEntity);
        
        if (entity != null) {
            debug("Overwriting existing custom entity: entity={}, entityId={}, clientId={}", new String[] {
                    getEntityDefinition().getType(), id, clientId });
            entity.getBody().clear();
            entity.getBody().putAll(clonedEntity);
            getRepo().update(CUSTOM_ENTITY_COLLECTION, entity);
        } else {
            debug("Creating new custom entity: entity={}, entityId={}, clientId={}", new String[] {
                    getEntityDefinition().getType(), id, clientId });
            EntityBody metaData = new EntityBody();
            metaData.put(CUSTOM_ENTITY_CLIENT_ID, clientId);
            metaData.put(CUSTOM_ENTITY_ENTITY_ID, id);
            getRepo().create(CUSTOM_ENTITY_COLLECTION, clonedEntity, metaData, CUSTOM_ENTITY_COLLECTION);
        }
    }
    
    private void checkReferences(EntityBody eb) {
        for (Map.Entry<String, Object> entry : eb.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            
            String fieldPath = fieldName;
            String entityType = provider.getReferencingEntity(defn.getType(), fieldPath);
            if (entityType != null) {
                debug("Field {} is referencing {}", fieldPath, entityType);
                SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication()
                        .getPrincipal();
                EntityContextResolver resolver = contextResolverStore.findResolver(principal.getEntity().getType(),
                        entityType);
                List<String> accessible = resolver.findAccessible(principal.getEntity());
                if (!accessible.contains(value)) {
                    debug("{} in {} is not accessible", value, entityType);
                    throw new AccessDeniedException(
                            "Cannot create an association to an entity you don't have access to");
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
        
        toReturn.put(METADATA, entity.getMetaData());
        
        for (Treatment treatment : treatments) {
            toReturn = treatment.toExposed(toReturn, defn, entity.getEntityId());
        }
        
        if (readRight != Right.ANONYMOUS_ACCESS) {
            // Blank out fields inaccessible to the user
            filterFields(toReturn, "");
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
     * @param sourceId
     *            ID that was deleted, where anything else with that ID should also be deleted
     */
    private void cascadeDelete(String sourceId) {
        // loop for every EntityDefinition that references the deleted entity's type
        for (EntityDefinition referencingEntity : defn.getReferencingEntities()) {
            // loop for every reference field that COULD reference the deleted ID
            for (String referenceField : referencingEntity.getReferenceFieldNames(defn.getStoredCollectionName())) {
                EntityService referencingEntityService = referencingEntity.getService();
                NeutralQuery neutralQuery = new NeutralQuery();
                neutralQuery.addCriteria(new NeutralCriteria(referenceField + "=" + sourceId));
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
                    debug("No {} have {}={}", new Object[] { referencingEntity.getResourceName(), referenceField,
                            sourceId });
                }
            }
        }
    }
    
    private void deleteAttachedCustomEntities(String sourceId) {
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_ENTITY_ID, "=", sourceId, false));
        Iterable<String> ids = getRepo().findAllIds(CUSTOM_ENTITY_COLLECTION, query);
        for (String id : ids) {
            getRepo().delete(CUSTOM_ENTITY_COLLECTION, id);
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
     * @throws EntityNotFoundException
     *             if requested entity doesn't exist
     * @throws AccessDeniedException
     *             if actor doesn't have association path to given entity
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
            if (entityId != null && !findAccessible().contains(entityId)) {
                throw new AccessDeniedException("No association between the user and target entity");
            }
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
    
    private List<String> findAccessible() {
        
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal == null) {
            throw new AccessDeniedException("Principal cannot be found");
        }
        
        Entity entity = principal.getEntity();
        String type = entity != null ? entity.getType() : null;   // null for super admins because
        // they don't contain mongo
        // entries
        
        if (getAuths().contains(Right.FULL_ACCESS)) {  // Super admin
            return AllowAllEntityContextResolver.SUPER_LIST;
        }
        
        EntityContextResolver resolver = contextResolverStore.findResolver(type, defn.getType());
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
     * @param fieldPath
     *            The field name
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
     * @param query
     *            The query to check
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
        metadata.put("isOrphaned", "true");
        metadata.put("createdBy", principal.getEntity().getEntityId());
        metadata.put("tenantId", principal.getTenantId());
        return metadata;
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
