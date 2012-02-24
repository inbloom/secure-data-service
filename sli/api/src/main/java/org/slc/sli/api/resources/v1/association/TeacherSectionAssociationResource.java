package org.slc.sli.api.resources.v1.association;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.resources.v1.PathConstants;

/**
 * Prototype new api end points and versioning
 * 
 * @author srupasinghe
 * 
 */
@Path(PathConstants.TEACHER_SECTION_ASSOCIATIONS)
@Component
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE, Resource.SLC_LONG_JSON_MEDIA_TYPE })
public class TeacherSectionAssociationResource {
    private final Resource crudDelegate;
    
    @Autowired
    public TeacherSectionAssociationResource(EntityDefinitionStore entityDefs) {
        this.crudDelegate = new Resource(entityDefs.lookupByResourceName(PathConstants.TEACHER_SECTION_ASSOCIATIONS),
                entityDefs);
    }
    
    @Path("/")
    public Resource handle() {
        return crudDelegate;
    }
}
