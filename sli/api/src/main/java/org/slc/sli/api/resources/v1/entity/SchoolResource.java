package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.resources.v1.PathConstants;

/**
 * Responds to requests for school-related information.
 * 
 * @author srupasinghe
 * @author kmyers
 * 
 */
@Path(PathConstants.SCHOOLS)
@Component
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE, Resource.SLC_LONG_JSON_MEDIA_TYPE })
public class SchoolResource extends Resource {
    
    @Autowired
    public SchoolResource(EntityDefinitionStore entityDefs) {
        super(entityDefs.lookupByResourceName(PathConstants.SCHOOLS),
                entityDefs);
    }
    
    
}
