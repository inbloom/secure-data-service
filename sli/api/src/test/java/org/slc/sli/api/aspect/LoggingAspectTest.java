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

package org.slc.sli.api.aspect;

import static org.mockito.Mockito.mock; 
import static org.mockito.Matchers.any; 
import static org.mockito.Mockito.times; 
import static org.mockito.Mockito.verify; 
import static org.mockito.Mockito.mock; 
import static org.mockito.Mockito.when; 
import static org.mockito.Matchers.eq;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.reflections.Reflections;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.aspect.LoggerCarrier;
import org.slc.sli.aspect.LoggerCarrierAspect;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
/**
 * Tests that logging can be done via inter-type declared methods
 * 
 * @author dkornishev
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LoggingAspectTest {
    
     private MongoEntityRepository mockedMongoEntityRepository;


    @Test
    public void testDebug() {
        debug("Debug msg");
        debug("Debug msg {} {} {}", "param1", 13, true);
    }

    @Test
    public void testInfo() {
        info("Info msg");
        info("Info msg {} {} {}", "param1", 13, true);
    }

    @Test
    public void testWarn() {
        warn("Warn msg");
        warn("Warn msg {} {} {}", "param1", 13, true);
    }

    @Test
    public void testError() {
        error("Error msg", new Exception("I am an error msg.  FEAR ME."));
    }

    @Test
    public void padCoverageNumbers() {
        StopWatch sw = new StopWatch();
        sw.start();
        Reflections reflections = new Reflections("org.slc.sli");
        Set<Class<? extends LoggerCarrier>> logs = reflections.getSubTypesOf(LoggerCarrier.class);

        SecurityEvent se = new SecurityEvent();
        String msg = "padding {}";
        int param = 42;
        Exception x = new Exception("bogus");

        for (Class<? extends LoggerCarrier> cl : logs) {
            if (!cl.isInterface() && !cl.isEnum() && !Modifier.isAbstract(cl.getModifiers())) {
                LoggerCarrier instance = getInstance(cl);
                if (instance != null && !Modifier.isAbstract( cl.getModifiers())) {
                    instance.audit(se);
                    instance.debug(msg);
                    instance.debug(msg, param);
                    instance.info(msg);
                    instance.info(msg, param);
                    instance.warn(msg);
                    instance.warn(msg, param);
                    instance.error(msg, x);
                    instance.error(msg, new Object[] {});                  
                }
            }
        }
        sw.stop();

        info("Finished in {} ms", sw.getTotalTimeMillis());
    }

    private LoggerCarrier getInstance(Class<? extends LoggerCarrier> clazz) {
        try {
            //will work for 0-length constructors
            return clazz.newInstance();
        } catch (Exception e) {
            //try alternative constructors
            return invokeAlternativeConstructors(clazz);
        }
    }

    /**
     * Look for non-0-length constructors for the given class and invoke those
     * if possible to create an instance.
     * 
     * @param cl
     * @return
     */
    private LoggerCarrier invokeAlternativeConstructors(Class<? extends LoggerCarrier> cl) {

        for (int i = 0; i < cl.getConstructors().length; i++) {
            Constructor<?> c = cl.getConstructors()[i];
            c.setAccessible(true);
            Object[] parms = new Object[c.getParameterTypes().length];
            for (int j = 0; j < parms.length; j++) {
                parms[j] = createParm(c.getParameterTypes()[j]);
            }
            try {
                LoggerCarrier toReturn = (LoggerCarrier) c.newInstance(parms);
                return toReturn;
            } catch (Exception e) {
                error("Couldn't invoke constructor", e);
            }
        }       

        return null;
    }

    /**
     * Try to generate an object of the given class, or return null if not possible.
     * 
     * @param cls
     * @return
     */
    private Object createParm(Class cls) {
        if (cls == EntityDefinitionStore.class) {
            return  new EntityDefinitionStore() {

                @Override
                public EntityDefinition lookupByResourceName(String resourceName) {
                    return null;
                }

                @Override
                public EntityDefinition lookupByEntityType(String entityType) {
                    return null;
                }

                @Override
                public Collection<EntityDefinition> getLinked(EntityDefinition defn) {
                    return null;
                }
            };
        } else if (cls.getName().equals("long")) {
            return 0l;
        }
        return null;
    }

    @Ignore
    @Test
    public void benchCompare() {
        StopWatch aop = new StopWatch();
        StopWatch plain = new StopWatch();
        StopWatch plainNoField = new StopWatch();

        int numIters = 500000;
        for (int i = 0; i < numIters; i++) {
            plain.start();
            debug("DEBUG MSG< OMGFG PLAIN {}/{}/{}{}", new Object[] { true, Math.random(), "hello world", i });
            plain.stop();

            plainNoField.start();
            debug("DEBUG MSG< OMGFG PLAIN {}/{}/{}{}", new Object[] { true, Math.random(), "hello world", i });
            plainNoField.stop();

            aop.start();
            debug("DEBUG MSG< OMGFG AOP {}/{}/{}{}", true, Math.random(), "hello world", i);
            aop.stop();
        }

        info("Plain: {} AVG: {}", plain.getTotalTimeMillis(), plain.getTotalTimeMillis() / (float) numIters);
        info("Plain No Field: {} AVG: {}", plainNoField.getTotalTimeMillis(), plainNoField.getTotalTimeMillis()
                / (float) numIters);
        info("AOP: {} AVG: {}", aop.getTotalTimeMillis(), aop.getTotalTimeMillis() / (float) numIters);
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
  
    @Test 
     public void testAudit() { 
         mockedMongoEntityRepository = mock(MongoEntityRepository.class);
         DBCollection mockedCollection = Mockito.mock(DBCollection.class);
         DB mockedDB = Mockito.mock(DB.class);
         SecurityEvent event = createSecurityEvent();
         LoggerCarrierAspect.aspectOf().setMongoEntityRepository(mockedMongoEntityRepository);
         when(mockedMongoEntityRepository.collectionExists("securityEvent")).thenReturn(false);
         audit(event);
         Mockito.verify(mockedMongoEntityRepository, times(1)).create(any(String.class), any(Map.class),any(Map.class), any(String.class)); 
    }
}
