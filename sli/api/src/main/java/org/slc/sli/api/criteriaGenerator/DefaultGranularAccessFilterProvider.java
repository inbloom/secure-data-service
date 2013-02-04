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

package org.slc.sli.api.criteriaGenerator;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Provides access to a GranularAccessFilter. Stores the entity in the request scope.
 * Servers as an abstraction for the static method calls.
 * @author: sashton
 */
@Component
public class DefaultGranularAccessFilterProvider implements GranularAccessFilterProvider {
    
    private static final String ATTR_NAME = "GranularAccessFilter";
    
    @Override
    public void storeGranularAccessFilter(GranularAccessFilter filter) {
        RequestContextHolder.getRequestAttributes().setAttribute(ATTR_NAME, filter, RequestAttributes.SCOPE_REQUEST);
    }
    
    @Override
    public GranularAccessFilter getFilter() {
        return (GranularAccessFilter) RequestContextHolder.getRequestAttributes().getAttribute(ATTR_NAME,
                RequestAttributes.SCOPE_REQUEST);
    }
    
    @Override
    public boolean hasFilter() {
        return getFilter() != null;
    }
    
}
