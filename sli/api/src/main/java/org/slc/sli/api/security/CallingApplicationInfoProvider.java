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


package org.slc.sli.api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

/**
 * Facade that simplifies obtaining application details from the security context
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@Component
public class CallingApplicationInfoProvider {
    
    /**
     * Returns the application clientId of the current request if authentication was performed using
     * oauth. Otherwise returns null.
     */
    public String getClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof OAuth2Authentication)) {
            return null;
        }
        return ((OAuth2Authentication) auth).getClientAuthentication().getClientId();
    }
}
