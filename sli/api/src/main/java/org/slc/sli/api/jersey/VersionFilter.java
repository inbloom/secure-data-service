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

    private static final String VERSION_PATTERN = "v([0-9])+(\\.([0-9])+)?";

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {

        List<PathSegment> segments = containerRequest.getPathSegments();

        if (!segments.isEmpty()) {
            String version = segments.get(0).getPath();
            boolean noVersion = version.equals("bulk");

            SortedSet<String> minorVersions = resourceEndPoint.getNameSpaceMappings().get(version);

            String newVersion = null;

            if(noVersion || (segments.size() > 1 && segments.get(1).getPath().equals("bulk"))) {
                if (noVersion) {
                    //there is no version specified in the request for bulk extract
                    version = "";
                } else {
                    //remove the version
                    segments.remove(0);
                }

                newVersion = getBulkExtractVersion(version);

            } else if ((minorVersions != null) && !minorVersions.isEmpty()) {
                segments.remove(0);
                newVersion = version + "." + minorVersions.last();
            }

            if(newVersion != null) {
                updateContainerRequest(containerRequest, segments, newVersion);
                info("Version Rewrite: {} --> {}", new Object[] { version, newVersion });
            }
        }

        return containerRequest;
    }

    /**
     * @param version
     * @return
     */
    private String getBulkExtractVersion(String version) {
        String newVersion = null;
        // Bulk extract always returns latest API version.
        if(validVersion(version)) {
            newVersion = getLatestApiVersion(version);
        } else {
            newVersion = version;
        }
        return newVersion;
    }

    private ContainerRequest updateContainerRequest(ContainerRequest containerRequest, List<PathSegment> segments, String newVersion) {
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

        containerRequest.getProperties().put(REQUESTED_PATH, containerRequest.getPath());
        containerRequest.setUris(containerRequest.getBaseUri(), builder.build());

        return containerRequest;
    }

/**
 * Get the latest API version.
 *
 * @return Latest API version
 */
    public String getLatestApiVersion(String requestedVersion) {
      String latestApiVersion = "";

      for (String majorVersion : resourceEndPoint.getNameSpaceMappings().keySet()) {
          String minorVersion = resourceEndPoint.getNameSpaceMappings().get(majorVersion).last();
          String fullVersion = majorVersion + "." + minorVersion;
              if (fullVersion.compareToIgnoreCase(latestApiVersion) > 0) {
                  latestApiVersion = fullVersion;
              }
      }
      if(requestedVersion == null ||requestedVersion.equals("")) {
          return latestApiVersion;
      }
      return compareVersion(requestedVersion, latestApiVersion) ? requestedVersion : latestApiVersion;
    }

    /**
     * Compre the 2 versions, assuming version format is vxx.xx, (define in VERSION_PATTERN).
     * @param version1 first version
     * @param version2 second version
     * @return true if version1 is bigger then version2
     */
    private static boolean compareVersion(String version1, String version2) {
        String [] v1 = version1.split("\\.");
        String [] v2 = version2.split("\\.");
        int major1 = 0, minor1 = 0;
        int major2 = 0, minor2 = 0;

        if(v1.length > 0) {
            major1 = Integer.parseInt(v1[0].substring(1));
            if(v1.length > 1) {
                minor1 = Integer.parseInt(v1[1]);
            }
        }

        if(v2.length > 0) {
            major2 = Integer.parseInt(v2[0].substring(1));
            if(v2.length > 1) {
                minor2 = Integer.parseInt(v2[1]);
            }
        }

        if(major1 > major2) {
            return true;
        } else if(major1 == major2) {
            return minor1 > minor2;
        }
        return false;

    }

    private static boolean validVersion(String version) {
        return version.equals("") || version.matches(VERSION_PATTERN);
    }
}
