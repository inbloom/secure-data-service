package org.slc.sli.api.security.pdp;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.PathSegment;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;

/**
 * @author dkornishev
 */
@Component
public class PolicyEnforcer {

    @Resource
    private ContextResolverStore store;

    @Resource
    private EdOrgHelper edorgHelper;

    @Resource
    private ContextInferranceHelper inferer;

    public void enforce(Authentication auth, ContainerRequest request) {

        if (request.getMethod() == "POST") {
            return;
        }

        List<PathSegment> seg = request.getPathSegments();

        SLIPrincipal user = (SLIPrincipal) auth.getPrincipal();
        if (seg.get(0).getPath().equals("v1")) {
            if (seg.size() < 3) {
                request.getProperties().put("requestedPath", request.getPath());
                String newPath = inferer.getInferredUri(seg.get(1).getPath(), user.getEntity());
                if (newPath != null) {
                    String parameters = request.getRequestUri().getQuery();
                    info("URI Rewrite from->to: {} -> {}", request.getPath(), newPath);
                    request.setUris(request.getBaseUri(), request.getBaseUriBuilder().path(seg.get(0).getPath()).path(newPath).replaceQuery(parameters).build());
                }
            }

            seg = request.getPathSegments();

            // Obtain resolver
            String entityName = seg.get(1).getPath();
            String entityId = null;
            if (seg.size() > 2) {
                entityId = seg.get(2).getPath();
            }

            List<String> ids = Collections.emptyList();

//            if ("schools".equals(entityName)) {
//                ids = edorgHelper.getAllEdOrgs(user.getEntity());
//            } else {
//                EntityContextResolver resolver = store.findResolver(user.getEntity().getType(), entityName.replaceAll("s$", ""));
//                ids = resolver.findAccessible(user.getEntity());
//            }
//
//            if (entityId != null && ids.contains(entityId)) {
//                info("Access Allowed: {}", request.getPath());
//            } else {
//                warn("Access Denied: {}", request.getPath());
//                //throw new AccessDeniedException("No context");
//            }
        }
    }

}
