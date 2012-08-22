package org.slc.sli.api.resources.generic.response;

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.generic.GenericResource;
import org.slc.sli.api.resources.generic.representation.HateoasLink;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.service.EntityDecorator;
import org.slc.sli.api.resources.generic.service.ResourceService;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.resources.v1.view.View;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Builds a get response
 *
 * @author srupasinghe
 */

@Scope("request")
@Component
public class GetResponseBuilder extends ResponseBuilder {

    @Autowired
    protected ResourceService resourceService;

    @Autowired
    private List<EntityDecorator> entityDecorators;

    @Autowired
    private View optionalView;


    @Autowired
    private HateoasLink hateoasLink;

    public Response build(final UriInfo uriInfo, final ResourceTemplate template,
                            final ResourceMethod method, final GenericResource.GetResourceLogic logic) {
        //get the resource container
        Resource resource = constructAndCheckResource(uriInfo, template, method);

        //run the resource logic
        ServiceResponse serviceResponse = logic.run(resource);
        List<EntityBody> entities = serviceResponse.getEntityBodyList();

        //add the optional fields
        entities = optionalView.add(entities, resource.getResourceType(), uriInfo.getQueryParameters());

        //add the links
        entities = hateoasLink.add(resource.getResourceType(), entities, uriInfo);

        //apply the decorators
        for (EntityBody entityBody : entities) {
            for (EntityDecorator entityDecorator : entityDecorators) {
                entityDecorator.decorate(entityBody, resourceHelper.getEntityDefinition(resource), uriInfo.getQueryParameters());
            }
        }

        //get the page count
        long pagingHeaderTotalCount = serviceResponse.getEntityCount();

        Object retValue = getResponseObject(entities);

        //add the paging headers and return the data
        return addPagingHeaders(Response.ok(new EntityResponse(resourceService.getEntityType(resource), retValue)),
                pagingHeaderTotalCount, uriInfo).build();
    }

    protected Object getResponseObject(List<EntityBody> entities) {
        final Object retVal = entities.size() == 1 ? entities.get(0) : entities;
        return retVal;
    }

    protected Response.ResponseBuilder addPagingHeaders(Response.ResponseBuilder resp, long total, UriInfo info) {
        if (info != null && resp != null) {
            NeutralQuery neutralQuery = new ApiQuery(info);
            int offset = neutralQuery.getOffset();
            int limit = neutralQuery.getLimit();

            int nextStart = offset + limit;
            if (nextStart < total) {
                neutralQuery.setOffset(nextStart);

                String nextLink = info.getRequestUriBuilder().replaceQuery(neutralQuery.toString()).build().toString();
                resp.header(ParameterConstants.HEADER_LINK, "<" + nextLink + ">; rel=next");
            }

            if (offset > 0) {
                int prevStart = Math.max(offset - limit, 0);
                neutralQuery.setOffset(prevStart);

                String prevLink = info.getRequestUriBuilder().replaceQuery(neutralQuery.toString()).build().toString();
                resp.header(ParameterConstants.HEADER_LINK, "<" + prevLink + ">; rel=prev");
            }

            resp.header(ParameterConstants.HEADER_TOTAL_COUNT, total);
        }

        return resp;
    }
}
