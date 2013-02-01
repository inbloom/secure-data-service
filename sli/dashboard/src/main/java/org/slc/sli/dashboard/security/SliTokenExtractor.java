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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.Token;
import org.scribe.utils.Preconditions;

/**
 * Class to get the token from the authorize response.
 * @author jnanney
 *
 */
public class SliTokenExtractor  implements AccessTokenExtractor {
    private static final String TOKEN_REGEX = "\"access_token\":\"([^&\"]+)\"";

    @Override
    public Token extract(String response) {
        Preconditions.checkEmptyString(response, "Response body is incorrect. Can't extract a token from an empty string");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(response).getAsJsonObject();

        if (json.has("error")) {
            throw new OAuthException(json.get("error_description").getAsString());
        }

        return new Token(json.get("access_token").getAsString(), "", response);
    }

}
