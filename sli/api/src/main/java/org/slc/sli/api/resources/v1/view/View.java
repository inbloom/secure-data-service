package org.slc.sli.api.resources.v1.view;

import org.slc.sli.api.representation.EntityBody;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

/**
 * Manipulates a list of entities to add views
 *
 * @author srupasinghe
 */

public interface View {

    public List<EntityBody> add(List<EntityBody> entities, final String resource, final MultivaluedMap<String, String> queryParams);

}
