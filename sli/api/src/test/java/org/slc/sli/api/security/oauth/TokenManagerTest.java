package org.slc.sli.api.security.oauth;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.domain.enums.Right;

/**
 * Test class for the TokenManager
 * 
 * @author jnanney
 * @author shalka
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class TokenManagerTest {

	private static final String ACCESS_TOKEN_ONE_VALUE = "test-refresh-token-one";
	private static final String ACCESS_TOKEN_ONE_TYPE = "test-type";
	private static final String REFRESH_TOKEN_ONE_VALUE = "test-refresh-token-one";
	private static final String BAD_TOKEN_VALUE = "bad-token";

	@Spy
	private EntityRepository repo;

	private Date expirationDate;

	@Mock
	private EntityService service;

	@Mock
	private RolesToRightsResolver rolesToRightsResolver;

	@InjectMocks
	private SliTokenStore tokenManager;

	private Set<GrantedAuthority> educatorAuthorities;

	@Before
	public void setUp() {
		repo = new MockRepo();
		tokenManager = new SliTokenStore();
		educatorAuthorities = new HashSet<GrantedAuthority>();
		educatorAuthorities.add(Right.AGGREGATE_READ);
		educatorAuthorities.add(Right.READ_GENERAL);
		service = Mockito.mock(EntityService.class);
		rolesToRightsResolver = Mockito.mock(RolesToRightsResolver.class);
		tokenManager.setService(service);

		Mockito.when(service.create(Mockito.isA(EntityBody.class))).thenReturn(
				"id");
		Mockito.when(
				rolesToRightsResolver.resolveRoles("test-realm",
						Arrays.asList(new String[] { "Educator" })))
				.thenReturn(educatorAuthorities);
		expirationDate = new Date();
		expirationDate.setTime(expirationDate.getTime() + 60 * 60 * 1000L);

		Map<String, Object> refreshToken = new HashMap<String, Object>();
		refreshToken.put("value", REFRESH_TOKEN_ONE_VALUE);
		refreshToken.put("expiration", expirationDate);

		Map<String, Object> accessToken = new HashMap<String, Object>();
		accessToken.put("value", ACCESS_TOKEN_ONE_VALUE);
		accessToken.put("expiration", expirationDate);
		accessToken.put("tokenType", ACCESS_TOKEN_ONE_TYPE);
		accessToken.put("refreshToken", refreshToken);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("accessToken.refreshToken.value", REFRESH_TOKEN_ONE_VALUE);
		data.put("accessToken.refreshToken.expiration", expirationDate);
		data.put("accessToken.value", ACCESS_TOKEN_ONE_VALUE);
		data.put("accessToken.tokenType", ACCESS_TOKEN_ONE_TYPE);
		data.put("accessToken.expiration", expirationDate);
		data.put("userAuthn.userId", "user-one");
		data.put("userAuthn.userRealm", "test-realm");
		data.put("userAuthn.userRoles",
				Arrays.asList(new String[] { "Educator" }));
		data.put("userAuthn.externalId", "user-one-external-id");
		data.put("accessToken", accessToken);

		MockitoAnnotations.initMocks(this);

		repo.create("oauthSession", data);
	}

	@Test
	public void testStoreAccessToken() {
		Entity principalEntity = Mockito.mock(Entity.class);
		Authentication userAuthentication = Mockito.mock(Authentication.class);
		SLIPrincipal principal = Mockito.mock(SLIPrincipal.class);

		OAuth2AccessToken accessToken = new OAuth2AccessToken("Test-token");
		ClientToken clientToken = new ClientToken("test-client-id",
				"test-client-secret", null);
		OAuth2Authentication auth = new OAuth2Authentication(clientToken,
				userAuthentication);

		Mockito.when(principalEntity.getEntityId()).thenReturn(
				"mongo-entity-id");
		Mockito.when(principal.getId()).thenReturn("test-id");
		Mockito.when(principal.getRealm()).thenReturn("test-realm");
		Mockito.when(principal.getRoles()).thenReturn(
				Arrays.asList(new String[] { "Educator" }));
		Mockito.when(principal.getExternalId()).thenReturn("test-external-id");
		Mockito.when(principal.getEntity()).thenReturn(principalEntity);
		Mockito.when(auth.getPrincipal()).thenReturn(principal);

		accessToken.setExpiration(expirationDate);
		accessToken.setTokenType("test-type");
		ExpiringOAuth2RefreshToken rt = new ExpiringOAuth2RefreshToken(
				"test-refresh", expirationDate);
		accessToken.setRefreshToken(rt);
		tokenManager.storeAccessToken(accessToken, auth);
	}

	@Test
	public void testReadRefreshToken() {
		ExpiringOAuth2RefreshToken token = tokenManager
				.readRefreshToken(REFRESH_TOKEN_ONE_VALUE);
		assertEquals(expirationDate, token.getExpiration());
		assertEquals("test-refresh-token-one", token.getValue());
	}

	@Test
	public void testReadAuthenticationWithAccessToken() {
		OAuth2Authentication auth = tokenManager
				.readAuthentication(new OAuth2AccessToken(
						ACCESS_TOKEN_ONE_VALUE));
		SLIPrincipal userAuth = (SLIPrincipal) auth.getUserAuthentication()
				.getPrincipal();
		assertEquals("user-one", userAuth.getId());
	}

	@Test
	public void testReadAuthenticationWithRefreshToken() {
		OAuth2Authentication auth = tokenManager
				.readAuthentication(new ExpiringOAuth2RefreshToken(
						REFRESH_TOKEN_ONE_VALUE, expirationDate));
		SLIPrincipal userAuth = (SLIPrincipal) auth.getUserAuthentication()
				.getPrincipal();
		assertEquals("user-one", userAuth.getId());
	}

	@Test
	public void testReadNonExistentAccessToken() {
		OAuth2AccessToken token = tokenManager.readAccessToken(BAD_TOKEN_VALUE);
		assertEquals(null, token);
	}

	@Test
	public void testReadAccessToken() {
		OAuth2AccessToken token = tokenManager
				.readAccessToken(ACCESS_TOKEN_ONE_VALUE);
		assertEquals(ACCESS_TOKEN_ONE_VALUE, token.getValue());
		assertEquals(ACCESS_TOKEN_ONE_TYPE, token.getTokenType());
	}

	@Test
	public void testStoreRefreshToken() {
		Entity principalEntity = Mockito.mock(Entity.class);
		Authentication userAuthentication = Mockito.mock(Authentication.class);
		SLIPrincipal principal = Mockito.mock(SLIPrincipal.class);

		ClientToken clientToken = new ClientToken("test-client-id",
				"test-client-secret", null);
		OAuth2Authentication auth = new OAuth2Authentication(clientToken,
				userAuthentication);

		Mockito.when(principalEntity.getEntityId()).thenReturn(
				"mongo-entity-id");
		Mockito.when(principal.getId()).thenReturn("test-id");
		Mockito.when(principal.getRealm()).thenReturn("test-realm");
		Mockito.when(principal.getRoles()).thenReturn(
				Arrays.asList(new String[] { "Educator" }));
		Mockito.when(principal.getExternalId()).thenReturn("test-external-id");
		Mockito.when(principal.getEntity()).thenReturn(principalEntity);
		Mockito.when(auth.getPrincipal()).thenReturn(principal);

		tokenManager.storeRefreshToken(new ExpiringOAuth2RefreshToken("token",
				new Date()), auth);
	}

}
