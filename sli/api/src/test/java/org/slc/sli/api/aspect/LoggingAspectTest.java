package org.slc.sli.api.aspect;

import java.lang.reflect.Modifier;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reflections.Reflections;
import org.slc.sli.aspect.LoggerCarrier;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;

/**
 * Tests that logging can be done via inter-type declared methods
 * 
 * @author dkornishev
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LoggingAspectTest {
    
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
                try {
                    LoggerCarrier instance = cl.newInstance();
                    instance.audit(se);
                    instance.debug(msg);
                    instance.debug(msg, param);
                    instance.info(msg);
                    instance.info(msg, param);
                    instance.warn(msg);
                    instance.warn(msg, param);
                    instance.error(msg, x);
                } catch (Exception e) {
                    info("Error padding coverage for {}", cl.getName());
                }
            }
        }
        sw.stop();
        
        info("Finished in {} ms", sw.getTotalTimeMillis());
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
}
