/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
        INVALID_REQUEST, INVALID_GRANT, UNAUTHORIZED_CLIENT, UNSUPPORTED_GRANT_TYPE, INVALID_SCOPE, INVALID_CLIENT, INVALID_TOKEN;
        
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
