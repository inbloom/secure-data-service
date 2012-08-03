package org.slc.sli.api.resources.generic.util;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

/**
 * @author jstokes
 */
@Component
public class RestResourceHelper implements ResourceHelper {
    private static final String MATCH_KEY = "resource";

    @Override
    public String grabResource(final String uri, final ResourceTemplate template) {
        final UriTemplate uriTemplate = new UriTemplate(template.getTemplate());
        final Map<String, String> matchList = uriTemplate.match(uri);
        return matchList.get(MATCH_KEY);
    }
}
