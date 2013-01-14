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

package org.slc.sli.aspect;

import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.slc.sli.common.util.logging.SecurityEvent;


public aspect LoggerCarrierAspect {

    declare parents : (org.slc.sli.api..* && !java.lang.Enum+ && !org.slc.sli.api.util.SecurityUtil.SecurityTask+ && !org.slc.sli.api.util.PATCH && !org.slc.sli.api.security.RightsAllowed)  implements LoggerCarrier;

	private MongoTemplate template;

	public MongoTemplate getTemplate() {
	    return template;
	}

	public void setTemplate(MongoTemplate template) {
	    this.template = template;
	}

	public void LoggerCarrier.debug(String msg) {
        LoggerFactory.getLogger(this.getClass()).debug(msg);
    }

    public void LoggerCarrier.info(String msg) {
        LoggerFactory.getLogger(this.getClass()).info(msg);
    }

    public void LoggerCarrier.warn(String msg) {
        LoggerFactory.getLogger(this.getClass()).warn(msg);
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
    	MongoTemplate mongoTemplate = LoggerCarrierAspect.aspectOf().getTemplate();
   	 	mongoTemplate.save(event);
   }

}
