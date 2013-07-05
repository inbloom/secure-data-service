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
package org.slc.sli.api.security.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;

import com.sun.jersey.spi.container.ContainerRequest;

import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.resources.generic.util.ResourceMethod;
import org.slc.sli.common.constants.EntityNames;

public abstract class AccessValidator {
    
    /**
     * discipline related entities
     */
    private static final Set<String> DISCIPLINE_RELATED = new HashSet<String>(
            Arrays.asList(EntityNames.DISCIPLINE_ACTION,
                    EntityNames.DISCIPLINE_INCIDENT,
                    EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION,
                    ResourceNames.DISCIPLINE_ACTIONS,
                    ResourceNames.DISCIPLINE_INCIDENTS,
                    ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS
                    ));

    private List<String> cleanPath(List<PathSegment> segs) {
        if (segs == null || segs.isEmpty()) {
            return Collections.<String> emptyList();
        }

        List<String> paths = new ArrayList<String>();
        // first one is version, system calls (un-versioned) have been handled elsewhere
        for (int i = 1; i < segs.size(); ++i) {
            if (segs.get(i) != null) {
                String path = segs.get(i).getPath();
                if (path != null && !path.isEmpty()) {
                    paths.add(path);
                }
            }
        }
        
        return paths;
    }
    
    private boolean isDisiplineRelated(List<String> paths) {
        for (String s : paths) {
            if (DISCIPLINE_RELATED.contains(s)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * check if a path can be accessed according to stored business rules
     * 
     * @param ContextRequest
     *            request
     * @return true if request is allowed
     */
    public boolean isAllowed(ContainerRequest request) {
        if (request == null || request.getPathSegments() == null) {
            return false;
        }
        
        List<String> paths = cleanPath(request.getPathSegments());
        
        if (paths.isEmpty()) {
            return false;
        }
        
        if (isDisiplineRelated(paths)) {
            return false;
        }
        
        if (ResourceMethod.getWriteOps().contains(request.getMethod())) {
            return isWriteAllowed(paths);
        } 
        
        return isReadAllowed(paths, request.getQueryParameters());
    }
    
    protected abstract boolean isReadAllowed(List<String> path, MultivaluedMap<String, String> queryParameters);
    
    protected abstract boolean isWriteAllowed(List<String> path);
}
