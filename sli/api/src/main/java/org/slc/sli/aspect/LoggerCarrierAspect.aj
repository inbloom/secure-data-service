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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;



public aspect LoggerCarrierAspect {

    declare parents : (org.slc.sli.api..* && !java.lang.Enum+ && !org.slc.sli.api.util.SecurityUtil.SecurityTask+ && !org.slc.sli.api.util.PATCH && !org.slc.sli.api.security.RightsAllowed)  implements LoggerCarrier;

    @Autowired
    private Repository<Entity> mongoEntityRepository;

	public void LoggerCarrier.debug(String msg) {
        LoggerFactory.getLogger(this.getClass()).debug(msg);
    }



	public Repository<Entity> getMongoEntityRepository() {
		return mongoEntityRepository;
	}



	public void setMongoEntityRepository(Repository<Entity> mongoEntityRepository) {
		this.mongoEntityRepository = mongoEntityRepository;
	}



	public void LoggerCarrier.info(String msg) {
        LoggerFactory.getLogger(this.getClass()).info(msg);
    }

    public void LoggerCarrier.warn(String msg) {
        LoggerFactory.getLogger(this.getClass()).warn(msg);
    }

    public void LoggerCarrier.error(String msg) {
        LoggerFactory.getLogger(this.getClass()).error(msg);
    }

    public void LoggerCarrier.debug(String msg, Object... params) {
        LoggerFactory.getLogger(this.getClass()).debug(msg, params);
    }

    public void LoggerCarrier.info(String msg, Object... params) {
        LoggerFactory.getLogger(this.getClass()).info(msg, params);
    }

    public void LoggerCarrier.warn(String msg, Object... params) {
        LoggerFactory.getLogger(this.getClass()).warn(msg, params);
    }

    public void LoggerCarrier.error(String msg, Object... params) {
        LoggerFactory.getLogger(this.getClass()).error(msg, params);
    }

    public void LoggerCarrier.error(String msg, Throwable x) {
        LoggerFactory.getLogger(this.getClass()).error(msg, x);
    }

    public void LoggerCarrier.auditLog(SecurityEvent event) {
        LoggerFactory.getLogger("audit").info(event.toString());
    }
    

    public void LoggerCarrier.audit(SecurityEvent event) {
        LoggerFactory.getLogger("audit").info(event.toString());
    	Repository<Entity> mer= LoggerCarrierAspect.aspectOf().getMongoEntityRepository();
    	if(mer != null) {
    		Map<String, Object> metadata = new HashMap<String, Object>();
    		metadata.put("tenantId", event.getTenantId());
    	    mer.create("securityEvent", event.getProperties(), metadata, "securityEvent");
    	} else {
            LoggerFactory.getLogger("audit").info("[Could not log SecurityEvent to mongo!]");
        }
    }

}
