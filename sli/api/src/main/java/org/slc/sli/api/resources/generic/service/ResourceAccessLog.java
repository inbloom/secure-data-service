/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
package org.slc.sli.api.resources.generic.service;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.security.SecurityEventBuilder;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;

/**
 * Log security events
 *
 */
@Component
public class ResourceAccessLog {


    @Autowired
    private ResourceHelper resourceHelper;

    @Autowired
    private SecurityEventBuilder securityEventBuilder;

    public void logAccessToRestrictedEntity(final UriInfo uriInfo, final Resource resource, final String loggingClass) {

        EntityDefinition definition = resourceHelper.getEntityDefinition(resource);

        if (definition == null) {
            definition = resourceHelper.getEntityDefinition(resource);
        }

        if (definition.isRestrictedForLogging()) {
            if (securityEventBuilder != null) {
                SecurityEvent event = securityEventBuilder.createSecurityEvent(loggingClass,
                        uriInfo, "restricted entity \"" + definition.getResourceName() + "\" is accessed.");
                audit(event);
            } else {
                warn("Cannot create security event, when restricted entity \"" + definition.getResourceName()
                        + "\" is accessed.");
            }
        }
    }
}
