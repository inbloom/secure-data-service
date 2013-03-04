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
import java.util.Map;
import java.util.Set;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.junit.Ignore;
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
import org.slc.sli.dal.repository.MongoRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Unit tests for the LoggerCarrierAspect.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/BatchJob-Mongo.xml" })
public class LoggerCarrierAspectTest {

    private MongoRepository<Entity> mockedEntityRepository;

     @Test
     public void test() {
         mockedEntityRepository = mock(MongoRepository.class);
         DBCollection mockedCollection = Mockito.mock(DBCollection.class);
         DB mockedDB = Mockito.mock(DB.class);
         SecurityEvent event = createSecurityEvent();
         LoggerCarrierAspect.aspectOf().setEntityRepository(mockedEntityRepository);
         LoggerCarrierAspect.aspectOf().setCapSize(new String("100"));
         
         MongoTemplate mockedMongoTemplate = mock(MongoTemplate.class);
         when(mockedEntityRepository.getTemplate()).thenReturn(mockedMongoTemplate);
         
         when(mockedMongoTemplate.collectionExists("securityEvent")).thenReturn(false);
         audit(event);
         Mockito.verify(mockedMongoTemplate, times(1)).createCollection(any(String.class), any(CollectionOptions.class));
         Mockito.verify(mockedEntityRepository, times(1)).create(any(String.class), any(Map.class), any(Map.class), any(String.class));

         when(mockedMongoTemplate.collectionExists("securityEvent")).thenReturn(true);
         when(mockedMongoTemplate.getCollection("securityEvent")).thenReturn(mockedCollection);
         when(mockedCollection.isCapped()).thenReturn(true);
         audit(event);
         Mockito.verify(mockedEntityRepository, times(2)).create(any(String.class), any(Map.class), any(Map.class), any(String.class));

         when(mockedMongoTemplate.collectionExists("securityEvent")).thenReturn(true);
         when(mockedMongoTemplate.getCollection("securityEvent")).thenReturn(mockedCollection);
         when(mockedMongoTemplate.getDb()).thenReturn(mockedDB);
         when(mockedCollection.isCapped()).thenReturn(false);
         audit(event);
         Mockito.verify(mockedDB, times(1)).command(any(DBObject.class));
         
         Mockito.verify(mockedEntityRepository, times(3)).create(any(String.class), any(Map.class), any(Map.class), any(String.class));
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
