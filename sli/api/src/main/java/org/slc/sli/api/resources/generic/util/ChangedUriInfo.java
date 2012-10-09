package org.slc.sli.api.resources.generic.util;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class ChangedUriInfo implements UriInfo {

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