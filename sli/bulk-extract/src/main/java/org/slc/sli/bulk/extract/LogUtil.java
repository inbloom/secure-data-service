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
package org.slc.sli.bulk.extract;

import java.util.HashMap;
import java.util.Map;

import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * fake aspect being converted to a static method
 * 
 * @author not nbrown
 * 
 */
@Component
public class LogUtil {
    
    private static final Logger LOG = LoggerFactory.getLogger("SecurityMonitor");
    
    private static Repository<Entity> entityRepository;
    
    @Autowired
    public LogUtil(@Qualifier("secondaryRepo") Repository<Entity> repo) {
        // yes, I know assigning a static variable in a constructor is evil, but it is less evil
        // than aspects
        setEntityRepository(repo);
    }
    
    public static void audit(SecurityEvent event) {
        if (entityRepository != null) {
            Map<String, Object> metadata = new HashMap<String, Object>();
            metadata.put("tenantId", event.getTenantId());
            entityRepository.create("securityEvent", event.getProperties(), metadata, "securityEvent");
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
    
    public static Repository<Entity> getEntityRepository() {
        return entityRepository;
    }
    
    public static void setEntityRepository(Repository<Entity> entityRepository) {
        LogUtil.entityRepository = entityRepository;
    }
}