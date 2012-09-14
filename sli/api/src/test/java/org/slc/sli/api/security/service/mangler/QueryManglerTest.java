package org.slc.sli.api.security.service.mangler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
public class QueryManglerTest {

    
    @Test
    public void testMangleListQuery() {
        DefaultQueryMangler mangler = new DefaultQueryMangler();
        NeutralQuery query = new NeutralQuery();
        NeutralCriteria baseCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"1", "2", "3"}));
        SecurityCriteria securityCriteria = new SecurityCriteria();
        // Test generic list query
        securityCriteria.setCollectionName("students");
        securityCriteria.setSecurityCriteria(baseCriteria);
        NeutralQuery finalQuery = mangler.mangleQuery(query, baseCriteria);
        assertTrue(finalQuery.getCriteria().size() == 0);
        assertTrue(finalQuery.getOrQueries().size() == 1);
        assertEquals(finalQuery.getOrQueries().get(0).getCriteria().get(0), baseCriteria);
    }
    
    @Test
    public void testMangleSpecificQuery() {
        DefaultQueryMangler mangler = new DefaultQueryMangler();
        NeutralQuery query = new NeutralQuery();
        NeutralCriteria baseCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"1", "2", "3"}));
        query.addCriteria(baseCriteria);
        NeutralCriteria secureCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"1", "2"}));
        SecurityCriteria securityCriteria = new SecurityCriteria();
        // Test generic list query
        securityCriteria.setCollectionName("students");
        securityCriteria.setSecurityCriteria(secureCriteria);
        NeutralQuery finalQuery = mangler.mangleQuery(query, secureCriteria);
        assertTrue(finalQuery.getCriteria().size() == 0);
        assertTrue(finalQuery.getOrQueries().size() == 1);
        assertEquals(finalQuery.getOrQueries().get(0).getCriteria().get(0), baseCriteria);
    }
    
    @Test
    public void testMangleSpecificFailedQuery() {
        DefaultQueryMangler mangler = new DefaultQueryMangler();
        NeutralQuery query = new NeutralQuery();
        NeutralCriteria baseCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"1", "2", "3"}));
        query.addCriteria(baseCriteria);
        NeutralCriteria secureCriteria = new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, Arrays.asList(new String[] {"4", "5"}));
        SecurityCriteria securityCriteria = new SecurityCriteria();
        // Test generic list query
        securityCriteria.setCollectionName("students");
        securityCriteria.setSecurityCriteria(secureCriteria);
        NeutralQuery finalQuery = mangler.mangleQuery(query, secureCriteria);
        assertEquals(finalQuery.getOrQueries().size(), 0);
    }
    
    
}
