package org.slc.sli.domain;

import java.util.Map;

public interface Entity {
    
    String getType();
    
    String getEntityId();
    
    Map<String, Object> getBody();
    
}
