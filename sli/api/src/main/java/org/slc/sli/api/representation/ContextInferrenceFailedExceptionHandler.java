package org.slc.sli.api.representation;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlRootElement;

import org.slc.sli.api.security.pdp.ContextInferrenceFailedException;
import org.springframework.stereotype.Component;

/**
 * Handles empty contexts
 * @author dkornishev
 *
 */
@Provider
@Component
public class ContextInferrenceFailedExceptionHandler implements ExceptionMapper<ContextInferrenceFailedException>{

	@Override
	public Response toResponse(ContextInferrenceFailedException exception) {
		warn("Failed Context Inferrence");
		return Response.ok(new EmptyResponse()).header("TotalCount", 0).build();
	}

	@XmlRootElement(name = "emptyList")
	public static class EmptyResponse {
		
	}
}
