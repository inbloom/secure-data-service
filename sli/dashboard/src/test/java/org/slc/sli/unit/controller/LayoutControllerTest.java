package org.slc.sli.unit.controller;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.controller.GenericLayoutController;
import org.slc.sli.entity.Config;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.util.StudentProgramUtil;
import org.slc.sli.manager.ConfigManager;
import org.slc.sli.manager.component.impl.CustomizationAssemblyFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-context.xml"})
public class LayoutControllerTest {

    @Autowired
    ApplicationContext applicationContext;
    
    @Autowired
    ConfigManager configManager;
    
    CustomizationAssemblyFactoryImpl dataFactory = new CustomizationAssemblyFactoryImpl() {
        
        protected String getTokenId() {
            return "1";
        }
        
        protected String getUsername() {
            return "lkim";
        }
        
        protected GenericEntity getDataComponent(String componentId, Object entityKey, Config.Data config) {
            GenericEntity simpleMaleStudentEntity = new GenericEntity();
            simpleMaleStudentEntity.put("id", "1");
            simpleMaleStudentEntity.put("gender", "male");
            simpleMaleStudentEntity.put("gradeNumeric", 5);
            return simpleMaleStudentEntity;
        }
    };
    
    class LayoutControllerMock extends GenericLayoutController{
        public ModelAndView handleStudentProfile(String id) {
            String TABBED_ONE_COL = "tabbed_one_col";
            ModelMap model = getPopulatedModel("simpleLayout", id);
            // TODO: get rid of StudentProgramUtil - instead enrich student entity with relevant programs 
            model.addAttribute("programUtil", new StudentProgramUtil());
            return getModelView(TABBED_ONE_COL, model);
        }

        public String getUsername() {
            return "lkim";
        }        
        
        public void populateModelLegacyItems(ModelMap model) {
            
        }
        
    }
    
    
    private AnnotationMethodHandlerAdapter handlerAdapter;
    
        
    private LayoutControllerMock layoutController;
    
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    //private HandlerAdapter handlerAdapter;
    //private LayoutController layoutController;
    
    
    @Before
    public void setUp() throws Exception {
        layoutController = new LayoutControllerMock();
        dataFactory.setConfigManager(configManager);
        layoutController.setCustomizedDataFactory(dataFactory);
        layoutController.setConfigManager(configManager);
        
    }
    
    
    /*
     * TODO: Remove this test
     * This test is going to be removed when the controllers are rrefactored
     */
    @Test
    public void testLayoutController() throws Exception {
        
        System.out.println(layoutController.handleStudentProfile("1"));
   }
    
}
