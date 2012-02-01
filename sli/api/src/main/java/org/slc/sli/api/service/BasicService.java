package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avro.Schema;
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

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.EntityContextResolver;
import org.slc.sli.dal.convert.IdConverter;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.enums.Right;
import org.slc.sli.validation.EntitySchemaRegistry;

/**
 * Implementation of EntityService that can be used for most entities.
 * 
 * It is very important this bean prototype scope, since one service is needed per
 * entity/association.
 * 
 * Now Secured!
 */
@Scope("prototype")
@Component("basicService")
public class BasicService implements EntityService {
    
    private static final Logger  LOG                    = LoggerFactory.getLogger(BasicService.class);
    
    private static final String  READ_ENFORCEMENT_VALUE = "restricted";
    private static final String  READ_ENFORCEMENT       = "read_enforcement";
    private static final int     MAX_RESULT_SIZE        = 9999;
    
    private String               collectionName;
    private List<Treatment>      treatments;
    private EntityDefinition     defn;
    
    @Autowired
    private EntityRepository     repo;
    
    @Autowired
    private ContextResolverStore contextResolverStore;
    
    @Autowired
    private EntitySchemaRegistry schemaRegistry;
    
    @Autowired
    private IdConverter          idConverter;
    
    public BasicService(String collectionName, List<Treatment> treatments) {
        this.collectionName = collectionName;
        this.treatments = treatments;
    }
    
    @Override
    public String create(EntityBody content) {
        LOG.debug("Creating a new entity in collection {} with content {}", new Object[] { collectionName, content });
        
        checkRights(determineWriteAccess(content));
        
        return repo.create(defn.getType(), sanitizeEntityBody(content), collectionName).getEntityId();
    }
    
    @Override
    public void delete(String id) {
        LOG.debug("Deleting {} in {}", new String[] { id, collectionName });
        
        checkAccess(Right.WRITE_GENERAL, id);
        
        if (!repo.delete(collectionName, id)) {
            LOG.info("Could not find {}", id);
            throw new EntityNotFoundException(id);
        }
        
        if (!(defn instanceof AssociationDefinition)) {
            removeEntityWithAssoc(id);
        }
    }
    
    @Override
    public boolean update(String id, EntityBody content) {
        LOG.debug("Updating {} in {}", new String[] { id, collectionName });
        
        checkAccess(determineWriteAccess(content), id);
        
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
        
        Entity entity = repo.find(collectionName, id);
        
        if (entity == null) {
            throw new EntityNotFoundException(id);
        }
        
        return makeEntityBody(entity);
    }
    
    @Override
    public Iterable<EntityBody> get(Iterable<String> ids) {
        
        checkRights(Right.READ_GENERAL);
        
        List<String> allowed = findAccessible();
        
        // Compute intersection of requested and allowed and encode
        Set<Object> binIds = new HashSet<Object>();
        for (String id : ids) {
            if (allowed.contains(ids)) {
                binIds.add(idConverter.toDatabaseId(id));
            }
        }
        
        if (!binIds.isEmpty()) {
            Iterable<Entity> entities = repo.findByQuery(collectionName, new Query(Criteria.where("_id").in(binIds)), 0, MAX_RESULT_SIZE);
            
            List<EntityBody> results = new ArrayList<EntityBody>();
            for (Entity e : entities) {
                results.add(makeEntityBody(e));
            }
            
            return results;
        } else {
            throw new AccessDeniedException("No access to any requested entities");
        }
    }
    
    @Override
    public Iterable<String> list(int start, int numResults) {
        checkRights(Right.READ_GENERAL);
        
        Query query = new Query();
        
        List<String> allowed = findAccessible();
        
        if (allowed.size() > 0) {
            Set<Object> binIds = new HashSet<Object>();
            for (String id : allowed) {
                binIds.add(idConverter.toDatabaseId(id));
            }
            
            query = new Query(Criteria.where("_id").in(binIds));
        }
        
        List<String> results = new ArrayList<String>();
        
        Iterable<Entity> entities = repo.findByQuery(collectionName, query, start, numResults);
        for (Entity entity : entities) {
            results.add(entity.getEntityId());
        }
        
        return results;
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
        filterFields(toReturn);
        
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
    
    private void removeEntityWithAssoc(String sourceId) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put(defn.getType() + "Id", sourceId);
        
        for (AssociationDefinition assocDef : defn.getLinkedAssoc()) {
            String assocCollection = assocDef.getStoredCollectionName();
            Iterable<Entity> iterable = repo.findByFields(assocCollection, fields);
            Iterator<Entity> foundEntities;
            if (iterable != null) {
                foundEntities = iterable.iterator();
                while (foundEntities.hasNext()) {
                    Entity assocEntity = foundEntities.next();
                    repo.delete(assocCollection, assocEntity.getEntityId());
                }
            }
        }
    }
    
    /**
     * Checks that Actor has the appropriate Rights and linkage to access given entity
     * Also checks for existence of the given entity
     * 
     * @param right needed Right for action
     * @param entityId id of the entity to access
     * @throws InsufficientAuthenticationException if authentication is required
     * @throws EntityNotFoundException if requested entity doesn't exist
     * @throws AccessDeniedException if actor doesn't have association path to given entity
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
    
    private void checkRights(Right right) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException("Login Required");
        }
        
        Collection<GrantedAuthority> auths = auth.getAuthorities();
        
        if (auths.contains(Right.FULL_ACCESS)) {
            LOG.debug("User has full access");
        } else if (auths.contains(right)) {
            LOG.debug("User has needed right: " + right);
        } else {
            throw new AccessDeniedException("Insufficient Privileges");
        }
    }
    
    private List<String> findAccessible() {
        
        SLIPrincipal principal = (SLIPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        EntityContextResolver resolver = contextResolverStore.getContextResolver(principal.getEntity().getType(), defn.getType());
        
        return resolver.findAccessible(principal.getEntity());
    }
    
    /**
     * Removes fields user isn't entitled to see
     * 
     * @param eb
     */
    private void filterFields(EntityBody eb) {
        
        Collection<GrantedAuthority> auths = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        
        if (!auths.contains(Right.READ_RESTRICTED)) {
            Schema schema = schemaRegistry.findSchemaForName(defn.getType());
            
            if (schema != null) {
                Iterator<String> keyIter = eb.keySet().iterator();
                
                while (keyIter.hasNext()) {
                    String fieldName = keyIter.next();
                    
                    Schema.Field field = schema.getField(fieldName);
                    LOG.debug("Field {} is restricted {}", fieldName, isRestrictedField(field));
                    
                    if (isRestrictedField(field)) {
                        keyIter.remove();
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
    private Right determineWriteAccess(EntityBody eb) {
        Right toReturn = Right.WRITE_GENERAL;
        Schema schema = schemaRegistry.findSchemaForName(defn.getType());
        
        if (schema != null) {
            Iterator<String> keyIter = eb.keySet().iterator();
            
            while (keyIter.hasNext()) {
                String fieldName = keyIter.next();
                
                Schema.Field field = schema.getField(fieldName);
                LOG.debug("Field {} is restricted {}", fieldName, isRestrictedField(field));
                
                if (isRestrictedField(field)) {
                    toReturn = Right.WRITE_RESTRICTED;
                    break;
                }
            }
        }
        return toReturn;
    }
    
    private boolean isRestrictedField(Schema.Field field) {
        return field != null && field.getProp(READ_ENFORCEMENT) != null && field.getProp(READ_ENFORCEMENT).equals(READ_ENFORCEMENT_VALUE);
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
    
    protected EntityRepository getRepo() {
        return repo;
    }
}
