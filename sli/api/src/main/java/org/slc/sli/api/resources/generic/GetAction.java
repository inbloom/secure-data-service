package org.slc.sli.api.resources.generic;

import org.slc.sli.api.representation.EntityBody;

import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * @author jstokes
 */
public interface GetAction {
    List<EntityBody> get(final UriInfo uriInfo);
}
