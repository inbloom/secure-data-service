package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;

import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Resource service.
 *
 * @author srupasinghe
 */

public interface ResourceService {
    public List<EntityBody> getEntitiesByIds(String resource, String idList, UriInfo uriInfo);

    public List<EntityBody> getEntities(String resource, UriInfo uriInfo);

    public String postEntity(String resource, EntityBody entity);

    public String getEntityType(String resource);

    public long getEntityCount(String resource, UriInfo uriInfo);

    public List<EntityBody> getEntities(String base, String id, String resource, UriInfo uriInfo);

    public List<EntityBody> getEntities(String base, String id, String association,
                                        String resource, UriInfo uriInfo);

    public EntityDefinition getEntityDefinition(String resource);

}
