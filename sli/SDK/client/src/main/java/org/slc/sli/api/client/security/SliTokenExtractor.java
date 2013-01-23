/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.api.client.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.Token;
import org.scribe.utils.Preconditions;

/**
 * Class to get the token from the authorize response.
 * 
 * @author jnanney
 * 
 */
public class SliTokenExtractor implements AccessTokenExtractor {
    private ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public Token extract(String response) {
        Preconditions.checkEmptyString(response,
                "Response body is incorrect. Can't extract a token from an empty string");
        JsonNode root;
        try {
            root = mapper.readTree(response);
            JsonNode token = root.findValue("access_token");
            return new Token(token.asText(), "", response);
            
        } catch (Exception e) {
            return null;
        }
    }
}
