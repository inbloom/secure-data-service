package org.slc.sli.unit.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.controller.AppSelectorController;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;


public class AppSelectorControllerTest {

    private AppSelectorController appSelector;
    
    @Before
    public void setup() {
        appSelector = new AppSelectorController();
    }
    
    @Test
    public void testApplicationListNotEmpty() {
        ModelMap model = new ModelMap();
        
        ModelAndView result = appSelector.returnApps("", model);
        assertEquals(result.getViewName(), "SelectApp");
        HashMap<String, String> appToUrl = (HashMap<String, String>) model.get("appToUrl");
        assertTrue(appToUrl.size() > 0);
    }
    
}
