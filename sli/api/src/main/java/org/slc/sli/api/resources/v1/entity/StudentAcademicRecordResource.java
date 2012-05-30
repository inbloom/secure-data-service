package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.v1.DefaultCrudResource;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Represents the cumulative record of academic achievements and the collection of student grades
 * for the student at the end of a semester or school year.
 *
 * If you're looking for records for a particular course, use StudentTranscriptAssociationResource
 * instead.
 *
 * For detailed information, see the schema for $$StudentAcademicRecord$$ resources.
 *
 * @author kmyers
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENT_ACADEMIC_RECORDS)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
public class StudentAcademicRecordResource extends DefaultCrudResource {

    @Autowired
    public StudentAcademicRecordResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_ACADEMIC_RECORDS);
        debug("Initialized a new {}", StudentAcademicRecordResource.class);
    }

}
