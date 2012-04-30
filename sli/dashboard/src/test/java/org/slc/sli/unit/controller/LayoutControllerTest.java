package org.slc.sli.unit.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.controller.GenericLayoutController;
import org.slc.sli.entity.Config;
import org.slc.sli.entity.Config.Data;
import org.slc.sli.entity.CustomConfig;
import org.slc.sli.entity.EdOrgKey;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.util.StudentProgramUtil;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.UserEdOrgManager;
import org.slc.sli.manager.component.impl.CustomizationAssemblyFactoryImpl;

/**
 * Tesing layout controller
 *
 * @author svankina
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-context.xml" })
public class LayoutControllerTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ConfigManager configManager;

    CustomizationAssemblyFactoryImpl dataFactory = new CustomizationAssemblyFactoryImpl() {

        @Override
        protected String getTokenId() {
            return "1";
        }

        @Override
        public GenericEntity getDataComponent(String componentId, Object entityKey, Config.Data config) {
            GenericEntity simpleMaleStudentEntity = new GenericEntity();
            simpleMaleStudentEntity.put("id", "1");
            simpleMaleStudentEntity.put("gender", "male");
            simpleMaleStudentEntity.put("gradeNumeric", 5);
            return simpleMaleStudentEntity;
        }
    };

    /**
     * Mock class extension for testing
     *
     */
    class LayoutControllerMock extends GenericLayoutController {
        public ModelAndView handleStudentProfile(String id) {
            String tabbedOneCol = "tabbed_one_col";
            ModelMap model = getPopulatedModel("simpleLayout", id, null);
            // TODO: get rid of StudentProgramUtil - instead enrich student entity with relevant
            // programs
            model.addAttribute("programUtil", new StudentProgramUtil());
            return getModelView(tabbedOneCol, model);
        }

        @Override
        protected void setContextPath(ModelMap model, HttpServletRequest request) {
        }

        public String getUsername() {
            return "lkim";
        }

        @Override
        public String getToken() {
            return "";
        }

        @Override
        public void populateModelLegacyItems(ModelMap model) {

        }

        @Override
        protected void addHeaderFooter(ModelMap map) {

        }

    }

    private LayoutControllerMock layoutController;

    @Before
    public void setUp() throws Exception {
        layoutController = new LayoutControllerMock();
        dataFactory.setConfigManager(configManager);
        dataFactory.setUserEdOrgManager(new UserEdOrgManager() {

            @Override
            public List<GenericEntity> getUserInstHierarchy(String token) {
                return null;
            }

            @Override
            public EdOrgKey getUserEdOrg(String token) {
                return new EdOrgKey("fake");
            }

            @Override
            public GenericEntity getUserInstHierarchy(String token, Object key, Data config) {
                return null;
            }
            
            @Override
            public CustomConfig getCustomConfig(String token) {
                return null;
            }
            
            @Override
            public void putCustomConfig(String token, String customConfigJson) {
            }
        });
        layoutController.setCustomizedDataFactory(dataFactory);
    }

    /*
     * TODO: Remove this test
     * This test is going to be removed when the controllers are rrefactored
     */
    @Test
    public void testLayoutController() throws Exception {

        Assert.assertNotNull(layoutController.handleStudentProfile("1"));
    }

}
