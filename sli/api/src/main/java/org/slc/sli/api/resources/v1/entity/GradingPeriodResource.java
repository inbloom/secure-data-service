package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;

/**
 * gradingPeriodsResource
 *
 * This entity represents an individual who performs specified activities for any public
 * or private education institution or agency that provides instructional and/or support
 * services to students or gradingPeriods at the early childhood level through high school
 * completion.
 *
 * @author jstokes
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.GRADING_PERIODS)
@Component
@Scope("request")
public class GradingPeriodResource extends DefaultCrudEndpoint {

    @Autowired
    public GradingPeriodResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.GRADING_PERIODS);
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
     * @return Returns all $$gradingPeriods$$ entities for which the logged in User has permission and context.
     */
    @Override
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
     *              URI information including path and query parameters
     * @return A 201 response on successfully created entity with the ID of the entity
     * @response.param {@name Location} {@style header} {@type
     *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI where the created
     *                 item is accessible.}
     */
    @Override
    @POST
    public Response create(final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * read
     *
     * @param gradingPeriodId
     *            The id (or list of ids) of the $$gradingPeriods$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A list of entities matching the list of ids queried for
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.GRADING_PERIOD_ID + "}")
    public Response read(@PathParam(ParameterConstants.GRADING_PERIOD_ID) final String gradingPeriodId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(gradingPeriodId, headers, uriInfo);
    }

    /**
     * delete
     *
     * @param gradingPeriodId
     *            The Id of the $$gradingPeriods$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.GRADING_PERIOD_ID + "}")
    public Response delete(@PathParam(ParameterConstants.GRADING_PERIOD_ID) final String gradingPeriodId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(gradingPeriodId, headers, uriInfo);
    }

    /**
     * update
     *
     * @param gradingPeriodId
     *            The id of the $$gradingPeriods$$.
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
    @Path("{" + ParameterConstants.GRADING_PERIOD_ID + "}")
    public Response update(@PathParam(ParameterConstants.GRADING_PERIOD_ID) final String gradingPeriodId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(gradingPeriodId, newEntityBody, headers, uriInfo);
    }

    /**
     * getReportCards
     *
     * @param gradingPeriodId
     *            The Id of the School.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns each $$gradingPeriodsEducationOrganizationAssociations$$ that references the given $$gradingPeriods$$
     */
    @GET
    @Path("{" + ParameterConstants.GRADING_PERIOD_ID + "}" + "/" + PathConstants.REPORT_CARDS)
    public Response getReportCards(@PathParam(ParameterConstants.GRADING_PERIOD_ID) final String gradingPeriodId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.REPORT_CARDS, "gradingPeriodId", gradingPeriodId, headers, uriInfo);
    }

    /**
     * getGrades
     *
     * @param gradingPeriodId
     *            The Id of the School.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns each $$gradingPeriodsEducationOrganizationAssociations$$ that references the given $$gradingPeriods$$
     */
    @GET
    @Path("{" + ParameterConstants.GRADING_PERIOD_ID + "}" + "/" + PathConstants.GRADES)
    public Response getGrades(@PathParam(ParameterConstants.GRADING_PERIOD_ID) final String gradingPeriodId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.GRADES, "gradingPeriodId", gradingPeriodId, headers, uriInfo);
    }


}
