package org.slc.sli.api.resources.generic;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.Arrays;
import java.util.List;

/**
 * @author jstokes
 */
public final class GetByIdAction implements GetAction {
    @Autowired
    private ResourceService resourceService;

    @Override
    public List<EntityBody> get(final UriInfo uriInfo) {
        return null;
    }
}
