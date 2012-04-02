package org.slc.sli.api.resources.v1.view;

import java.util.List;

import org.slc.sli.api.representation.EntityBody;

/**
 * Interface for providing a strategy for marshaling required data
 * for custom views returned by the api 
 * @author srupasinghe
 *
 */
public interface OptionalFieldAppender {
    public List<EntityBody> applyOptionalField(List<EntityBody> entities);
}
