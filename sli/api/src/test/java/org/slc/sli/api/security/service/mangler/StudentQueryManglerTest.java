package org.slc.sli.api.security.service.mangler;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.security.service.SecurityCriteria;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentQueryManglerTest {

    
    @Test
    public void testMangleListQuery() {
        StudentQueryMangler mangler = new StudentQueryMangler();
        NeutralQuery query = new NeutralQuery();
        NeutralCriteria baseCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"1", "2", "3"}));
        SecurityCriteria securityCriteria = new SecurityCriteria();
        // Test generic list query
        securityCriteria.setCollectionName("students");
        securityCriteria.setSecurityCriteria(baseCriteria);
        NeutralQuery finalQuery = mangler.mangleQuery(query, baseCriteria);
        assertTrue(finalQuery.getCriteria().size() == 1);
        assertTrue(finalQuery.getCriteria().get(0).equals(baseCriteria));        
    }
    
    @Test
    public void testMangleSpecificQuery() {
        StudentQueryMangler mangler = new StudentQueryMangler();
        NeutralQuery query = new NeutralQuery();
        NeutralCriteria baseCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"1", "2", "3"}));
        query.addCriteria(baseCriteria);
        NeutralCriteria secureCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"1", "2"}));
        SecurityCriteria securityCriteria = new SecurityCriteria();
        // Test generic list query
        securityCriteria.setCollectionName("students");
        securityCriteria.setSecurityCriteria(secureCriteria);
        NeutralQuery finalQuery = mangler.mangleQuery(query, secureCriteria);
        assertEquals(finalQuery.getCriteria().get(0), baseCriteria);
        assertTrue(finalQuery.getCriteria().size() == 1);
    }
    
    @Test
    public void testMangleSpecificFailedQuery() {
        StudentQueryMangler mangler = new StudentQueryMangler();
        NeutralQuery query = new NeutralQuery();
        NeutralCriteria baseCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"1", "2", "3"}));
        query.addCriteria(baseCriteria);
        NeutralCriteria secureCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"4", "5"}));
        SecurityCriteria securityCriteria = new SecurityCriteria();
        // Test generic list query
        securityCriteria.setCollectionName("students");
        securityCriteria.setSecurityCriteria(secureCriteria);
        NeutralQuery finalQuery = mangler.mangleQuery(query, secureCriteria);
        assertEquals(finalQuery, null);
    }
    
    @Test
    public void testRespondsTo() {
        StudentQueryMangler mangler = new StudentQueryMangler();
        assertTrue(mangler.respondsTo("student"));
        assertFalse(mangler.respondsTo("Waffles"));
    }
    
}
