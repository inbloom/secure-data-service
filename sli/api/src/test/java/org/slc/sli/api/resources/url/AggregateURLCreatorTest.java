package org.slc.sli.api.resources.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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

import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.common.constants.ResourceConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

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
        creator.repo.create(ResourceConstants.ENTITY_TYPE_AGGREGATION, agg);
    }

    @Test
    public void testValidParamGetURL() {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("groupBy.educationOrganization", "=", ED_ORG_ID));
        neutralQuery.addCriteria(new NeutralCriteria("groupBy.grade", "=", GRADE_ID));

        try {
            List<EmbeddedLink> list = creator.getUrls(buildMockUriInfo(""), "", "", neutralQuery);
            assertEquals("Should have one link", 1, list.size());

            EmbeddedLink link = list.get(0);
            assertEquals("Type should be aggregation", link.getType(), ResourceConstants.ENTITY_TYPE_AGGREGATION);
            assertEquals("Should be links", link.getRel(), "links");
            assertTrue("Returned link should point to an aggregation",
                    link.getHref().indexOf(ResourceConstants.ENTITY_EXPOSE_TYPE_AGGREGATIONS) > 0);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testInvalidParamGetUrl() {
        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria("groueducationOrganization", "=", "56"));
        neutralQuery.addCriteria(new NeutralCriteria("grade", "=", GRADE_ID));

        try {
            List<EmbeddedLink> list = creator.getUrls(buildMockUriInfo(""), "", "", neutralQuery);
            assertEquals("Should have not have any links", 0, list.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
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
        Map<String, Object> groupBy = new HashMap<String, Object>();
        groupBy.put("educationOrganization", ED_ORG_ID);
        groupBy.put("grade", GRADE_ID);
        groupBy.put("subject", "Math");
        body.put(ResourceConstants.ENTITY_BODY_GROUPBY, groupBy);
        body.put("aggregate", "1234");
        return body;
    }
}
