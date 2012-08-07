package org.slc.sli.api.resources.config;

import com.sun.jersey.api.core.DefaultResourceConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.api.resources.generic.GenericResource;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.wadl.helpers.WadlWalker;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;


/**
 * Custom resource config for registering resources at runtime
 *
 * @author srupasinghe
 */

@Component
public class ResourceRegisterConfig extends DefaultResourceConfig {

    private static final String WADL = "/wadl/base_wadl.wadl";
    private static final String PREFIX = "generic/";
    private static final Integer ONE_PART = 1;

    public ResourceRegisterConfig() {
        super();
    }

    @PostConstruct
    public void init() {
        Map<Integer, List<Pair<String, String>>> resources = getResources();

        addOnePartResources(resources.get(ONE_PART));
    }

    protected void addOnePartResources(List<Pair<String, String>> resources) {
        for (Pair<String, String> resourceTemplate : resources) {
            try {
                Class resourceClass = GenericResource.class;
                if (resourceTemplate.getRight() != null && !resourceTemplate.getRight().isEmpty()) {
                    resourceClass = Class.forName(resourceTemplate.getRight());
                }

                getExplicitRootResources().put(PREFIX + resourceTemplate.getLeft(), resourceClass);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    protected Map<Integer, List<Pair<String, String>>> getResources() {
        ResourceWadlHandler handler = new ResourceWadlHandler();
        Application app = WadlReader.readApplication(getClass().getResourceAsStream(WADL));
        WadlWalker walker = new WadlWalker(handler);

        walker.walk(app);

        return handler.getResourceEnds();
    }
}
