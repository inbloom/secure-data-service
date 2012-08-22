package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;

import javax.ws.rs.core.MultivaluedMap;

/**
 * A decorator to add data to an entity
 *
 * @author srupasinghe
 */

public interface EntityDecorator {

    public void decorate(EntityBody entity, EntityDefinition definition, MultivaluedMap<String, String> queryParams);
}
