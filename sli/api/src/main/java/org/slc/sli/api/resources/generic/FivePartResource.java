package org.slc.sli.api.resources.generic;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.resources.generic.util.ChangedUriInfo;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Ok, copy and paste inheritance, but the four and three part handlers are riddled with anonymous classes and call-back
 * driven design, none of which is re-usable Using handlers directly would impose considerable overhead
 * 
 * @author dkornishev
 * 
 */
@Component
@Scope("request")
public class FivePartResource extends GenericResource {

	@Autowired
	private ThreePartResource three;

	@GET
	public Response get(@Context UriInfo uriInfo) {
		String ids = locateIds(uriInfo, ResourceTemplate.FIVE_PART);
		List<String> segments = extractSegments(uriInfo.getPathSegments(), Arrays.asList(0, 4, 5));
		segments.add(2, ids);
		String newUri = String.format("/rest/%s/%s/%s/%s", segments.toArray());
		Response res = three.get(new ChangedUriInfo(newUri, uriInfo.getBaseUriBuilder()), ids);
		return res;
	}
}
