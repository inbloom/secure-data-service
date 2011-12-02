package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicDefinitionStore implements EntityDefinitionStore {
    private final static Logger LOG = LoggerFactory.getLogger(BasicDefinitionStore.class);
    
    Map<String, EntityDefinition> mapping = new HashMap<String, EntityDefinition>();
    
    Map<EntityDefinition, Collection<EntityDefinition>> links = new HashMap<EntityDefinition, Collection<EntityDefinition>>();
    
    Map<EntityDefinition, List<Validator>> validators = new HashMap<EntityDefinition, List<Validator>>();
    
    Map<EntityDefinition, List<Treatment>> treatments = new HashMap<EntityDefinition, List<Treatment>>();
    
    Map<EntityDefinition, List<Filter>> filters = new HashMap<EntityDefinition, List<Filter>>();
    
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
        EntityDefinition student = new EntityDefinition("students");
        addDefinition(student);
        EntityDefinition school = new EntityDefinition("schools");
        addDefinition(school);
        AssociationDefinition studentEnroll = new AssociationDefinition("student-enrollments", student, school);
        addAssocDefinition(studentEnroll);
    }
    
    private void add(EntityDefinition defn) {
        mapping.put(defn.getResourceName(), defn);
        validators.put(defn, new ArrayList<Validator>());
        treatments.put(defn, new ArrayList<Treatment>());
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
    
    @Override
    public List<Validator> getValidators(EntityDefinition defn) {
        return validators.get(defn);
    }
    
    @Override
    public List<Treatment> getTreatments(EntityDefinition defn) {
        return treatments.get(defn);
    }
    
    @Override
    public List<Filter> getImpliedFilters(EntityDefinition defn) {
        return filters.get(defn);
    }
}
