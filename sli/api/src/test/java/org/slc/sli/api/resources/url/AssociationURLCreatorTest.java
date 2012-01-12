package org.slc.sli.api.resources.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_TYPE_STAFF_EDORG_ASSOC;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_TYPE_STAFF_SCHOOL_ASSOC;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_BODY_STAFF_ID;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_BODY_EDORG_ID;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_BODY_SCHOOL_ID;
import static org.slc.sli.api.resources.util.ResourceConstants.RESOURCE_PATH_DISTRICT;
import static org.slc.sli.api.resources.util.ResourceConstants.RESOURCE_PATH_SCHOOL;

import org.slc.sli.api.representation.EmbeddedLink;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.uri.UriBuilderImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.slc.sli.api.service.MockRepo;

/**
 * Tests the AssociationURLCreator
 * @author srupasinghe
 *
 */
public class AssociationURLCreatorTest {
	private AssociationURLCreator creator = new AssociationURLCreator(); //class under test
	private static final String STAFF_ID = "1234";
	private static final String ED_ORG_ID = "56789";
	private static final String SCHOOL_ID = "45678";
	
	@Before
	public void setup() {
		MockRepo repo = new MockRepo();
		creator.setRepo(repo);
		
		// create new staffedorgassociation entity
        Map<String, Object> staffEdOrgAssoc = buildTestStaffEdOrgEntity();
        creator.repo.create(ENTITY_TYPE_STAFF_EDORG_ASSOC, staffEdOrgAssoc);
        
        // create new staffschoolassociation entity
        Map<String, Object> staffSchoolAssoc = buildTestStaffSchoolEntity();
        creator.repo.create(ENTITY_TYPE_STAFF_SCHOOL_ASSOC, staffSchoolAssoc);
	}
	
	@Test
	public void testGetStaffEducationOrganizationLinks() {
		Map<String, String> params = new HashMap<String, String>();
		
		try {
			params.put(ENTITY_BODY_STAFF_ID, STAFF_ID);
			
			List<EmbeddedLink> list = creator.getStaffEducationOrganizationAssociationLinks(buildMockUriInfo(""), params);
			assertEquals("Should have one link", 1, list.size());
			
			EmbeddedLink link = list.get(0);
			assertEquals("Type should be staff-edorg-association", link.getType(), ENTITY_TYPE_STAFF_EDORG_ASSOC);
			assertEquals("Should be type links", link.getRel(), "links");
			assertTrue("Returned link should point to a district", link.getHref().indexOf(RESOURCE_PATH_DISTRICT) > 0);
			
			assertUUID(link.getHref(), ED_ORG_ID);			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetStaffSchoolAssociationLinks() {
		Map<String, String> params = new HashMap<String, String>();
		
		try {
			params.put(ENTITY_BODY_STAFF_ID, STAFF_ID);
			
			List<EmbeddedLink> list = creator.getStaffSchoolAssociationLinks(buildMockUriInfo(""), params);
			assertEquals("Should have one link", 1, list.size());
			
			EmbeddedLink link = list.get(0);
			assertEquals("Type should be staff-school-association", link.getType(), ENTITY_TYPE_STAFF_SCHOOL_ASSOC);
			assertEquals("Should be type links", link.getRel(), "links");
			assertTrue("Returned link should point to a district", link.getHref().indexOf(RESOURCE_PATH_SCHOOL) > 0);
			
			assertUUID(link.getHref(), SCHOOL_ID);			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private void assertUUID(String url, String uuid) {
		Pattern regex = Pattern.compile(".+/(\\d+)$");
        Matcher matcher = regex.matcher(url);
        matcher.find();
        assertEquals(1, matcher.groupCount());
        assertEquals("Id should match", matcher.group(1), uuid);
	}
	
	public UriInfo buildMockUriInfo(final String queryString) throws Exception {
        UriInfo mock = mock(UriInfo.class);
        when(mock.getAbsolutePathBuilder()).thenAnswer(new Answer<UriBuilder>() {
            
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("absolute");
            }
        });
        when(mock.getBaseUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
            
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("base");
            }
        });
        when(mock.getRequestUriBuilder()).thenAnswer(new Answer<UriBuilder>() {
            
            @Override
            public UriBuilder answer(InvocationOnMock invocation) throws Throwable {
                return new UriBuilderImpl().path("request");
            }
        });
        
        //when(mock.getRequestUri()).thenReturn(new UriBuilderImpl().replaceQuery(queryString).build(null));
        return mock;
    }
	
	private Map<String, Object> buildTestStaffEdOrgEntity() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ENTITY_BODY_STAFF_ID, STAFF_ID);
        body.put(ENTITY_BODY_EDORG_ID, ED_ORG_ID);
        
        return body;
    }
	
	private Map<String, Object> buildTestStaffSchoolEntity() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ENTITY_BODY_STAFF_ID, STAFF_ID);
        body.put(ENTITY_BODY_SCHOOL_ID, SCHOOL_ID);
        
        return body;
    }
}
