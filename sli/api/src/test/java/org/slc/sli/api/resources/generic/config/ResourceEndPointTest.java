package org.slc.sli.api.resources.generic.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class ResourceEndPointTest {

    @InjectMocks
    private ResourceEndPoint resourceEndPoint = new ResourceEndPoint();

    @Autowired
    @Spy
    private ResourceHelper resourceHelper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(resourceHelper.resolveResourcePath("/students", ResourceTemplate.FOUR_PART)).thenReturn(true);
    }

    @Test
    public void testGetResourceClass() {
        ResourceEndPointTemplate template = new ResourceEndPointTemplate();

        template.setResourceClass("templateClass");
        assertEquals("Should match", "templateClass", resourceEndPoint.getResourceClass("/students", template));

        template.setResourceClass(null);
        assertEquals("Should match", "org.slc.sli.api.resources.generic.FourPartResource",
                resourceEndPoint.getResourceClass("/students", template));
    }

    @Test(expected = RuntimeException.class)
    public void testNoClass() {
        ResourceEndPointTemplate template = new ResourceEndPointTemplate();
        template.setResourceClass(null);

        when(resourceHelper.resolveResourcePath("/students", ResourceTemplate.FOUR_PART)).thenReturn(false);
        when(resourceHelper.resolveResourcePath("/students", ResourceTemplate.THREE_PART)).thenReturn(false);
        when(resourceHelper.resolveResourcePath("/students", ResourceTemplate.TWO_PART)).thenReturn(false);
        when(resourceHelper.resolveResourcePath("/students", ResourceTemplate.ONE_PART)).thenReturn(false);

        resourceEndPoint.getResourceClass("/students", template);
    }

    @Test
    public void testBuildEndPoints() {
        when(resourceHelper.resolveResourcePath("/students", ResourceTemplate.THREE_PART)).thenReturn(true);

        ResourceEndPointTemplate template = new ResourceEndPointTemplate();
        template.setPath("/students");
        template.setResourceClass("someClass");

        ResourceEndPointTemplate subResource = new ResourceEndPointTemplate();
        subResource.setPath("/{id}/studentSectionAssociations");

        template.setSubResources(Arrays.asList(subResource));

        Map<String, String> resources = resourceEndPoint.buildEndPoints("v1", "", template);
        assertEquals("Should match", 2, resources.keySet().size());
        assertEquals("Should match", "someClass", resources.get("v1/students"));
        assertEquals("Should match", "org.slc.sli.api.resources.generic.ThreePartResource",
                resources.get("v1/students/{id}/studentSectionAssociations"));

    }

    @Test
    public void testLoadNameSpace() throws IOException {
        String json = "{\n" +
                "    \"nameSpace\":\"v6\",\n" +
                "    \"resources\":[\n" +
                "        {\n" +
                "            \"path\":\"/reportCards\",\n" +
                "            \"doc\":\"some doc.\"\n" +
                "        }]}";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());

        ApiNameSpace nameSpace = resourceEndPoint.loadNameSpace(inputStream);
        assertEquals("Should match", "v6", nameSpace.getNameSpace());
        assertEquals("Should match", 1, nameSpace.getResources().size());
        assertEquals("Should match", "/reportCards", nameSpace.getResources().get(0).getPath());
        assertEquals("Should match", "some doc.", nameSpace.getResources().get(0).getDoc());
    }


}
