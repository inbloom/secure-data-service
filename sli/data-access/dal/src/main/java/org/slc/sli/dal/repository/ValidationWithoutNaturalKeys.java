package org.slc.sli.dal.repository;

import org.slc.sli.domain.Entity;

public interface ValidationWithoutNaturalKeys {
    
    public boolean updateWithoutValidatingNaturalKeys(String entityType, Entity entity);
}
