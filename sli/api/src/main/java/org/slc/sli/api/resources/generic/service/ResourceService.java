package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.representation.EntityBody;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 8/2/12
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ResourceService {

    public EntityBody getEntity(String resource, String id);

    public List<EntityBody> getEntities(String resource);
}
