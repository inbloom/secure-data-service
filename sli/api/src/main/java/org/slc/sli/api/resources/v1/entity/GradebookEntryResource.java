package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.Path;

import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.v1.DefaultCrudResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Represents a teacher’s assignment, homework, or classroom assessment to be recorded in a
 * gradebook.
 *
 * For more information, see the schema for $$GradebookEntry$$ resources.
 * 
 * @author kmyers
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.GRADEBOOK_ENTRIES)
@Component
@Scope("request")
public class GradebookEntryResource extends DefaultCrudResource {

    @Autowired
        public GradebookEntryResource(EntityDefinitionStore entityDefs) {
            super(entityDefs, ResourceNames.GRADEBOOK_ENTRIES);
        debug("Initialized a new {}", GradebookEntryResource.class);
    }

}
