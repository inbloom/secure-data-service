package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.representation.EntityBody;

import java.util.List;

/**
 * Resource service.
 *
 * @author srupasinghe
 */

public interface ResourceService {

    public EntityBody getEntity(String resource, String id);

    public List<EntityBody> getEntities(String resource);
}
