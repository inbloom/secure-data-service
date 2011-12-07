package org.slc.sli.api.service;

import java.util.List;
import java.util.Map;

public interface EntityService {
    public boolean create(String type, Map<String, Object> content);
    
    public boolean delete(String type, String id);
    
    public boolean update(String type, String id, Map<String, Object> content);
    
    public Map<String, Object> get(String type, String id);
    
    public List<Map<String, Object>> list(String type);
    
    public List<Map<String, Object>> getAssociated(String baseType, String baseID, String assocType);
    
    public boolean associate(String type1, String id1, String type2, String id2);
    
    public boolean associate(String type1, String id1, String type2, String id2, Map<String, Object> content);
}
