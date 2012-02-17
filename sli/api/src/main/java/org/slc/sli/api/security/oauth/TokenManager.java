package org.slc.sli.api.security.oauth;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.RandomValueTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;

/**
 * Responsible for storage and management of access and refresh tokens for OAuth
 * 2.0 implementation.
 * 
 * @author shalka
 */
@Component
public class TokenManager extends RandomValueTokenServices implements
		TokenStore {

	private static final int REFRESH_TOKEN_VALIDITY_SECONDS = 3600;
	private static final int ACCESS_TOKEN_VALIDITY_SECONDS = 900;
	private static final String OAUTH_SESSION_COLLECTION = "oauthSession";

	@Autowired
	private EntityRepository repo;

	@Autowired
	private EntityDefinitionStore store;

	private EntityService service;

	@Autowired
	private RolesToRightsResolver rolesToRightsResolver;

	@PostConstruct
	public void init() {
		EntityDefinition def = store
				.lookupByResourceName(OAUTH_SESSION_COLLECTION);
		setService(def.getService());
		setRefreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
		setAccessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS);
		setSupportRefreshToken(true);
	}

	public void setService(EntityService service) {
		this.service = service;
	}

	// ******* METHODS OVERRIDDEN FROM RandomValueTokenServices *******

	@Override
	public void afterPropertiesSet() throws Exception {
		// don't do anything
	}

	@Override
	public OAuth2AccessToken createAccessToken(
			OAuth2Authentication authentication) throws AuthenticationException {
		ExpiringOAuth2RefreshToken refreshToken = createRefreshToken(authentication);
		OAuth2AccessToken accessToken = createAccessToken(authentication,
				refreshToken);
		storeAccessToken(accessToken, authentication);
		return accessToken;
	}

	@Override
	public OAuth2AccessToken refreshAccessToken(String refreshTokenValue,
			Set<String> scope) throws AuthenticationException {

		// invalidate access token already associated with the refresh token
		removeAccessTokenUsingRefreshToken(refreshTokenValue);

		ExpiringOAuth2RefreshToken refreshToken = readRefreshToken(refreshTokenValue);
		if (refreshToken == null) {
			throw new InvalidGrantException("Invalid refresh token: "
					+ refreshTokenValue);
		} else if (isExpired(refreshToken)) {
			removeRefreshToken(refreshTokenValue);
			throw new InvalidGrantException("Invalid refresh token: "
					+ refreshToken);
		}

		OAuth2Authentication authentication = readAuthentication(refreshToken);
		return createAccessToken(authentication, refreshToken);
	}

	// Not overridden:
	// public OAuth2Authentication loadAuthentication(String accessTokenValue)

	/**
	 * Creates an expiring refresh token and updates the entry for the
	 * OAuth2Authentication object.
	 */
	@Override
	protected ExpiringOAuth2RefreshToken createRefreshToken(
			OAuth2Authentication authentication) {
		ExpiringOAuth2RefreshToken refreshToken;
		String refreshTokenValue = UUID.randomUUID().toString();
		refreshToken = new ExpiringOAuth2RefreshToken(refreshTokenValue,
				new Date(System.currentTimeMillis()
						+ (REFRESH_TOKEN_VALIDITY_SECONDS * 1000L)));
		storeRefreshToken(refreshToken, authentication);
		return refreshToken;
	}

	/**
	 * Creates an access token, stores the refresh token into the access token,
	 * and updates the entry for the OAuth2Authentication object.
	 */
	@Override
	protected OAuth2AccessToken createAccessToken(
			OAuth2Authentication authentication, OAuth2RefreshToken refreshToken) {
		String tokenValue = UUID.randomUUID().toString();
		OAuth2AccessToken token = new OAuth2AccessToken(tokenValue);
		token.setExpiration(new Date(System.currentTimeMillis()
				+ (ACCESS_TOKEN_VALIDITY_SECONDS * 1000L)));
		token.setRefreshToken(refreshToken);
		token.setScope(authentication.getClientAuthentication().getScope());
		storeAccessToken(token, authentication);
		return token;
	}

	// ******* METHODS OVERRIDDEN FROM RandomValueTokenServices *******
	// ******* METHODS OVERRIDDEN FROM TokenStore *********************

	/**
	 * Finds the OAuth2Authentication object in the oauthSession collection that
	 * corresponds to the specified Access Token. Assume that, if an Access
	 * Token exists, and it is valid, then the user has already authenticated.
	 */
	@Override
	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		Iterable<Entity> results = repo.findByQuery(
				OAUTH_SESSION_COLLECTION,
				new Query(Criteria.where("body.accessToken.value").is(
						token.getValue())), 0, 1);
		if (results != null) {
			for (Entity oauth2Session : results) {
				Map<String, Object> body = oauth2Session.getBody();
				Date accessTokenExpiration = (Date) body
						.get("accessToken.expiration");

				// make sure the access token hasn't expired
				if (!(System.currentTimeMillis() > accessTokenExpiration
						.getTime())) {
					String clientId = (String) body.get("clientAuthn.clientId");
					String clientSecret = (String) body
							.get("clientAuthn.clientSecret");
					Set<String> clientScope = new HashSet<String>();
					clientScope.add((String) body
							.get("clientAuthn.clientScope"));

					String userId = (String) body.get("userAuthn.userId");
					String userRealm = (String) body.get("userAuthn.userRealm");
					@SuppressWarnings("unchecked")
					List<String> userRoles = (List<String>) body
							.get("userAuthn.userRoles");
					String userExtId = (String) body
							.get("userAuthn.externalId");
					Entity mongoEntityId = (Entity) body
							.get("userAuthn.mongoEntityId");

					SLIPrincipal principal = new SLIPrincipal();
					principal.setId(userId);
					principal.setRealm(userRealm);
					principal.setRoles(userRoles);
					principal.setExternalId(userExtId);
					principal.setEntity(mongoEntityId);

					String samlMessageId = (String) body.get("samlMessageId");

					Set<GrantedAuthority> userAuthorities = rolesToRightsResolver
							.resolveRoles(userRealm, userRoles);

					ClientToken clientAuthentication = new ClientToken(
							clientId, clientSecret, clientScope);
					Authentication userAuthentication = new PreAuthenticatedAuthenticationToken(
							principal, samlMessageId, userAuthorities);

					return new OAuth2Authentication(clientAuthentication,
							userAuthentication);
				} else {
					// should we throw an expired access token exception here?
					// - how gracefully will we handle this in the future?
				}
			}
		}
		return null;
	}

	/**
	 * Finds the OAuth2Authentication object in the oauthSession collection that
	 * corresponds to the specified Refresh Token.
	 */
	@Override
	public OAuth2Authentication readAuthentication(
			ExpiringOAuth2RefreshToken token) {
		Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
				new Query(Criteria.where("body.accessToken.refreshToken.value")
						.is(token.getValue())), 0, 1);
		if (results != null) {
			for (Entity oauth2Session : results) {
				Map<String, Object> body = oauth2Session.getBody();
				Date refreshTokenExpiration = (Date) body
						.get("accessToken.refreshToken.expiration");

				// make sure the refresh token hasn't expired
				if (!(System.currentTimeMillis() > refreshTokenExpiration
						.getTime())) {
					String clientId = (String) body.get("clientAuthn.clientId");
					String clientSecret = (String) body
							.get("clientAuthn.clientSecret");
					Set<String> clientScope = new HashSet<String>();
					clientScope.add((String) body
							.get("clientAuthn.clientScope"));

					String userId = (String) body.get("userAuthn.userId");
					String userRealm = (String) body.get("userAuthn.userRealm");
					@SuppressWarnings("unchecked")
					List<String> userRoles = (List<String>) body
							.get("userAuthn.userRoles");
					String userExtId = (String) body
							.get("userAuthn.externalId");
					Entity mongoEntityId = (Entity) body
							.get("userAuthn.mongoEntityId");

					SLIPrincipal principal = new SLIPrincipal();
					principal.setId(userId);
					principal.setRealm(userRealm);
					principal.setRoles(userRoles);
					principal.setExternalId(userExtId);
					principal.setEntity(mongoEntityId);

					String samlMessageId = (String) body.get("samlMessageId");

					Set<GrantedAuthority> userAuthorities = rolesToRightsResolver
							.resolveRoles(userRealm, userRoles);

					ClientToken clientAuthentication = new ClientToken(
							clientId, clientSecret, clientScope);
					Authentication userAuthentication = new PreAuthenticatedAuthenticationToken(
							principal, samlMessageId, userAuthorities);

					return new OAuth2Authentication(clientAuthentication,
							userAuthentication);
				}
			}
		}
		return null;
	}

	/**
	 * Stores an access token and corresponding OAuth2Authentication object in
	 * the oauthSession collection.
	 */
	@Override
	public void storeAccessToken(OAuth2AccessToken token,
			OAuth2Authentication authentication) {
		EntityBody clientAuthentication = mapClientAuthentication(authentication
				.getClientAuthentication());
		EntityBody userAuthentication = mapUserAuthentication(authentication
				.getUserAuthentication());
		EntityBody accessToken = mapAccessToken(token);

		EntityBody oauth2Session = new EntityBody();
		oauth2Session.put("clientAuthn", clientAuthentication);
		oauth2Session.put("userAuthn", userAuthentication);
		oauth2Session.put("accessToken", accessToken);

		service.create(oauth2Session);
	}

	/**
	 * Determines if there is a valid Access Token in the oauthSessions
	 * collection that matches the specified Access Token value.
	 */
	@Override
	public OAuth2AccessToken readAccessToken(String tokenValue) {
		Iterable<Entity> results = repo.findByQuery(
				OAUTH_SESSION_COLLECTION,
				new Query(Criteria.where("body.accessToken.value").is(
						tokenValue)), 0, 1);
		if (results != null) {
			for (Entity oauth2Session : results) {
				Map<String, Object> oauthSessionBody = oauth2Session.getBody();

				@SuppressWarnings("unchecked")
				Map<String, Object> accessToken = (Map<String, Object>) oauthSessionBody
						.get("accessToken");

				Date accessTokenExpiration = (Date) accessToken
						.get("expiration");

				// make sure the access token hasn't expired
				if (!isTokenExpired(accessTokenExpiration)) {
					OAuth2AccessToken result = new OAuth2AccessToken(tokenValue);
					result.setExpiration((Date) accessToken.get("expiration"));
					result.setTokenType((String) accessToken.get("tokenType"));

					@SuppressWarnings("unchecked")
					Map<String, Object> refreshToken = (Map<String, Object>) accessToken
							.get("refreshToken");

					String refreshTokenValue = (String) refreshToken
							.get("value");
					Date refreshTokenExpiration = (Date) refreshToken
							.get("expiration");

					ExpiringOAuth2RefreshToken rt = new ExpiringOAuth2RefreshToken(
							refreshTokenValue, refreshTokenExpiration);
					result.setRefreshToken(rt);
					return result;
				}
			}
		}
		return null;
	}

	/**
	 * Removes the Access Token's validation by expiring it immediately.
	 */
	@Override
	public void removeAccessToken(String tokenValue) {
		Iterable<Entity> results = repo.findByQuery(
				OAUTH_SESSION_COLLECTION,
				new Query(Criteria.where("body.accessToken.value").is(
						tokenValue)), 0, 1);
		if (results != null) {
			for (Entity oauth2Session : results) {
				@SuppressWarnings("unchecked")
				Map<String, Object> accessToken = (Map<String, Object>) oauth2Session
						.getBody().get("accessToken");

				Date accessTokenExpiration = (Date) accessToken
						.get("expiration");

				if (!isTokenExpired(accessTokenExpiration)) {
					Map<String, Object> oauthSession = (Map<String, Object>) oauth2Session
							.getBody();

					accessToken.put("expiration", new Date());
					service.update(oauth2Session.getEntityId(),
							(EntityBody) oauthSession);
				}
			}
		}
	}

	/**
	 * Performs a lookup on the OAuth2Authentication object, and stores the
	 * Refresh Token into the corresponding Access Token.
	 */
	@Override
	public void storeRefreshToken(ExpiringOAuth2RefreshToken refreshToken,
			OAuth2Authentication authentication) {
		SLIPrincipal principal = (SLIPrincipal) authentication
				.getUserAuthentication().getPrincipal();
		if (principal != null) {
			Entity oauthSession = repo.find(OAUTH_SESSION_COLLECTION, principal
					.getEntity().getEntityId());
			if (oauthSession != null) {
				Map<String, Object> body = oauthSession.getBody();
				Map<String, Object> rt = new HashMap<String, Object>();
				rt.put("value", refreshToken.getValue());
				rt.put("expiration", refreshToken.getExpiration());
				@SuppressWarnings("unchecked")
				Map<String, Object> accessToken = (Map<String, Object>) body
						.get("accessToken");
				accessToken.put("refreshToken", rt);
				service.update(oauthSession.getEntityId(), (EntityBody) body);
			}
		}
	}

	/**
	 * Determines if there is a Refresh Token in the oauthSessions collection
	 * that matches the specified Refresh Token value.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ExpiringOAuth2RefreshToken readRefreshToken(String tokenValue) {
		Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
				new Query(Criteria.where("body.accessToken.refreshToken.value")
						.is(tokenValue)), 0, 1);
		if (results != null) {
			for (Entity oauthSession : results) {
				Map<String, Object> accessToken = (Map<String, Object>) oauthSession
						.getBody().get("accessToken");
				Map<String, Object> refreshToken = (Map<String, Object>) accessToken
						.get("refreshToken");
				Date expirationDate = (Date) refreshToken.get("expiration");
				return new ExpiringOAuth2RefreshToken(tokenValue,
						expirationDate);
			}
		}
		return null;
	}

	/**
	 * Removes the Refresh Token's validation by expiring it immediately.
	 */
	@Override
	public void removeRefreshToken(String tokenValue) {
		Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
				new Query(Criteria.where("body.accessToken.refreshToken.value")
						.is(tokenValue)), 0, 1);
		if (results != null) {
			for (Entity oauthSession : results) {
				@SuppressWarnings("unchecked")
				Map<String, Object> refreshToken = (Map<String, Object>) oauthSession
						.getBody().get("accessToken.refreshToken");

				Date refreshTokenExpiration = (Date) refreshToken
						.get("expiration");

				if (!isTokenExpired(refreshTokenExpiration)) {
					@SuppressWarnings("unchecked")
					Map<String, Object> accessToken = (Map<String, Object>) oauthSession
							.getBody().get("accessToken");

					refreshToken.put("expiration", new Date());
					accessToken.put("refreshToken", refreshToken);
					oauthSession.getBody().put("accessToken", accessToken);
					service.update(oauthSession.getEntityId(),
							(EntityBody) oauthSession.getBody());
				}
			}
		}
	}

	/**
	 * Removes the Access Token's validation by expiring it immediately (lookup
	 * done on Refresh Token value). This does not remove the refresh token's
	 * validation.
	 */
	@Override
	public void removeAccessTokenUsingRefreshToken(String refreshToken) {
		Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
				new Query(Criteria.where("body.accessToken.refreshToken.value")
						.is(refreshToken)), 0, 1);
		if (results != null) {
			for (Entity oauthSession : results) {
				@SuppressWarnings("unchecked")
				Map<String, Object> access = (Map<String, Object>) oauthSession
						.getBody().get("accessToken");

				@SuppressWarnings("unchecked")
				Map<String, Object> refresh = (Map<String, Object>) oauthSession
						.getBody().get("accessToken.refreshToken");

				Date accessTokenTokenExpiration = (Date) access
						.get("expiration");

				Date refreshTokenTokenExpiration = (Date) refresh
						.get("expiration");

				if (!isTokenExpired(accessTokenTokenExpiration)
						&& !isTokenExpired(refreshTokenTokenExpiration)) {
					access.put("expiration", new Date());
					oauthSession.getBody().put("accessToken", access);
					service.update(oauthSession.getEntityId(),
							(EntityBody) oauthSession.getBody());
				}
			}
		}
	}

	/**
	 * Maps the specified Client Authentication object into an EntityBody for
	 * storage into the oauthSession collection.
	 * 
	 * @param accessToken
	 *            Client Authentication object to be mapped.
	 * @return EntityBody containing relevant values of Client Authentication.
	 */
	private EntityBody mapClientAuthentication(ClientToken clientToken) {
		EntityBody entity = new EntityBody();
		entity.put("clientId", clientToken.getClientId());
		entity.put("clientSecret", clientToken.getClientSecret());
		entity.put("clientScope", clientToken.getScope().toString());
		return entity;
	}

	/**
	 * Maps the specified User Authentication object into an EntityBody for
	 * storage into the oauthSession collection by resolving its type to
	 * SLIPrincipal and calling the appropriate function.
	 * 
	 * @param accessToken
	 *            User Authentication object to be mapped.
	 * @return EntityBody containing relevant values of User Authentication.
	 */
	private EntityBody mapUserAuthentication(Authentication userAuthentication) {
		SLIPrincipal principal = (SLIPrincipal) userAuthentication
				.getPrincipal();
		return mapSliPrincipal(principal);
	}

	/**
	 * Maps the specified SLI Principal object into an EntityBody for storage
	 * into the oauthSession collection (stored in userAuthn field).
	 * 
	 * @param principal
	 *            Resolved user authentication object to be mapped.
	 * @return EntityBody containing relevant values for SLI Principal.
	 */
	private EntityBody mapSliPrincipal(SLIPrincipal principal) {
		EntityBody entity = new EntityBody();
		entity.put("userId", principal.getId());
		entity.put("userRealm", principal.getRealm());
		entity.put("userRoles", principal.getRoles().toString());
		entity.put("externalId", principal.getExternalId());
		entity.put("mongoEntityId", principal.getEntity().getEntityId()
				.toString());
		return entity;
	}

	/**
	 * Maps the specified Access Token into an EntityBody for storage into the
	 * oauthSession collection.
	 * 
	 * @param accessToken
	 *            Access Token to be mapped.
	 * @return EntityBody containing relevant values of Access Token (including
	 *         Refresh Token).
	 */
	private EntityBody mapAccessToken(OAuth2AccessToken accessToken) {
		EntityBody refreshToken = new EntityBody();
		refreshToken.put("value", accessToken.getRefreshToken().getValue());
		refreshToken.put("expiration",
				((ExpiringOAuth2RefreshToken) accessToken.getRefreshToken())
						.getExpiration());

		EntityBody entity = new EntityBody();
		entity.put("value", accessToken.getValue());
		entity.put("expiration", accessToken.getExpiration());
		entity.put("tokenType", accessToken.getTokenType());
		entity.put("refreshToken", refreshToken);
		return entity;
	}

	/**
	 * Returns true if the current time (in ms) is greater than the specified
	 * expiration date (indicating that expiration is true).
	 * 
	 * @param expiration
	 *            Date to be checked.
	 * @return 'true' if expired, 'false' if not expired.
	 */
	private boolean isTokenExpired(Date expiration) {
		return System.currentTimeMillis() > expiration.getTime();
	}

	// ******* METHODS OVERRIDDEN FROM TokenStore *********************

	/**
	 * Method called by SAML consumer. Performs a lookup in Mongo to find
	 * OAuth2Authentication object corresponding to the original SAML message
	 * ID, and stores information about the authenticated user into that object.
	 * 
	 * @param originalMsgId
	 *            Unique identifier of SAML message sent to disco.
	 * @param credential
	 *            String representing Identity Provider issuer.
	 * @param principal
	 *            SLIPrincipal representing the authenticated user.
	 * @return String representing the composed redirect URI with verification
	 *         code and request token as parameters in link.
	 */
	public String userAuthenticated(String originalMsgId, String issuer,
			SLIPrincipal principal) {
		Iterable<Entity> results = repo.findByQuery(OAUTH_SESSION_COLLECTION,
				new Query(Criteria.where("body.samlMessageId")
						.is(originalMsgId)), 0, 1);
		if (results != null) {
			for (Entity oauthSession : results) {
				StringBuilder url = new StringBuilder();
				Map<String, Object> body = oauthSession.getBody();

				EntityBody sliPrincipal = mapSliPrincipal(principal);
				body.put("userAuthn", sliPrincipal);

				service.update(oauthSession.getEntityId(), (EntityBody) body);

				// it might be pertinent to do more in terms of checking the
				// client
				// and user authentication objects for consistency/validity
				@SuppressWarnings("unchecked")
				Map<String, Object> clientAuthentication = (Map<String, Object>) body
						.get("clientAuthn");

				String clientRedirectUri = (String) clientAuthentication
						.get("redirectUri");

				String requestToken = (String) body.get("requestToken");
				String verificationCode = (String) body.get("verificationCode");

				url.append(clientRedirectUri);
				url.append("?requestToken=" + requestToken);
				url.append("&verificationCode=" + verificationCode);
				return url.toString();
			}
		}
		return null;
	}

	/**
	 * Method called by SAML consumer. Performs a lookup in Mongo to find
	 * oauthSession correponding to the requestToken, and stores the SAML
	 * message ID into that object.
	 * 
	 * @param requestToken
	 *            Value of token granted to the application (signifying request
	 *            for access token).
	 * @param messageId
	 *            SAML message unique identifier.
	 */
	public void storeSamlMessageId(String requestToken, String messageId) {
		Iterable<Entity> results = repo
				.findByQuery(OAUTH_SESSION_COLLECTION, new Query(Criteria
						.where("body.requestToken").is(requestToken)), 0, 1);
		if (results != null && messageId.length() > 0) {
			for (Entity oauthSession : results) {
				Map<String, Object> body = oauthSession.getBody();
				body.put("samlMessageId", messageId);
				service.update(oauthSession.getEntityId(), (EntityBody) body);
			}
		}
	}

}
