package org.slc.sli.validation.utils;

import java.io.IOException;

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
    public void testSliXSD() throws IOException {
        NeutralJsonExporter.main(new String[] { "--test", "classpath:sliXsd" });
    }
}
