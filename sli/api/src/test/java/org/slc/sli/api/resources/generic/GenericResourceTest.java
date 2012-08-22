package org.slc.sli.api.resources.generic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests
 *
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class GenericResourceTest {

    @Autowired
    @Qualifier("onePartResource")
    private GenericResource resource;

    @Test
    public void dumb() {
        assertTrue(true);
    }
}
