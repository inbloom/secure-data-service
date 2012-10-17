package org.slc.sli.api.representation;

import java.util.Collections;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlRootElement;

import org.slc.sli.api.security.pdp.ContextInferrenceFailedException;
import org.springframework.stereotype.Component;

/**
 * Handles empty contexts
 * 
 * @author dkornishev
 * 
 */
@Provider
@Component
public class ContextInferrenceFailedExceptionHandler implements ExceptionMapper<ContextInferrenceFailedException> {

	@Context
	private HttpHeaders headers;

	@Override
	public Response toResponse(ContextInferrenceFailedException exception) {
		warn("Failed Context Inferrence");
		Object entity = Collections.EMPTY_LIST;
		if (headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_XML_TYPE)) {
			entity=new EmptyResponse();
		}

		return Response.ok(entity).header("TotalCount", 0).build();
	}

	@XmlRootElement(name = "emptyList")
	public static class EmptyResponse {
	}
}
