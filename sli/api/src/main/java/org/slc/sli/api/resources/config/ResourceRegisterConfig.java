package org.slc.sli.api.resources.config;

import com.sun.jersey.api.core.DefaultResourceConfig;
import org.slc.sli.api.resources.generic.GenericResource;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.wadl.helpers.WadlWalker;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;


/**
 * Custom resource config for registering resources at runtime
 *
 * @author srupasinghe
 */

@Component
public class ResourceRegisterConfig extends DefaultResourceConfig {

    private static final String WADL = "/sliModel/SLI.wadl";
    private static final String PREFIX = "generic/";
    private static final Integer ONE_PART = 1;

    public ResourceRegisterConfig() {
        super();
    }

    @PostConstruct
    public void init() {
        Map<Integer, List<String>> resources = getResources();

        addOnePartResources(resources.get(ONE_PART));
    }

    protected void addOnePartResources(List<String> resources) {
        for (String resource : resources) {
            getExplicitRootResources().put(PREFIX + resource, GenericResource.class);
        }
    }

    protected Map<Integer, List<String>> getResources() {
        ResourceWadlHandler handler = new ResourceWadlHandler();
        Application app = WadlReader.readApplication(getClass().getResourceAsStream(WADL));
        WadlWalker walker = new WadlWalker(handler);

        walker.walk(app);

        return handler.getResourceEnds();
    }
}
