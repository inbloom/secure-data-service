/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.api.jersey;

import java.util.List;
import java.util.SortedSet;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.api.resources.generic.config.ResourceEndPoint;

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
            boolean isBulkNonVersion = version.equals("bulk");

            SortedSet<String> minorVersions = resourceEndPoint.getNameSpaceMappings().get(version);

            if (isBulkNonVersion || ((minorVersions != null) && !minorVersions.isEmpty())) {
                if (!isBulkNonVersion) {
                    //remove the version
                    segments.remove(0);
                }

                String newVersion;
                // Bulk extract always returns latest API version.
                if (isBulkNonVersion || ((segments.size() > 1) && segments.get(1).getPath().equals("bulk"))) {
                    newVersion = getLatestApiVersion();
                } else {
                    newVersion = version + "." + minorVersions.last();
                }

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

/**
 * Get the latest API version.
 *
 * @return Latest API version
 */
    public String getLatestApiVersion() {
      String latestApiVersion = "";
      for (String majorVersion : resourceEndPoint.getNameSpaceMappings().keySet()) {
          String minorVersion = resourceEndPoint.getNameSpaceMappings().get(majorVersion).last();
          String fullVersion = majorVersion + "." + minorVersion;
              if (fullVersion.compareToIgnoreCase(latestApiVersion) > 0) {
                  latestApiVersion = fullVersion;
              }
      }
      return latestApiVersion;
    }
}
