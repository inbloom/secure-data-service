package org.slc.sli.api.cache;

import java.util.Arrays;
import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.domain.enums.Right;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests
 * 
 * @author dkornishev
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@Component
public class SessionCacheTest {
	
	private static final String TOKEN  = "token";
    private static final String TOKEN2 = "hurrah";
	
	@Resource
	private SessionCache sessions;

	@Test
	public void testPut() {
		Authentication userAuthentication = new PreAuthenticatedAuthenticationToken(new SLIPrincipal("1234"), "auth", Arrays.asList(Right.FULL_ACCESS));
		sessions.put(TOKEN, new OAuth2Authentication(new ClientToken("the", "ordinary", Collections.singleton("man")), userAuthentication));
		Assert.assertNotNull(sessions.get(TOKEN));
	}
	
	@Test
	@Ignore    // ignored untill better times.  Intermitent failures in CI
	public void testRemove() {
		Authentication userAuthentication = new PreAuthenticatedAuthenticationToken(new SLIPrincipal("1234"), "auth", Arrays.asList(Right.FULL_ACCESS));
		sessions.put(TOKEN2, new OAuth2Authentication(new ClientToken("the", "ordinary", Collections.singleton("man")), userAuthentication));
		Assert.assertNotNull(sessions.get(TOKEN2));
		sessions.remove(TOKEN2);
		Assert.assertNull(sessions.get(TOKEN2));
	}
	
}
