package org.slc.sli.api.security.schema;

import org.slc.sli.domain.enums.Right;

/**
 * Provides security related data from data definition
 *
 * @author dkornishev
 *
 */
public interface SchemaDataProvider {
    public Right getRequiredReadLevel(String entityType, String fieldPath);

    public Right getRequiredWriteLevel(String entityType, String fieldPath);

    public String getDataSphere(String entityType);
    
    public String getReferencingEntity(String entityType, String fieldPath);
}
