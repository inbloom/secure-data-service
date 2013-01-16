package org.slc.sli.aspect;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.LoggerCarrier;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.ingestion.util.LogUtil;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Collection;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LoggerCarrierAspectTest {

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