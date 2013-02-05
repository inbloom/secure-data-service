/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.api.resources.generic.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jstokes
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ResourceHelperTest {

    private ResourceHelper resourceHelper;
    private UriInfo mockUriInfo;

    @Before
    public void setup() {
        resourceHelper = new RestResourceHelper();
        mockUriInfo = mock(UriInfo.class);
    }

    @Test
    public void testGetResourceName() throws URISyntaxException {
        Resource resource;

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/rest/v1/sections"));
        resource = resourceHelper.getResourceName(mockUriInfo, ResourceTemplate.ONE_PART);
        assertEquals("sections", resource.getResourceType());
        assertEquals("v1", resource.getNamespace());

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/rest/v1/sections/1234"));
        resource = resourceHelper.getResourceName(mockUriInfo, ResourceTemplate.TWO_PART);
        assertEquals("sections", resource.getResourceType());
        assertEquals("v1", resource.getNamespace());

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/rest/v1/sections/1234/studentSectionAssociations"));
        resource = resourceHelper.getResourceName(mockUriInfo, ResourceTemplate.THREE_PART);
        assertEquals("studentSectionAssociations", resource.getResourceType());
        assertEquals("v1", resource.getNamespace());

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/rest/v1/sections/1234/studentSectionAssociations/students"));
        resource = resourceHelper.getResourceName(mockUriInfo, ResourceTemplate.FOUR_PART);
        assertEquals("students", resource.getResourceType());
        assertEquals("v1", resource.getNamespace());
    }

    @Test
    public void testGetBaseName() throws URISyntaxException {
        Resource resource;

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/rest/v1/sections/1234/studentSectionAssociations"));
        resource = resourceHelper.getBaseName(mockUriInfo, ResourceTemplate.THREE_PART);
        assertEquals("sections", resource.getResourceType());
        assertEquals("v1", resource.getNamespace());

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/rest/v1/sections/1234/studentSectionAssociations/students"));
        assertEquals("sections", resource.getResourceType());
        assertEquals("v1", resource.getNamespace());
    }

    @Test
    public void testGetResourcePath() throws URISyntaxException {

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/rest/v1/sections"));
        assertEquals("v1/sections", resourceHelper.getResourcePath(mockUriInfo, ResourceTemplate.ONE_PART));

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/rest/v1/sections/1234"));
        assertEquals("v1/sections/{id}", resourceHelper.getResourcePath(mockUriInfo, ResourceTemplate.TWO_PART));

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/rest/v1/sections/1234/studentSectionAssociations"));
        assertEquals("v1/sections/{id}/studentSectionAssociations", resourceHelper.getResourcePath(mockUriInfo, ResourceTemplate.THREE_PART));

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/rest/v1/sections/1234/studentSectionAssociations/students"));
        assertEquals("v1/sections/{id}/studentSectionAssociations/students", resourceHelper.getResourcePath(mockUriInfo, ResourceTemplate.FOUR_PART));
    }

    @Test
    public void testGetAssociationName() throws URISyntaxException {
        Resource resource;
        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/rest/v1/sections/1234/studentSectionAssociations/students"));
        resource = resourceHelper.getAssociationName(mockUriInfo, ResourceTemplate.FOUR_PART);
        assertEquals("studentSectionAssociations", resource.getResourceType());
        assertEquals("v1", resource.getNamespace());
    }
    
    @Test
    public void testUnversioned() throws URISyntaxException {
        Resource resource;
        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/rest/sections"));
        resource = resourceHelper.getResourceName(mockUriInfo, ResourceTemplate.UNVERSIONED_ONE_PART);
        assertEquals("sections", resource.getResourceType());
        assertEquals(null, resource.getNamespace());
    }

    @Test
    public void testExtractVersion() {
        PathSegment segment1 = mock(PathSegment.class);
        when(segment1.getPath()).thenReturn("v1.1");

        List<PathSegment> segmentList = new ArrayList<PathSegment>();
        assertEquals("Should match", PathConstants.V1, resourceHelper.extractVersion(segmentList));

        segmentList.add(segment1);
        assertEquals("", "v1.1", resourceHelper.extractVersion(segmentList));
    }
}
