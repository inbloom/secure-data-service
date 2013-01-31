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

package org.slc.sli.ingestion.aspect;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.aspect.LoggerCarrierAspect;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.LoggerCarrier;
import org.slc.sli.common.util.logging.SecurityEvent;

/**
 * Unit tests for the LoggerCarrierAspect.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LoggerCarrierAspectTest {

	 private MongoTemplate mockedMongoTemplate;

	 @Test
	 public void test() {
		 mockedMongoTemplate = mock(MongoTemplate.class);
		 DBCollection mockedCollection = Mockito.mock(DBCollection.class);
		 DB mockedDB = Mockito.mock(DB.class);
		 SecurityEvent event = createSecurityEvent();
		 LoggerCarrierAspect.aspectOf().setTemplate(mockedMongoTemplate);
		 LoggerCarrierAspect.aspectOf().setCapSize(new String("100"));

		 when(mockedMongoTemplate.collectionExists("securityEvent")).thenReturn(false);
		 audit(event);
		 Mockito.verify(mockedMongoTemplate, times(1)).createCollection(any(String.class), any(CollectionOptions.class));
		 Mockito.verify(mockedMongoTemplate, times(1)).save(any(SecurityEvent.class));

		 when(mockedMongoTemplate.collectionExists("securityEvent")).thenReturn(true);
		 when(mockedMongoTemplate.getCollection("securityEvent")).thenReturn(mockedCollection);
		 when(mockedCollection.isCapped()).thenReturn(true);
		 audit(event);
		 Mockito.verify(mockedMongoTemplate, times(2)).save(any(SecurityEvent.class));

		 when(mockedMongoTemplate.collectionExists("securityEvent")).thenReturn(true);
		 when(mockedMongoTemplate.getCollection("securityEvent")).thenReturn(mockedCollection);
		 when(mockedMongoTemplate.getDb()).thenReturn(mockedDB);
		 when(mockedCollection.isCapped()).thenReturn(false);
		 audit(event);
		 Mockito.verify(mockedDB, times(1)).command(any(DBObject.class));
		 Mockito.verify(mockedMongoTemplate, times(3)).save(any(SecurityEvent.class));
	 }


	 @Test
	 public void testAudit() {
		SecurityEvent event = createSecurityEvent();
		Reflections reflections = new Reflections("org.slc.sli");
		Set<Class<? extends LoggerCarrier>> logs = reflections.getSubTypesOf(LoggerCarrier.class);
		for (Class<? extends LoggerCarrier> cl : logs) {
            if (!cl.isInterface() && !cl.isEnum() && !Modifier.isAbstract(cl.getModifiers())) {
            	LoggerCarrier instance = getInstance(cl);
                if (instance != null && !Modifier.isAbstract( cl.getModifiers())) {
                	instance.audit(event);
                }
            }
		}
	}

	private SecurityEvent createSecurityEvent() {
        List<String> userRoles = Collections.emptyList();
        SecurityEvent event = new SecurityEvent();
        event.setTenantId("Midgar");
        event.setUser("");
        event.setTargetEdOrg("");
        event.setActionUri("AppProcessing");
        event.setAppId("app");
        event.setOrigin("");
        event.setCredential("");
        event.setUserOrigin("");
        event.setTimeStamp(new Date());
        event.setProcessNameOrId(ManagementFactory.getRuntimeMXBean().getName());
        event.setClassName(this.getClass().getName());
        event.setLogLevel(LogLevelType.TYPE_INFO);
        event.setRoles(userRoles);
        event.setLogMessage("App process started.");
        return event;
	}

    private LoggerCarrier getInstance(Class<? extends LoggerCarrier> clazz) {
        try {
            //will work for 0-length constructors
            return clazz.newInstance();
        } catch (Exception e) {
            //try alternative constructors
        	LoggerFactory.getLogger(LoggerCarrierAspectTest.class).error("fail to get the constructor");
            return null;
        }
    }

}
