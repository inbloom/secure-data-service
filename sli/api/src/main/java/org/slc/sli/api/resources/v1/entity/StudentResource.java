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
 * Entities representing students
 * 
 * @author jstokes
 * 
 */
@Path(PathConstants.STUDENTS)
@Component
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE, Resource.SLC_LONG_JSON_MEDIA_TYPE })
public class StudentResource extends Resource {
    
    @Autowired
    public StudentResource(EntityDefinitionStore entityDefs) {
        super(entityDefs.lookupByResourceName(PathConstants.STUDENTS), entityDefs);
    }
    
}
