package org.slc.sli.api.aspect;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;

/**
 * Tests that logging can be done via inter-type declared methods
 * 
 * @author dkornishev
 * 
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LoggingAspectTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(LoggingAspectTest.class);
    
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
    
    @Ignore
    @Test
    public void benchCompare() {
        StopWatch aop = new StopWatch();
        StopWatch plain = new StopWatch();
        StopWatch plainNoField = new StopWatch();
        
        int numIters = 500000;
        for (int i = 0; i < numIters; i++) {
            plain.start();
            LOG.debug("DEBUG MSG< OMGFG PLAIN {}/{}/{}{}", new Object[] { true, Math.random(), "hello world", i });
            plain.stop();

            plainNoField.start();
            LoggerFactory.getLogger(this.getClass()).debug("DEBUG MSG< OMGFG PLAIN {}/{}/{}{}", new Object[] { true, Math.random(), "hello world", i });
            plainNoField.stop();

            aop.start();
            debug("DEBUG MSG< OMGFG AOP {}/{}/{}{}", true, Math.random(), "hello world", i);
            aop.stop();
        }
        
        info("Plain: {} AVG: {}", plain.getTotalTimeMillis(), plain.getTotalTimeMillis() / (float) numIters);
        info("Plain No Field: {} AVG: {}", plainNoField.getTotalTimeMillis(), plainNoField.getTotalTimeMillis() / (float) numIters);
        info("AOP: {} AVG: {}", aop.getTotalTimeMillis(), aop.getTotalTimeMillis() / (float) numIters);
    }
}
