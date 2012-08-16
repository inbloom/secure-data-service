package org.slc.sli.api.resources.generic.util;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author jstokes
 */
@Component
public class RestResourceHelper implements ResourceHelper {
    private static final String MATCH_KEY = "resource";

    private static final String ID_KEY = "resource";
    @Override
    public String grabResource(final String uri, final ResourceTemplate template) {
        final UriTemplate uriTemplate = new UriTemplate(template.getTemplate());
        final Map<String, String> matchList = uriTemplate.match(uri);
        return matchList.get(MATCH_KEY);
    }

    @Override
    public String getResourceName(final UriInfo uriInfo, final ResourceTemplate template) {
        return grabResource(uriInfo.getRequestUri().toString(), template);
    }

    @Override
    public ArrayList<String> getIds(UriInfo uriInfo,ResourceTemplate template) {
        final  UriTemplate uriTemplate = new UriTemplate(template.getTemplate());
        final Map<String,String> matchList = uriTemplate.match(uri);
        ArrayList<String> ids = new ArrayList<String>();
       for(String id : matchList.get(ID_KEY).split(",")) {
           ids.add(id);
       }
       return ids;
    }
}
