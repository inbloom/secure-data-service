package org.slc.sli.api.resources.url;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.uri.UriBuilderImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.representation.EmbeddedLink;

import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_TYPE_AGGREGATION;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_EXPOSE_TYPE_AGGREGATIONS;
import static org.slc.sli.api.resources.util.ResourceConstants.ENTITY_BODY_GROUPBY;

/**
 * Tests the AggregateURLCreator
 * 
 * @author srupasinghe
 * 
 */
public class AggregateURLCreatorTest {
    private AggregateURLCreator creator = new AggregateURLCreator(); // class under test
    private static final String ED_ORG_ID = "12";
    private static final String GRADE_ID = "8";
    
    @Before
    public void setup() {
        MockRepo repo = new MockRepo();
        creator.setRepo(repo);
        
        // create new aggregate entity
        Map<String, Object> agg = buildTestAggregationEntity();
        creator.repo.create(ENTITY_TYPE_AGGREGATION, agg);
    }
    
    @Test
    public void testValidParamGetURL() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("educationOrganization", ED_ORG_ID);
        params.put("grade", GRADE_ID);
        
        try {
            List<EmbeddedLink> list = creator.getUrls(buildMockUriInfo(""), params);
            assertEquals("Should have one link", 1, list.size());
            
            EmbeddedLink link = list.get(0);
            assertEquals("Type should be aggregation", link.getType(), ENTITY_TYPE_AGGREGATION);
            assertEquals("Should be links", link.getRel(), "links");
            assertTrue("Returned link should point to an aggregation",
                    link.getHref().indexOf(ENTITY_EXPOSE_TYPE_AGGREGATIONS) > 0);
            
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testInvalidParamGetUrl() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("educationOrganization", "56");
        params.put("grade", GRADE_ID);
        
        try {
            List<EmbeddedLink> list = creator.getUrls(buildMockUriInfo(""), params);
            assertEquals("Should have not have any links", 0, list.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testConvertParamsToEnitiyFormat() {
        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("districtId", "1234");
        initParams.put("schoolId", "45678");
        initParams.put("gradeId", "123");
        
        Map<String, String> params = creator.convertParamsToEnitiyFormat(initParams);
        
        assertEquals("Maps should be same size", params.size(), initParams.size());
        
        for (Map.Entry<String, String> e : initParams.entrySet()) {
            String key = ENTITY_BODY_GROUPBY + "." + e.getKey();
            
            assertEquals("Values should match", e.getValue(), params.get(key));
        }
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
        
        // when(mock.getRequestUri()).thenReturn(new
        // UriBuilderImpl().replaceQuery(queryString).build(null));
        return mock;
    }
    
    private Map<String, Object> buildTestAggregationEntity() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("entityId", "9876543");
        body.put(ENTITY_BODY_GROUPBY + "." + "educationOrganization", ED_ORG_ID);
        body.put(ENTITY_BODY_GROUPBY + "." + "grade", GRADE_ID);
        body.put(ENTITY_BODY_GROUPBY + "." + "subject", "Math");
        body.put("aggregate", "1234");
        return body;
    }
}
