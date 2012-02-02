package org.slc.sli.validation.utils;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Runs the dependency checker against the sli XSDs.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SliXsdTest {
    
    @Test
    public void testSliXSD() {
        try {
            NeutralJsonExporter.main(new String[] { "--test", "classpath:sliXsd-wip" });
        } catch (Throwable e) {
            System.err.println(e);
            fail(e.toString());
        }
    }
}
