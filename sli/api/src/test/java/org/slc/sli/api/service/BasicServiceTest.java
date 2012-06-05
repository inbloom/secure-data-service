package org.slc.sli.api.service;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.config.DefinitionFactory;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 *
 * Unit Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class BasicServiceTest {
    private BasicService service = null; //class under test

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SecurityContextInjector securityContextInjector;

    @Autowired
    DefinitionFactory factory;


    @Before
    public void setup() {
        service = (BasicService) context.getBean("basicService", "student", null,
                Right.READ_GENERAL, Right.WRITE_GENERAL);

        EntityDefinition student = factory.makeEntity("student")
                .exposeAs("students").build();

        service.setDefn(student);
    }

    @Test
    public void testCheckFieldAccessAdmin() {
        // inject administrator security context for unit testing
        securityContextInjector.setAdminContextWithElevatedRights();

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("economicDisadvantaged", "=", "true"));

        NeutralQuery query1 = new NeutralQuery(query);

        service.checkFieldAccess(query);
        assertTrue("Should match", query1.equals(query));
    }

    @Test (expected = QueryParseException.class)
    public void testCheckFieldAccessEducator() {
        // inject administrator security context for unit testing
        securityContextInjector.setEducatorContext();

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("economicDisadvantaged", "=", "true"));

        service.checkFieldAccess(query);
    }


}
