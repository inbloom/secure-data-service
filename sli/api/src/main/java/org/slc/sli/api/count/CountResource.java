package org.slc.sli.api.count;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.slc.sli.api.resources.generic.DefaultResource;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@Scope("request")
@Path("count")
@Produces({ HypermediaType.JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8" })
public class CountResource extends DefaultResource {

	@Autowired
	CountService countService;

	@GET
	@Path("educationOrganizations")
	public @ResponseBody List<EducationOrganizationCount> find(@Context HttpServletRequest request) {
		return countService.find();
	}

	@GET
	@Path("educationOrganizations/{edOrgId}")
	public @ResponseBody EducationOrganizationCount find(@PathParam("edOrgId") String edOrgId, @Context HttpServletRequest request) {
		return countService.findOne(edOrgId);
	}
}