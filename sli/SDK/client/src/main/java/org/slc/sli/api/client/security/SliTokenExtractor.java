package org.slc.sli.api.client.security;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
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
    
    @SuppressWarnings("finally")
    @Override
    public Token extract(String response) {
        Preconditions.checkEmptyString(response,
                "Response body is incorrect. Can't extract a token from an empty string");
        JsonNode root;
        try {
            root = mapper.readTree(response);
            JsonNode token = root.findValue("access_token");
            return new Token(token.asText(), "", response);
            
        } finally {
            return null;
        }
    }
}
