package org.slc.sli.api.service;

import java.util.Map;

public interface Transformer {
    public Map<String, Object> toStored(Map<String, Object> exposed);
    
    public Map<String, Object> toExposed(Map<String, Object> stored);
    
}
