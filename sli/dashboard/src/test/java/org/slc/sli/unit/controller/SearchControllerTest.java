package org.slc.sli.unit.controller;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.web.controller.SearchController;
import org.slc.sli.web.entity.StudentSearch;

/**
 * search test
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml", "/dashboard-servlet-test.xml" })
public class SearchControllerTest extends ControllerTestBase  {
    SearchController searchController = new SearchController() {
        @Override
        public boolean isAdmin() {
            return false;
        }
    };

    @Before
    public void setup() throws Exception {
        setCustomizationAssemblyFactory(searchController);
        setPortalWS(searchController);
    }

    @Test
    public void testHandle() throws Exception {
        // test good search
        try {
            Assert.assertNotNull(searchController.handle(new StudentSearch("M", "S"), request));
        } catch (Exception e) {
            Assert.fail("Should pass but getting " + e.getMessage());
        }
    }
}
