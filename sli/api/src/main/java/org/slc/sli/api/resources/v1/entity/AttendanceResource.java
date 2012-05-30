package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.Path;

import org.slc.sli.api.resources.v1.DefaultCrudResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.config.EntityDefinitionStore;

/**
 * Represents the definition of an attendance resource. An attendance resource represents
 * both daily and class period (section) attendance.
 *
 * For more information, see the schema for $$Attendance$$ resources.
 */
@Path(PathConstants.V1 + "/" + PathConstants.ATTENDANCES)
@Component
@Scope("request")
public class AttendanceResource extends DefaultCrudResource {

    @Autowired
    public AttendanceResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.ATTENDANCES);
    }

}
