package org.slc.sli.api.resources.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
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
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.config.AssociationDefinition;
import org.slc.sli.api.config.DefinitionFactory;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EmbeddedLink;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.ResourceConstants;

/**
 * Tests the AssociationURLCreator
 *
 * @author srupasinghe
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class AssociationURLCreatorTest {
    private static final String ENTITY_TYPE_STAFF_EDORG_ASSOC = "staffEducationOrganizationAssociation";
    private static final String ENTITY_TYPE_EDORG_SCHOOL_ASSOC = "educationOrganizationSchoolAssociation";

    @Autowired
    AssociationURLCreator associationURLCreator; // class under test

    @Autowired
    DefinitionFactory factory;

    private EntityDefinition staff;

    private static final String STAFF_ID = "1234";
    private static final String ED_ORG_ID = "56789";
    private static final String SCHOOL_ID = "45678";

    @Before
    public void setup() {
        // create the mock staff entity
        staff = factory.makeEntity("staff").exposeAs("staff").build();
        // create the mock edorg entity
        EntityDefinition educationOrganization = factory.makeEntity("educationOrganization")
                .exposeAs("educationOrganizations").build();
        // create the mock school entity
        EntityDefinition school = factory.makeEntity("school").exposeAs("schools").build();

        // create the mock staffedorg association
        AssociationDefinition staffEdOrgAssoc = factory.makeAssoc("staffEducationOrganizationAssociation", "staffEducationOrganizationAssociations")
                .exposeAs("staff-educationOrganization-associations").storeAs(ENTITY_TYPE_STAFF_EDORG_ASSOC)
                .from(staff, "getStaff", "getStaff")
                .to(educationOrganization, "getEducationOrganization", "getEducationOrganizations")
                .calledFromSource("getEducationOrganizationsAssigned").calledFromTarget("getStaffAssigned").build();

        // create the mock edorgschool association
        AssociationDefinition educationOrganizationSchoolAssoc = factory
                .makeAssoc("educationOrganizationSchoolAssociation", "educationOrganizationSchoolAssociations")
                .exposeAs("educationOrganization-school-associations")
                .storeAs(ENTITY_TYPE_EDORG_SCHOOL_ASSOC)
                .from(educationOrganization, "getEducationOrganization", "getEducationOrganizations")
                .to(school, "getSchool", "getSchools").calledFromSource("getSchoolsAssigned")
                .calledFromTarget("getEducationOrganizationsAssigned").build();

        // create the mock collection for staffedorg association
        Collection<EntityDefinition> staffEdOrgAssocColl = new ArrayList<EntityDefinition>();
        staffEdOrgAssocColl.add(staffEdOrgAssoc);

        // create the mock collection for edorgschool association
        Collection<EntityDefinition> edOrgSchoolAssocColl = new ArrayList<EntityDefinition>();
        edOrgSchoolAssocColl.add(educationOrganizationSchoolAssoc);

        // mock the EntityDefinitionStore
        EntityDefinitionStore store = mock(EntityDefinitionStore.class);
        when(store.getLinked(staff)).thenReturn(staffEdOrgAssocColl);
        when(store.getLinked(educationOrganization)).thenReturn(edOrgSchoolAssocColl);
        associationURLCreator.setStore(store);

        // create the mock repo
        MockRepo repo = new MockRepo();
        // create the staffedorgassociation entity
        Map<String, Object> staffEdOrgAssocData = buildTestStaffEdOrgEntity();
        repo.create(ENTITY_TYPE_STAFF_EDORG_ASSOC, staffEdOrgAssocData);

        // create edorgschoolassociation entity
        Map<String, Object> edOrgSchoolAssoc = buildTestEdOrgSchoolEntity();
        repo.create(ENTITY_TYPE_EDORG_SCHOOL_ASSOC, edOrgSchoolAssoc);

        associationURLCreator.setRepo(repo);
    }

    @Test
    public void testGetAssociationUrls() {
        List<EmbeddedLink> results = new ArrayList<EmbeddedLink>();

        try {
            associationURLCreator.getAssociationUrls(staff, STAFF_ID, results, buildMockUriInfo(""));
            assertEquals("Should have 2 links", 2, results.size());

            // test the district link
            EmbeddedLink link = results.get(0);
            assertEquals("Type should be staff-edorg-association", link.getType(), ENTITY_TYPE_STAFF_EDORG_ASSOC);
            assertEquals("Should be type links", link.getRel(), "links");
            assertTrue("Returned link should point to a district", link.getHref().indexOf(ResourceConstants.RESOURCE_PATH_DISTRICT) > 0);
            assertUUID(link.getHref(), ED_ORG_ID);

            // test the school
            link = results.get(1);
            assertEquals("Type should be edorg-school-association", link.getType(), ENTITY_TYPE_EDORG_SCHOOL_ASSOC);
            assertEquals("Should be type links", link.getRel(), "links");
            assertTrue("Returned link should point to a school", link.getHref().indexOf(ResourceConstants.RESOURCE_PATH_SCHOOL) > 0);
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

        // when(mock.getRequestUri()).thenReturn(new
        // UriBuilderImpl().replaceQuery(queryString).build(null));
        return mock;
    }

    private Map<String, Object> buildTestStaffEdOrgEntity() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ResourceConstants.ENTITY_BODY_STAFF_ID, STAFF_ID);
        body.put(ResourceConstants.ENTITY_BODY_EDORG_ID, ED_ORG_ID);

        return body;
    }

    private Map<String, Object> buildTestEdOrgSchoolEntity() {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ResourceConstants.ENTITY_BODY_SCHOOL_ID, SCHOOL_ID);
        body.put(ResourceConstants.ENTITY_BODY_EDORG_ID, ED_ORG_ID);

        return body;
    }
}
