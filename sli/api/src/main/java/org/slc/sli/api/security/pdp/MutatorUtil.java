package org.slc.sli.api.security.pdp;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for common mutation operations
 */
public class MutatorUtil {
    public static Map<String, String> getParameterMap(String queryParameters) {
        Map<String, String> parameters = new HashMap<String, String>();

        for (String query : queryParameters.split("&")) {
            String[] keyAndValue = query.split("=", 2);
            if (keyAndValue.length == 2) {
                parameters.put(keyAndValue[0], keyAndValue[1]);
            }
        }
        return parameters;
    }
}
