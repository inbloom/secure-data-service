package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.v1.DefaultCrudResource;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.PathConstants;

/**
 * Represents the cumulative record of academic achievements and the collection of student grades
 * for the student at the end of a semester or school year.
 *
 * If you're looking for records for a particular course, use StudentTranscriptAssociationResource instead.
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

    /**
     * Logging utility.
     */
    private static final Logger LOG = LoggerFactory.getLogger(StudentAcademicRecordResource.class);

    @Autowired
    public StudentAcademicRecordResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_ACADEMIC_RECORDS);
        LOG.debug("Initialized a new {}", StudentAcademicRecordResource.class);
    }

}
