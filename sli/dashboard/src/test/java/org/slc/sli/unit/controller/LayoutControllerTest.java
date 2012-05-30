package org.slc.sli.unit.controller;

import java.util.Collection;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.util.Constants;
import org.slc.sli.web.controller.LayoutController;

/**
 * Tesing layout controller
 *
 * @author svankina
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml", "/dashboard-servlet-test.xml" })
public class LayoutControllerTest extends ControllerTestBase  {
    LayoutController layoutController = new LayoutController() {
        @Override
        public boolean isAdmin() {
            return false;
        }
    };;

    @Before
    public void setup() throws Exception {
        setCustomizationAssemblyFactory(layoutController);
        setPortalWS(layoutController);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHandle() throws Exception {
        try {
            ModelAndView mv = layoutController.handle("simpleLayout", null, request);
            Assert.assertEquals(2, ((Map<String, Config>) mv.getModel().get(Constants.MM_KEY_VIEW_CONFIGS)).size());
            Assert.assertEquals(1, ((Map<String, GenericEntity>) mv.getModel().get(Constants.MM_KEY_DATA)).size());
            Assert.assertEquals(1, ((Collection<Config>) mv.getModel().get(Constants.MM_KEY_LAYOUT)).size());
        } catch (Exception e) {
            Assert.fail("Should pass but getting " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHandleWithId() throws Exception {
        try {
            ModelAndView mv = layoutController.handleWithId("simpleLayout", null, request);
            ModelAndView mv1 = layoutController.handle("simpleLayout", null, request);
            Map<String, Config> c = (Map<String, Config>) mv.getModel().get(Constants.MM_KEY_VIEW_CONFIGS);
            Map<String, Config> c1 = (Map<String, Config>) mv1.getModel().get(Constants.MM_KEY_VIEW_CONFIGS);
            Assert.assertEquals(2, c.size());
            Assert.assertEquals(2, c1.size());
            for (Map.Entry<String, Config> conf : c.entrySet()) {
                Assert.assertEquals(conf.getValue().getName(), c1.get(conf.getKey()).getName());
            }
        } catch (Exception e) {
            Assert.fail("Should pass but getting " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testHandleLos() throws Exception {
        try {
            ModelAndView mv = layoutController.handleLos(null, request);
            Assert.assertEquals(2, ((Map<String, Config>) mv.getModel().get(Constants.MM_KEY_VIEW_CONFIGS)).size());
        } catch (Exception e) {
            Assert.fail("Should pass but getting " + e.getMessage());
        }
    }
}
