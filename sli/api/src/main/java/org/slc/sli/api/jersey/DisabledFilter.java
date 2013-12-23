package org.slc.sli.api.jersey;


import com.google.common.collect.Sets;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.generic.MethodNotAllowedException;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.security.context.ContextValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.PathSegment;
import java.util.List;

/**
 * Resources that have PUT or PATCH disabled in BasicDefinitionStore are filtered out here
 *
 * ben morgan
 */
@Component
public class DisabledFilter implements ContainerRequestFilter {

    @Autowired
    private ContextValidator contextValidator;


    @Autowired
    private EntityDefinitionStore entityDefinitionStore;

    @Autowired
    private ResourceHelper resourceHelper;


    @Override
    public ContainerRequest filter(ContainerRequest request) {

        //skip this filter of the request is not a put and not a patch
        if(!request.getMethod().equalsIgnoreCase("put") && !request.getMethod().equalsIgnoreCase("patch")) {
            return request;
        }

        //always allow access to put and patch on custom data
        if(resourceHelper.resolveResourcePath("/rest/"+request.getPath(), ResourceTemplate.CUSTOM)) {
            return request;
        }

        if(resourceHelper.resolveResourcePath("/rest/"+request.getPath(), ResourceTemplate.UNVERSIONED_CUSTOM)) {
            return request;
        }

        //check each segment, find the associated resource and verify that put or patch is enabled
        List<PathSegment> segs = request.getPathSegments();
        segs = contextValidator.cleanEmptySegments(segs);

        for(PathSegment seg : segs) {
            EntityDefinition entityDef = entityDefinitionStore.lookupByResourceName(seg.getPath());

            if(entityDef != null) {
                if(request.getMethod().equalsIgnoreCase("put") && !entityDef.supportsPut()) {
                    throw new MethodNotAllowedException(Sets.newHashSet(new String[]{}));
                }
                if(request.getMethod().equalsIgnoreCase("patch") && !entityDef.supportsPatch()) {
                    throw new MethodNotAllowedException(Sets.newHashSet(new String[] {}));
                }
            }
        }

        return request;
    }

}

