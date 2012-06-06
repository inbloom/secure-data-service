package org.slc.sli.api.security.oauth;

/**
 *
 *
 */
public class OAuthAccessException extends Exception {
    
    /**
     *
     */
    private static final long serialVersionUID = -326326587881648221L;
    
    /**
     * OAuth errors, as defined in section 5.2
     * 
     */
    public enum OAuthError {
        INVALID_REQUEST, INVALID_GRANT, UNAUTHORIZED_CLIENT, UNSUPPORTED_GRANT_TYPE, INVALID_SCOPE;
        
        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }
    
    private OAuthError type;
    private String state;
    
    public OAuthAccessException(OAuthError type, String description) {
        super(description);
        this.type = type;
    }
    
    public OAuthAccessException(OAuthError type, String description, String state) {
        this(type, description);
        this.state = state;
    }
    
    public OAuthError getType() {
        return type;
    }
    
    public String getState() {
        return state;
    }
    
}
