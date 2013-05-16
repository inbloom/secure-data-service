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

package org.slc.sli.aspect;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.common.util.logging.LoggerCarrier;

import java.util.HashMap;
import java.util.Map;

public aspect LoggerCarrierAspect {
    declare parents : (org.slc.sli.bulk.extract..* && !java.lang.Enum+)  implements LoggerCarrier;

    private static final Logger LOG = LoggerFactory.getLogger("SecurityMonitor");

    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> entityRepository;

    public void LoggerCarrier.audit(SecurityEvent event) {
        LOG.info(event.toString());
        Repository<Entity> mer= LoggerCarrierAspect.aspectOf().getEntityRepository();
        if(mer != null) {
            Map<String, Object> metadata = new HashMap<String, Object>();
            metadata.put("tenantId", event.getTenantId());
            mer.create("securityEvent", event.getProperties(), metadata, "securityEvent");
        } else {
            LOG.error("Could not log SecurityEvent to the database.");
        }

        switch (event.getLogLevel()) {
            case TYPE_DEBUG:
                LOG.debug(event.toString());
                break;

            case TYPE_WARN:
                LOG.warn(event.toString());
                break;

            case TYPE_INFO:
                LOG.info(event.toString());
                break;

            case TYPE_ERROR:
                LOG.error(event.toString());
                break;

            case TYPE_TRACE:
                LOG.trace(event.toString());
                break;

            default:
                LOG.info(event.toString());
                break;
        }

    }

    public Repository<Entity> getEntityRepository() {
        return entityRepository;
    }

    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }
}