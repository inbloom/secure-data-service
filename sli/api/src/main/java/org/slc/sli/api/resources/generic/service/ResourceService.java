package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;

import java.net.URI;
import java.util.List;

/**
 * Resource service.
 *
 * @author srupasinghe
 */

public interface ResourceService {
    public ServiceResponse getEntitiesByIds(Resource resource, String idList, URI requestURI);

    public ServiceResponse getEntities(Resource resource, URI requestURI, boolean getAllEntities);

    public ServiceResponse getEntities(Resource base, String id, Resource resource, URI requestURI);

    public ServiceResponse getEntities(Resource base, String id, Resource association,
                                        Resource resource, URI requestURI);

    public String postEntity(Resource resource, EntityBody entity);

    public void putEntity(Resource resource, String id, EntityBody entity);

    public void patchEntity(Resource resource, String id, EntityBody entity);

    public void deleteEntity(Resource resource, String id);

    public String getEntityType(Resource resource);
}
