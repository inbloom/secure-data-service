package org.slc.sli.api.resources.generic;

import java.net.URI;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
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
	public Response get(@Context final UriInfo uriInfo) {
		final String id = uriInfo.getPathSegments().get(2).getPath();
		Resource resource = resourceHelper.getResourceName(uriInfo, ResourceTemplate.FIVE_PART);
		Resource base = resourceHelper.getBaseName(uriInfo, ResourceTemplate.FIVE_PART);
		Resource association = resourceHelper.getAssociationName(uriInfo, ResourceTemplate.FIVE_PART);

		ServiceResponse resp = resourceService.getEntities(base, id, association, resource, uriInfo.getRequestUri());
		System.out.println(resp.getEntityBodyList());

		StringBuilder ids = new StringBuilder();

		for (EntityBody eb : resp.getEntityBodyList()) {
			ids.append(eb.get("id") + ",");
		}

		String cleanIds = ids.toString().replaceAll(",$", "");
		String newUri = String.format("/rest/%s/%s/%s/%s", uriInfo.getPathSegments().get(0).getPath(), uriInfo.getPathSegments().get(4).getPath(), cleanIds, uriInfo.getPathSegments().get(5).getPath());
		Response res = three.get(new ChangedUriInfo(newUri, uriInfo.getBaseUriBuilder()), cleanIds);
		return res;
	}

	private static class ChangedUriInfo implements UriInfo {

		private URI uri;
		private UriBuilder baseUriBuilder;

		public ChangedUriInfo(String uri, UriBuilder builder) {
			System.out.println("NEW URI: " + uri);
			this.uri = URI.create(uri);
			this.baseUriBuilder = builder;
		}

		@Override
		public String getPath() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getPath(boolean decode) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<PathSegment> getPathSegments() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<PathSegment> getPathSegments(boolean decode) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public URI getRequestUri() {
			return this.uri;
		}

		@Override
		public UriBuilder getRequestUriBuilder() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public URI getAbsolutePath() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public UriBuilder getAbsolutePathBuilder() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public URI getBaseUri() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public UriBuilder getBaseUriBuilder() {
			return this.baseUriBuilder;
		}

		@Override
		public MultivaluedMap<String, String> getPathParameters() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MultivaluedMap<String, String> getPathParameters(boolean decode) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MultivaluedMap<String, String> getQueryParameters() {
			return new MultivaluedHashMap<String, String>();
		}

		@Override
		public MultivaluedMap<String, String> getQueryParameters(boolean decode) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<String> getMatchedURIs() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<String> getMatchedURIs(boolean decode) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Object> getMatchedResources() {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
