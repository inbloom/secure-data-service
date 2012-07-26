package org.slc.sli.api.selectors.doc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.selectors.model.*;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.modeling.uml.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests
 *
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultSelectorDocumentTest {

    @Autowired
    DefaultSelectorDocument defaultSelectorDocument;

    private ModelProvider provider;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private MockRepo repo;

    final static String TEST_XMI_LOC = "/sliModel/test_SLI.xmi";

    private Entity student1;
    private Entity student2;


    @Before
    public void setup() {
        provider = new ModelProvider(TEST_XMI_LOC);

        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        student1 = repo.create("student", createStudentEntity("1234"));
        student2 = repo.create("student", createStudentEntity("5678"));

        Entity section1 = repo.create("section", createSectionEntity("Math 1"));
        Entity section2 = repo.create("section", createSectionEntity("English 1"));

        repo.create("studentSectionAssociation", createStudentSectionAssociationEntity(student1.getEntityId(),
                section1.getEntityId()));

        repo.create("studentSectionAssociation", createStudentSectionAssociationEntity(student2.getEntityId(),
                section2.getEntityId()));
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void test() {

        List<String> ids = new ArrayList<String>();
        ids.add(student1.getEntityId());

        Constraint constraint = new Constraint();
        constraint.setKey("_id");
        constraint.setValue(ids);

        defaultSelectorDocument.aggregate(createQueryPlan(), constraint);
    }

    private Map<Type, SelectorQueryPlan> createQueryPlan() {
        Map<Type, SelectorQueryPlan> result = new HashMap<Type, SelectorQueryPlan>();

        result.put(provider.getClassType("Student"), getSelectorQueryPlan());

        return result;
    }

    private SelectorQueryPlan getSelectorQueryPlan() {
        SelectorQueryPlan plan = mock(SelectorQueryPlan.class);
        NeutralQuery query = new NeutralQuery();
        query.setIncludeFields("name");

        List<Object> childQueries = getChildQueries();

        when(plan.getQuery()).thenReturn(query);
        when(plan.getChildQueryPlans()).thenReturn(childQueries);

        return plan;
    }

    private List<Object> getChildQueries() {
        List<Object> list = new ArrayList<Object>();

        NeutralQuery query = new NeutralQuery();
        query.setIncludeFields("sectionId");

        SelectorQueryPlan plan = mock(SelectorQueryPlan.class);
        when(plan.getQuery()).thenReturn(query);

        Map<Type, SelectorQueryPlan> map = new HashMap<Type, SelectorQueryPlan>();
        map.put(provider.getClassType("StudentSectionAssociation"), plan);

        list.add(map);

        return list;
    }

    private EntityBody createStudentEntity(String studentUniqueStateId) {
        EntityBody entity = new EntityBody();
        entity.put("name", "somename");
        entity.put("studentUniqueStateId", studentUniqueStateId);

        return entity;
    }

    private EntityBody createSectionEntity(String sectionName) {
        EntityBody entity = new EntityBody();
        entity.put("field", 1);
        entity.put("sectionName", sectionName);

        return entity;
    }

    private EntityBody createStudentSectionAssociationEntity(String studentId, String sectionId) {
        EntityBody entity = new EntityBody();
        entity.put("studentId", studentId);
        entity.put("sectionId", sectionId);

        return entity;
    }

}
