package org.slc.sli.ingestion.tool;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test for OfflineTool main
 * @author tke
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext.xml" })
public class OfflineToolTest {

    @Test
    public void testMain() {
        final String dir = "invalid/Session1.zip";
        Resource fileResource = new ClassPathResource(dir);
        String [] args = new String[1];
        try {
            args[0] = fileResource.getFile().getParent();
        } catch (IOException e) {
            fail("IO Exception");
        }
        try {
            OfflineTool.main(args);
        } catch (IOException e) {
            fail("IO Exception from main");
        }

        String [] args2 = new String[2];
        try {
            OfflineTool.main(args2);
        } catch (IOException e) {
            fail("IO Exception from main");
        }
    }

}
