package org.slc.sli.ingestion.aspect;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.mongodb.DBObject;
import com.mongodb.DB;
import org.junit.Test;
import org.reflections.Reflections;
import org.slc.sli.aspect.LoggerCarrierAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.LoggerCarrier;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.util.LogUtil;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Constructor;
import java.util.Collection;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.DBCollection;

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