package org.slc.sli.api.resources.config;

import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.wadl.helpers.WadlHandler;
import org.slc.sli.modeling.wadl.helpers.WadlHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Walks a wadl and returns the resource end points
 *
 * @author srupasinghe
 *
 */
public class ResourceWadlHandler implements WadlHandler {
    private Map<Integer, List<Pair<String, String>>> resourceEnds = new HashMap<Integer, List<Pair<String, String>>>();

    private void computeURI(final Resource resource, final Resources resources, final Application app,
                                          final Stack<Resource> ancestors) {
        final List<String> steps = WadlHelper.toSteps(resource, ancestors);

        if (steps.contains("custom") || steps.contains("aggregates")) return;

        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final String step : steps) {
            if (WadlHelper.isVersion(step)) {
                // Ignore
            } else {
                if (first) {
                    first = false;
                } else {
                    sb.append("/");
                }
                sb.append(step);
            }
        }

        addURI(steps.size()-1, sb.toString(), resource.getResourceClass());
    }

    private void addURI(int key, String uri, String resourceClass) {
        Pair<String, String> pair = Pair.of(uri, resourceClass);

        if (resourceEnds.containsKey(key)) {
            resourceEnds.get(key).add(pair);
        } else {
            List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
            list.add(pair);
            resourceEnds.put(key, list);
        }
    }

    @Override
    public void beginApplication(Application application) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beginResource(final Resource resource, final Resources resources, final Application app,
                              final Stack<Resource> ancestors) {
        computeURI(resource, resources, app, ancestors);
    }

    @Override
    public void endApplication(Application application) {
        // TODO Auto-generated method stub

    }

    @Override
    public void endResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors) {
        // Ignore
    }

    @Override
    public void method(Method method, Resource resource, Resources resources, Application application,
                       Stack<Resource> ancestors) {
    }


    public Map<Integer, List<Pair<String, String>>> getResourceEnds() {
        return resourceEnds;
    }
}

