package org.slc.sli.security;

public class SecurityResponse {
	boolean authenticated;
	String 	redirect_user;
	
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
	
	public String getRedirect_user() {
		return redirect_user;
	}
	
	public void setRedirect_user(String redirect_user) {
		this.redirect_user = redirect_user;
	}
}
