package org.slc.sli.admin.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.RedirectView;

import org.slc.sli.admin.util.URLHelper;
import org.slc.sli.admin.util.UrlBuilder;

/**
 * Handles our NoSessionException and defers all others to the standard Spring
 * SimpleMappingExceptionResolver
 * 
 * @author scole
 * 
 */
@Component
public class AdminExceptionResolver extends SimpleMappingExceptionResolver {
    
    @Autowired
    private RESTClient restClient;
    
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        if (ex instanceof HttpClientErrorException) {
            int code = ((HttpClientErrorException) ex).getStatusCode().value();
            if (401 == code) {
                request.getSession().setAttribute("ADMIN_SESSION_ID", null);
                JsonObject jsonSession = this.restClient.sessionCheck(null);
                String baseUrl = jsonSession.get("redirect_user").getAsString();
                String requestedURL = URLHelper.getUrl(request);
                UrlBuilder url = new UrlBuilder(baseUrl);
                url.addQueryParam("RelayState", requestedURL);
                return new ModelAndView(new RedirectView(url.toString()), null);
            } else if (403 == code) {
                return new ModelAndView("notAuthorized", null);
            }
        }
        return super.resolveException(request, response, handler, ex);
    }
}
