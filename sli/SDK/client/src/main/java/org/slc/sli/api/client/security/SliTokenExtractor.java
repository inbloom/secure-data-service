package org.slc.sli.api.client.security;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.Token;
import org.scribe.utils.Preconditions;

/**
 * Class to get the token from the authorize response.
 * @author jnanney
 *
 */
public class SliTokenExtractor  implements AccessTokenExtractor {
//    private static final Logger LOG = LoggerFactory.getLogger(SliTokenExtractor.class);

    @Override
    public Token extract(String response) {
        Preconditions.checkEmptyString(response, "Response body is incorrect. Can't extract a token from an empty string");
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(response).getAsJsonObject();
//        DE260 - commenting out possibly sensitive data
//        LOG.debug("Response to extract token from - {}", json);
        return new Token(json.get("access_token").getAsString(), "", response);
    }

}
