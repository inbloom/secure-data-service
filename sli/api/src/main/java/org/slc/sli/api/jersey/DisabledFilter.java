package org.slc.sli.api.jersey;


import com.google.common.collect.Sets;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.generic.MethodNotAllowedException;
import org.slc.sli.api.security.context.ContextValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.PathSegment;
import java.util.List;


@Component
public class DisabledFilter implements ContainerRequestFilter {

    @Autowired
    private ContextValidator contextValidator;


    @Autowired
    private EntityDefinitionStore entityDefinitionStore;


    @Override
    public ContainerRequest filter(ContainerRequest request) {

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

