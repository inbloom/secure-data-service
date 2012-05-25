package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.v1.DefaultCrudResource;

/**
 * Provides references for competency level descriptors during interchange. Use XML IDREF to
 * reference a course record that is included in the interchange. To lookup where already loaded
 * specify either CodeValue OR Description.
 *
 * For more information, see the schema $$CompetencyLevelDescriptorType$$ resources.
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
