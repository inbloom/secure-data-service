package org.slc.sli.dashboard.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.slc.sli.dashboard.security.SLIAuthenticationEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author ychen
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml" })

public class SLIAuthenticationEntryPointTest {

    @Autowired
    ApplicationContext applicationContext;

    private static HttpServletRequest request;
    private static HttpServletResponse response;

    private static final Logger LOG = LoggerFactory.getLogger(SLIAuthenticationEntryPoint.class);

    @Before
    public void setUp() throws Exception {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void testIsSecuredRequest() throws Exception {
        //Test all four scenarios
        LOG.debug("[SLCAuthenticationEntryPointTest]Secure Protocol with local environment, return FALSE");
        when(request.getServerName()).thenReturn("local.slidev.org");
        when(request.isSecure()).thenReturn(true);
        assertFalse(SLIAuthenticationEntryPoint.isSecureRequest(request));

        LOG.debug("[SLCAuthenticationEntryPointTest]Non-Secure Protocol with local environment, return FALSE");
        when(request.getServerName()).thenReturn("local.slidev.org");
        when(request.isSecure()).thenReturn(false);
        assertFalse(SLIAuthenticationEntryPoint.isSecureRequest(request));

        LOG.debug("[SLCAuthenticationEntryPointTest]Non-Secure Protocol with non-local environment, return FALSE");
        when(request.getServerName()).thenReturn("rcdashboard.slidev.org");
        when(request.isSecure()).thenReturn(false);
        assertFalse(SLIAuthenticationEntryPoint.isSecureRequest(request));

//<<<<<<< HEAD:sli/dashboard/src/test/java/org/slc/sli/dashboard/security/SLIAuthenticationEntryPointTest.java
    	LOG.debug("[SLCAuthenticationEntryPointTest]Non-Secure Protocol with local environment, return FALSE");
    	when(request.getServerName()).thenReturn("local.slidev.org");
    	when(request.isSecure()).thenReturn(false);
    	assertFalse(SLIAuthenticationEntryPoint.isSecureRequest(request));

    	LOG.debug("[SLCAuthenticationEntryPointTest]Non-Secure Protocol with non-local environment, return FALSE");
    	when(request.getServerName()).thenReturn("rcdashboard.slidev.org");
    	when(request.isSecure()).thenReturn(false);
    	assertFalse(SLIAuthenticationEntryPoint.isSecureRequest(request));

    	LOG.debug("[SLCAuthenticationEntryPointTest]Secure Protocol with non-local environment, return TRUE");
    	when(request.getServerName()).thenReturn("rcdashboard.slidev.org");
    	when(request.isSecure()).thenReturn(true);
    	assertTrue(SLIAuthenticationEntryPoint.isSecureRequest(request));
//=======
     /*   LOG.debug("[SLCAuthenticationEntryPointTest]Secure Protocol with non-local environment, return TRUE");
        when(request.getServerName()).thenReturn("rcdashboard.slidev.org");
        when(request.isSecure()).thenReturn(true);
        assertTrue(SLIAuthenticationEntryPoint.isSecureRequest(request));
     */
//>>>>>>> b7ea980c37176f6b295f57b8ab1040ceb152f4b2:sli/dashboard/src/test/java/org/slc/sli/security/SLIAuthenticationEntryPointTest.java
    }
}
