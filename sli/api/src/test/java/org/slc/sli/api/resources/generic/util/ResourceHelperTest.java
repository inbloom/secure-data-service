package org.slc.sli.api.resources.generic.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

/**
 * @author jstokes
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ResourceHelperTest {

    private ResourceHelper resourceHelper;

    @Before
    public void setup() {
        resourceHelper = new RestResourceHelper();
    }

    @Test
    public void testGrabResource() {
        final String readAll = "/generic/resource";
        assertEquals("resource", resourceHelper.grabResource(readAll, ResourceTemplate.ONE_PART));
        final String readWithId = "/generic/resource/1234";
        assertEquals("resource", resourceHelper.grabResource(readWithId, ResourceTemplate.TWO_PART));
        final String threePart = "/generic/entity/1234/resource";
        assertEquals("resource", resourceHelper.grabResource(threePart, ResourceTemplate.THREE_PART));
        final String fourPart = "/generic/entity/1234/association/resource";
        assertEquals("resource", resourceHelper.grabResource(fourPart, ResourceTemplate.FOUR_PART));
    }
}
