package org.slc.sli.api.resources.generic;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.selectors.doc.Constraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * @author jstokes
 */
public final class GetAllAction implements GetAction {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceHelper resourceHelper;

    @Override
    public List<EntityBody> get(final UriInfo uriInfo, final Constraint constraint) {
        final String resourceName = resourceHelper.getResourceName(uriInfo, ResourceTemplate.ONE_PART);
        return resourceService.getEntities(resourceName);
    }
}
