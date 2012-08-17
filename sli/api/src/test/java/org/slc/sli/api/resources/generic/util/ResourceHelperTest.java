package org.slc.sli.api.resources.generic.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.UriInfo;

import java.net.URI;
import java.net.URISyntaxException;

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
        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/generic/v1/sections"));
        assertEquals("v1/sections", resourceHelper.getResourceName(mockUriInfo, ResourceTemplate.ONE_PART));

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/generic/v1/sections/1234"));
        assertEquals("v1/sections", resourceHelper.getResourceName(mockUriInfo, ResourceTemplate.TWO_PART));

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/generic/v1/sections/1234/studentSectionAssociations"));
        assertEquals("v1/studentSectionAssociations", resourceHelper.getResourceName(mockUriInfo, ResourceTemplate.THREE_PART));

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/generic/v1/sections/1234/studentSectionAssociations/students"));
        assertEquals("v1/students", resourceHelper.getResourceName(mockUriInfo, ResourceTemplate.FOUR_PART));
    }

    @Test
    public void testGetBaseName() throws URISyntaxException {
        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/generic/v1/sections/1234/studentSectionAssociations"));
        assertEquals("sections", resourceHelper.getBaseName(mockUriInfo, ResourceTemplate.THREE_PART));

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/generic/v1/sections/1234/studentSectionAssociations/students"));
        assertEquals("sections", resourceHelper.getBaseName(mockUriInfo, ResourceTemplate.FOUR_PART));
    }

    @Test
    public void testGetResourcePath() throws URISyntaxException {
        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/generic/v1/sections"));
        assertEquals("v1/sections", resourceHelper.getResourcePath(mockUriInfo, ResourceTemplate.ONE_PART));

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/generic/v1/sections/1234"));
        assertEquals("v1/sections/{id}", resourceHelper.getResourcePath(mockUriInfo, ResourceTemplate.TWO_PART));

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/generic/v1/sections/1234/studentSectionAssociations"));
        assertEquals("v1/sections/{id}/studentSectionAssociations",
                resourceHelper.getResourcePath(mockUriInfo, ResourceTemplate.THREE_PART));

        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/generic/v1/sections/1234/studentSectionAssociations/students"));
        assertEquals("v1/sections/{id}/studentSectionAssociations/students",
                resourceHelper.getResourcePath(mockUriInfo, ResourceTemplate.FOUR_PART));
    }

    @Test
    public void testGetAssociationName() throws URISyntaxException {
        when(mockUriInfo.getRequestUri()).thenReturn(new URI("/generic/v1/sections/1234/studentSectionAssociations/students"));
        assertEquals("studentSectionAssociations", resourceHelper.getAssociationName(mockUriInfo, ResourceTemplate.FOUR_PART));
    }
}
