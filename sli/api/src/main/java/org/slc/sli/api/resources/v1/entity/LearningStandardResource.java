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
 * Represents a subelement of a learning objective consisting of a precise statement of the
 * expectation of a student's proficiency.
 *
 * For more information, see the schema for the $$learningStandardResources$$ entity.
 *
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.LEARNING_STANDARDS)
@Component
@Scope("request")
public class LearningStandardResource extends DefaultCrudResource {

    @Autowired
    public LearningStandardResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.LEARNINGSTANDARDS);
    }

}
