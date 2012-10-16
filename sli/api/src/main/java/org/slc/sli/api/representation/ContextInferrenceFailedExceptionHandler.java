package org.slc.sli.api.representation;

import java.util.Collections;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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
		info("Borked");
		return Response.ok(Collections.EMPTY_LIST).build();
	}

}
