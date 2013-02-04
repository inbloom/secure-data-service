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


package org.slc.sli.dashboard.security;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

/**
 * Class has the information needed for integrating with OAuth to which implement for the Scribe library.
 * 
 * @author jnanney
 *
 */
public class SliApi extends DefaultApi20 {
    
    private static String apiUrl;
    private String authorizeUrl = apiUrl.replaceAll("/$", "") + "/api/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s";

    @Override
    public String getAccessTokenEndpoint() {
        return apiUrl.replaceAll("/$", "") + "/api/oauth/token?grant_type=authorization_code";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        Preconditions.checkValidUrl(config.getCallback(), "Must provide a valid url as callback.");
        
        return String.format(authorizeUrl, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
    }
    
    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
      return new SliTokenExtractor();
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public static void setBaseUrl(String baseUrl) {
        apiUrl = baseUrl;
    }

}
