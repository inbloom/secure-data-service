package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;

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
    public List<EntityBody> getEntitiesByIds(String resource, String idList, URI requestURI, MultivaluedMap<String, String> requestParams);

    public List<EntityBody> getEntities(String resource, URI requestURI, MultivaluedMap<String, String> requestParams);

    public List<EntityBody> getEntities(String base, String id, String resource, URI requestURI);

    public List<EntityBody> getEntities(String base, String id, String association,
                                        String resource, UriInfo uriInfo);

    public String postEntity(String resource, EntityBody entity);

    public void putEntity(String resource, String id, EntityBody entity);

    public void deleteEntity(String resource, String id);

    public Long getEntityCount(String resource, URI requestURI, MultivaluedMap<String, String> requestParams);

    public String getEntityType(String resource);

    public EntityDefinition getEntityDefinition(String resource);

}
