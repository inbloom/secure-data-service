package org.slc.sli.api.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicDefinitionService implements EntityDefinitionService {
    private final static Logger LOG = LoggerFactory.getLogger(BasicDefinitionService.class);
    
    Map<String, EntityDefinition> mapping = new HashMap<String, EntityDefinition>();
    
    Map<EntityDefinition, Collection<EntityDefinition>> links = new HashMap<EntityDefinition, Collection<EntityDefinition>>();
    
    public BasicDefinitionService() {
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
        EntityDefinition student = new EntityDefinition("students");
        addDefinition(student);
        EntityDefinition school = new EntityDefinition("schools");
        addDefinition(school);
        AssociationDefinition studentEnroll = new AssociationDefinition("student-enrollments", student, school);
        addAssocDefinition(studentEnroll);
    }
    
    private void addDefinition(EntityDefinition defn) {
        LOG.debug("adding definition for {}", defn.getResourceName());
        mapping.put(defn.getResourceName(), defn);
        links.put(defn, new LinkedHashSet<EntityDefinition>());
    }
    
    private void addAssocDefinition(AssociationDefinition defn) {
        LOG.debug("adding assoc for {}", defn.getResourceName());
        mapping.put(defn.getResourceName(), defn);
        EntityDefinition sourceEntity = defn.getSourceEntity();
        EntityDefinition targetEntity = defn.getTargetEntity();
        links.put(defn, Arrays.asList(sourceEntity, targetEntity));
        links.get(targetEntity).add(defn);
        links.get(sourceEntity).add(defn);
    }
    
    @Override
    public List<Validator> getValidators(EntityDefinition defn) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<Transformer> getTransformers(EntityDefinition defn) {
        // TODO Auto-generated method stub
        return null;
    }
}
