package org.slc.sli.api.security.oauth;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.json.JSONObject;
import org.springframework.security.oauth2.provider.ClientDetails;

public class ApplicationDetails implements ClientDetails {

	private String clientId;
	private String clientSecret;
	private String redirectUri;
	private boolean isSecretRequired;
	private boolean isScoped;
	private List<String> myScope;
	private List<String> authorizedGrantTypes;
	private List<GrantedAuthority> myAuthorities;

	/**
	 * Default constructor for the ApplicationDetails class.
	 */
	public ApplicationDetails() {

	}

	/**
	 * Constructor for creating application details from a Mongo JSON object.
	 * 
	 * @param appFromMongo
	 *            Raw JSON object returned from MongoDB.
	 */
	public ApplicationDetails(JSONObject appFromMongo) {
		// iterate over JSONObject keys
		// assign value associated with each key in corresponding application
		// fields
	}

	@Override
	public String getClientId() {
		return clientId;
	}

	/**
	 * Sets the 'clientId' field.
	 * 
	 * @param newClientId
	 *            New value for 'clientId' field
	 */
	public void setClientId(String newClientId) {
		this.clientId = newClientId;
	}

	@Override
	public boolean isSecretRequired() {
		return isSecretRequired;
	}

	/**
	 * Sets the 'isSecretRequired' field.
	 * 
	 * @param newIsSecretRequired
	 *            New value for 'isSecretRequired' field
	 */
	public void setIsSecretRequired(boolean newIsSecretRequired) {
		this.isSecretRequired = newIsSecretRequired;
	}

	@Override
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * Sets the 'clientSecret' field.
	 * 
	 * @param newClientSecret
	 *            New value for 'clientSecret' field.
	 */
	public void setClientSecret(String newClientSecret) {
		this.clientSecret = newClientSecret;
	}

	@Override
	public boolean isScoped() {
		return isScoped;
	}

	/**
	 * Sets the 'isScoped' field.
	 * 
	 * @param newIsScoped
	 *            New value for 'isScoped' field.
	 */
	public void setIsScoped(boolean newIsScoped) {
		this.isScoped = newIsScoped;
	}

	@Override
	public List<String> getScope() {
		return myScope;
	}

	/**
	 * Sets the 'myScope' field (representing the application's scope).
	 * 
	 * @param newApplicationScope
	 *            New value for 'myScope' field.
	 */
	public void setScope(List<String> newApplicationScope) {
		this.myScope = newApplicationScope;
	}

	@Override
	public List<String> getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	/**
	 * Sets the 'authorizedGrantTypes' field.
	 * 
	 * @param newAuthorizedGrantTypes
	 *            New value for the 'authorizedGrantTypes' field.
	 */
	public void setAuthorizedGrantTypes(List<String> newAuthorizedGrantTypes) {
		this.authorizedGrantTypes = newAuthorizedGrantTypes;
	}

	@Override
	public String getWebServerRedirectUri() {
		return redirectUri;
	}

	/**
	 * Sets the 'redirectUri' field.
	 * 
	 * @param newRedirectUri
	 *            New value for the 'redirectUri' field.
	 */
	public void setWebServerRedirectUri(String newRedirectUri) {
		this.redirectUri = newRedirectUri;
	}

	@Override
	public List<GrantedAuthority> getAuthorities() {
		return myAuthorities;
	}

	/**
	 * Sets the 'myAuthorities' field.
	 * 
	 * @param newGrantedAuthorityList
	 *            New value for the 'myAuthorities' field.
	 */
	public void setAuthorities(List<GrantedAuthority> newGrantedAuthorityList) {
		this.myAuthorities = newGrantedAuthorityList;
	}
}
