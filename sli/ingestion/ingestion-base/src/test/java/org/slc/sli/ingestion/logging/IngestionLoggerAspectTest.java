package org.slc.sli.ingestion.logging;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class IngestionLoggerAspectTest {

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

}
