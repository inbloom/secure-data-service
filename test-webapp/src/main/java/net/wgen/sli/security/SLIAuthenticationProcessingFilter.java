package net.wgen.sli.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.web.authentication.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.AuthenticationException;

public class SLIAuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String param = request.getParameter("authDirectory");
		Object u = request.getParameter("j_username");
		Object p = request.getParameter("j_password");
	    SLIUsernamePasswordAuthenticationToken authRequest = new SLIUsernamePasswordAuthenticationToken(u,p);
	    authRequest.setDirectory(param);
	
		HttpSession session = request.getSession();
		if (session != null || getAllowSessionCreation()) {
			session.setAttribute("authDirectory", param);
		}
		
		setDetails(request, authRequest);

        return super.getAuthenticationManager().authenticate(authRequest);
		
		//return super.attemptAuthentication(request, response);
	}

}