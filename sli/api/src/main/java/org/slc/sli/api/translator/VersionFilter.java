package org.slc.sli.api.translator;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import org.slc.sli.api.resources.generic.config.ResourceEndPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.SortedSet;

/**
 * Pre-request version filter.
 * Mutates the URI version to the laster major.minor version
 *
 * @author srupasinghe
 */
@Component
public class VersionFilter implements ContainerRequestFilter {

    private static final String REQUESTED_PATH = "requestedPath";

    @Autowired
    private ResourceEndPoint resourceEndPoint;

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {

        List<PathSegment> segments = containerRequest.getPathSegments();

        if (!segments.isEmpty()) {
            String version = segments.get(0).getPath();

            SortedSet<String> minorVersions = resourceEndPoint.getNameSpaceMappings().get(version);

            if ((minorVersions != null) && !minorVersions.isEmpty()) {
                //remove the version
                segments.remove(0);

                String newVersion = version + "." + minorVersions.last();

                //add the new version
                UriBuilder builder = containerRequest.getBaseUriBuilder().path(newVersion);

                //add the rest of the request
                for (PathSegment segment : segments) {
                    builder.path(segment.getPath());
                }

                if (containerRequest.getRequestUri().getQuery() != null &&
                        !containerRequest.getRequestUri().getQuery().isEmpty()) {
                    builder.replaceQuery(containerRequest.getRequestUri().getQuery());
                }

                info("Version Rewrite: {} --> {}", new Object[] { version, newVersion });

                containerRequest.getProperties().put(REQUESTED_PATH, containerRequest.getPath());
                containerRequest.setUris(containerRequest.getBaseUri(), builder.build());
            }
        }

        return containerRequest;
    }
}
