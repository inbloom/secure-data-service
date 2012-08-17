package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.springframework.util.MultiValueMap;

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

    public String postEntity(String resource, EntityBody entity);

    public String getEntityType(String resource);

    public long getEntityCount(String resource, URI requestURI, MultivaluedMap<String, String> requestParams);

    public List<EntityBody> getEntities(String base, String id, String resource);

    public EntityDefinition getEntityDefinition(String resource);

}
