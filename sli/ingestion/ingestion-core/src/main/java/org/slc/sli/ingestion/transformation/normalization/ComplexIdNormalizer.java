package org.slc.sli.ingestion.transformation.normalization;

import java.util.List;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * 
 * This class is responsible for resolving references that are more complicated
 * than a simple lookup in the target collection. IdNormalizer.java often assumes 
 * that a field can be resolved by searching metaData. This class allows custom
 * logic for times where that is not appropriate.
 * 
 * For example, an implementation may resolve a reference by searching a second
 * collection for a reference it contains.
 * 
 * @author kmyers
 *
 */
public interface ComplexIdNormalizer {
    
    /**
     * Resolves the specified field's reference and returns the associated ID. 
     * Throws an IdResolutionException if ID cannot be resolved.
     * 
     * @param entity entity containing field that needs to be resolved
     * @param neutralQuery a query where "tenantId" is already specified
     * @param entityRepository access to execute query
     * @return resolved ID or an empty list
     */
    public List<String> resolveInternalId(Entity entity, NeutralQuery neutralQuery, Repository<Entity> entityRepository) throws IdResolutionException;
}
