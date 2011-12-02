package org.slc.sli.api.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the entity definition store
 * 
 * @author nbrown
 * 
 */
public class BasicDefinitionStore implements EntityDefinitionStore {
    private final static Logger LOG = LoggerFactory.getLogger(BasicDefinitionStore.class);
    
    Map<String, EntityDefinition> mapping = new HashMap<String, EntityDefinition>();
    
    Map<EntityDefinition, Collection<EntityDefinition>> links = new HashMap<EntityDefinition, Collection<EntityDefinition>>();
    
    public BasicDefinitionStore() {
        init();
    }
    
    @Override
    public EntityDefinition lookupByResourceName(String resourceName) {
        return mapping.get(resourceName);
    }
    
    @Override
    public Collection<EntityDefinition> getLinked(EntityDefinition defn) {
        return links.get(defn);
    }
    
    private void init() {
        EntityDefinition student = EntityDefinition.makeEntity("students").build();
        addDefinition(student);
        EntityDefinition school = EntityDefinition.makeEntity("schools").build();
        addDefinition(school);
        AssociationDefinition studentEnroll = AssociationDefinition.makeAssoc("student-enrollments").from(student)
                .to(school).build();
        addAssocDefinition(studentEnroll);
    }
    
    private void add(EntityDefinition defn) {
        mapping.put(defn.getResourceName(), defn);
    }
    
    private void addDefinition(EntityDefinition defn) {
        LOG.debug("adding definition for {}", defn.getResourceName());
        add(defn);
        links.put(defn, new LinkedHashSet<EntityDefinition>());
    }
    
    private void addAssocDefinition(AssociationDefinition defn) {
        LOG.debug("adding assoc for {}", defn.getResourceName());
        add(defn);
        EntityDefinition sourceEntity = defn.getSourceEntity();
        EntityDefinition targetEntity = defn.getTargetEntity();
        links.put(defn, Arrays.asList(sourceEntity, targetEntity));
        links.get(targetEntity).add(defn);
        links.get(sourceEntity).add(defn);
    }
    
}
