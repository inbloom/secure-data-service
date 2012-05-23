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
 * Represents a student's grade or competency level for a $$GradebookEntry$$.
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENT_SECTION_GRADEBOOK_ENTRIES)
@Component
@Scope("request")
public class StudentSectionGradebookEntryResource extends DefaultCrudResource {

    @Autowired
    public StudentSectionGradebookEntryResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_SECTION_GRADEBOOK_ENTRIES);
    }

}
