package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

/**
 * Resource service.
 *
 * @author srupasinghe
 */

public interface ResourceService {
    public List<EntityBody> getEntitiesByIds(Resource resource, String idList, URI requestURI, MultivaluedMap<String, String> requestParams);

    public List<EntityBody> getEntities(Resource resource, URI requestURI, MultivaluedMap<String, String> requestParams);

    public List<EntityBody> getEntities(Resource base, String id, Resource resource, URI requestURI);

    public List<EntityBody> getEntities(Resource base, String id, Resource association,
                                        Resource resource, UriInfo uriInfo);

    public long getEntityCount(Resource resource, URI requestURI, MultivaluedMap<String, String> requestParams);
    public String postEntity(Resource resource, EntityBody entity);

    public void putEntity(Resource resource, String id, EntityBody entity);

    public void deleteEntity(Resource resource, String id);

    public String getEntityType(Resource resource);
}
