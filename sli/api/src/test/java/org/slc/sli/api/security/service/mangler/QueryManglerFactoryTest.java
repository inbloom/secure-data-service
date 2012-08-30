package org.slc.sli.api.security.service.mangler;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.security.service.SecurityCriteria;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
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
public class QueryManglerFactoryTest {
    @Autowired
    private QueryManglerFactory factory;
        
    @Test
    public void testGetManglerForStudent() {
        NeutralQuery query = new NeutralQuery();
        SecurityCriteria criteria = new SecurityCriteria();
        criteria.setCollectionName("student");
        criteria.setSecurityCriteria(new NeutralCriteria("_id", NeutralCriteria.CRITERIA_IN, new ArrayList<String>()));
        Mangler chosenMangler = factory.getMangler(query, criteria);
        assertTrue(chosenMangler != null);
        assertTrue(chosenMangler.respondsTo("student"));
    }
    
    @Test
    public void testGetManglerForSection() {
        assertFalse(true);
    }
    
    @Test
    public void testGetManglerForInvalidParameter() {
        assertFalse(true);
    }
    
}
