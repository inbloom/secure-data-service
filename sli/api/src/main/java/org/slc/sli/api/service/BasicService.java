package org.slc.sli.api.service;

import java.util.List;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class BasicService implements EntityService {
    private final static Logger LOG = LoggerFactory.getLogger(BasicService.class);
    private final String collectionName;
    private final List<Treatment> treatments;
    private final List<Validator> validators;
    
    public BasicService(String collectionName, List<Treatment> treatments, List<Validator> validators) {
        super();
        this.collectionName = collectionName;
        this.treatments = treatments;
        this.validators = validators;
    }
    
    @Override
    public String create(EntityBody content) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean delete(String id) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean update(String id, EntityBody content) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public EntityBody get(String id) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Iterable<EntityBody> get(Iterable<String> ids) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Iterable<String> list() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Iterable<String> getAssociated(String id, EntityDefinition assocType) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
