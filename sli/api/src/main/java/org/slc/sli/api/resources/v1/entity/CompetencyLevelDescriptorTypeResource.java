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
 * Resource handler for CompetencyLevelDescriptorType entity.
 * Stubbed out for documentation
 *
 * @author chung
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.COMPETENCY_LEVEL_DESCRIPTOR_TYPES)
@Component
@Scope("request")
public class CompetencyLevelDescriptorTypeResource extends DefaultCrudResource {

    @Autowired
    public CompetencyLevelDescriptorTypeResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.COMPETENCY_LEVEL_DESCRIPTOR_TYPES);
    }

}
