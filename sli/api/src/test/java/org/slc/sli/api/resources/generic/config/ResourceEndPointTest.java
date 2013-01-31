/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.api.resources.generic.config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.resources.generic.util.ResourceTemplate;
import org.slc.sli.api.test.WebContextTestExecutionListener;

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

        String json = "[" +
                "{\n" +
                "    \"nameSpace\": [\"v6.0\", \"v6.1\"],\n" +
                "    \"resources\":[\n" +
                "        {\n" +
                "            \"path\":\"/reportCards\",\n" +
                "            \"doc\":\"some doc.\"\n" +
                "        }]}," +
                "{\n" +
                "    \"nameSpace\": [\"v7.0\"],\n" +
                "    \"resources\":[\n" +
                "        {\n" +
                "            \"path\":\"/schools\",\n" +
                "            \"dateSearchDisallowed\":\"true\",\n" +
                "            \"doc\":\"some school doc.\"\n" +
                "        }]}]";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());

        ApiNameSpace[] nameSpaces = resourceEndPoint.loadNameSpace(inputStream);
        assertEquals("Should match", 2, nameSpaces.length);

        ApiNameSpace nameSpace = nameSpaces[0];
        assertEquals("Should match", 2, nameSpace.getNameSpace().length);
        assertEquals("Should match", "v6.0", nameSpace.getNameSpace()[0]);
        assertEquals("Should match", "v6.1", nameSpace.getNameSpace()[1]);
        assertEquals("Should match", 1, nameSpace.getResources().size());
        assertEquals("Should match", "/reportCards", nameSpace.getResources().get(0).getPath());
        assertEquals("Should match", "some doc.", nameSpace.getResources().get(0).getDoc());

        nameSpace = nameSpaces[1];
        assertEquals("Should match", 1, nameSpace.getNameSpace().length);
        assertEquals("Should match", "v7.0", nameSpace.getNameSpace()[0]);
        assertEquals("Should match", 1, nameSpace.getResources().size());
        assertEquals("Should match", "/schools", nameSpace.getResources().get(0).getPath());
        assertEquals("Should match", "some school doc.", nameSpace.getResources().get(0).getDoc());

        assertEquals("Should have one entry", 1, resourceEndPoint.getDateRangeDisallowedEndPoints().size());
        assertTrue("Should match",  resourceEndPoint.getDateRangeDisallowedEndPoints().contains("v7.0/schools"));
    }


}
