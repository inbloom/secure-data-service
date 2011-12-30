package org.slc.sli.admin.client;

import java.io.IOException;

import junit.framework.Assert;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import org.slc.sli.admin.test.bootstrap.WebContextTestExecutionListener;

/**
 * 
 * @author scole
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/commonCtx.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class AdminExceptionResolverTest {
    
    private static final String RAW_JSON = "{\"authenticated\":false,\"redirect_user\":\"http://testapi1.slidev.org:8080/disco/realms/list.do\"}";
    private AdminExceptionResolver resolver;
    
    @Before
    public void init() {
        resolver = new AdminExceptionResolver();
        
    }
    
    @Test
    public void testNoSessionHaveCookie() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HttpClientErrorException exception = Mockito.mock(HttpClientErrorException.class);
        Mockito.when(exception.getStatusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        
        RESTClient rest = Mockito.mock(RESTClient.class);
        JsonParser parser = new JsonParser();
        JsonObject jsonArray = parser.parse(RAW_JSON).getAsJsonObject();
        Mockito.when(rest.sessionCheck(null)).thenReturn(jsonArray);
        resolver.setRESTClient(rest);
        
        ModelAndView model = resolver.resolveException(request, response, null, exception);
        Assert.assertTrue("View is not instance of RedirectView", model.getView() instanceof RedirectView);
        
        RedirectView view = (RedirectView) model.getView();
        
        String expectedUrl = "http://testapi1.slidev.org:8080/disco/realms/list.do?RelayState=http";
        Assert.assertTrue("Redirect url is not correct", view.getUrl().startsWith(expectedUrl));
    }
}
