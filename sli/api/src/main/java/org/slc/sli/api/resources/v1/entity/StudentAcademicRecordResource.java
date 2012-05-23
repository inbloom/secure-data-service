package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.client.constants.ResourceNames;
import org.slc.sli.client.constants.v1.ParameterConstants;
import org.slc.sli.client.constants.v1.PathConstants;

/**
 * StudentAcademicRecordResource
 *
 * This resource responds to create, read one, read all, update, and delete operations for student academic records.
 *
 * If you're looking for records for a particular course, use StudentTranscriptAssociationResource instead.
 *
 * Limitations: None
 *
 * @author kmyers
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENT_ACADEMIC_RECORDS)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
public class StudentAcademicRecordResource extends DefaultCrudEndpoint {

    /**
     * Logging utility.
     */
    private static final Logger LOG = LoggerFactory.getLogger(StudentAcademicRecordResource.class);

    @Autowired
    public StudentAcademicRecordResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_ACADEMIC_RECORDS);
        LOG.debug("Initialized a new {}", StudentAcademicRecordResource.class);
    }

    /**
     * readAll
     *
     * @param offset
     *            starting position in results to return to user 
     * @param limit
     *            maximum number of results to return to user (starting from offset)
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return all $$studentAcademicRecords$$ entities for which the logged in User has permission and context
     * @response.representation.200.mediaType HTTP headers with an OK status code.
     * @response.representation $$studentAcademicRecords$$
     */
    @Override
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * create
     *
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *              URI information including path and
     * @return response containing ID/location of newly created entity
     * @response.representation.201.mediaType HTTP headers with a CREATED status code.
     */
    @Override
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response create(final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * read
     *
     * @param studentAcademicRecordId
     *            The Id of the $$studentAcademicRecords$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return a single $$studentAcademicRecords$$ entity
     * @response.representation.200.mediaType HTTP headers with an OK status code.
     * @response.representation $$studentAcademicRecords$$
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.STUDENT_ACADEMIC_RECORD_ID + "}")
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response read(@PathParam(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID) final String studentAcademicRecordId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(studentAcademicRecordId, headers, uriInfo);
    }

    /**
     * delete
     *
     * @param studentAcademicRecordId
     *            The Id of the $$studentAcademicRecords$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.STUDENT_ACADEMIC_RECORD_ID + "}")
    public Response delete(@PathParam(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID) final String studentAcademicRecordId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(studentAcademicRecordId, headers, uriInfo);
    }

    /**
     * update
     *
     * Updates an existing $$studentAcademicRecords$$ entity.
     *
     * @param studentAcademicRecordId
     *            The id of the $$studentAcademicRecords$$.
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @PUT
    @Path("{" + ParameterConstants.STUDENT_ACADEMIC_RECORD_ID + "}")
    public Response update(@PathParam(ParameterConstants.STUDENT_ACADEMIC_RECORD_ID) final String studentAcademicRecordId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(studentAcademicRecordId, newEntityBody, headers, uriInfo);
    }
}
