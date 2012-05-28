package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.v1.DefaultCrudResource;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.PathConstants;

/**
 * Represents an overall score or assessment tied to a course over a period
 * of time (i.e., the grading period).
 *
 * Student grades are usually a compilation of marks and other scores.
 *
 * For more information, see the schema for $$Grade$$ resources.
 *
 * @author jstokes
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.GRADES)
@Component
@Scope("request")
public class GradeResource extends DefaultCrudResource {

    @Autowired
    public GradeResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.GRADES);
    }
}
