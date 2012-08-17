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
    public EntityBody getEntity(final String resource, final String id, final UriInfo uriInfo);

    public List<EntityBody> getEntities(final String resource, final UriInfo uriInfo);

    public String postEntity(final String resource, EntityBody entity);

    public String getEntityType(final String resource);

    public long getEntityCount(String resource, final UriInfo uriInfo);

    public List<EntityBody> getEntities(String base, String id, String resource);

    public EntityDefinition getEntityDefinition(String resource);

}
