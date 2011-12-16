package org.slc.sli.unit.controller;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.controller.LoginController;

/**
 * TODO: Write Javadoc
 * 
 */
public class LoginControllerTest {

    private LoginController loginController;

    @Before
    public void setup() {
        loginController = new LoginController();
    }

    @Test
    public void testLoginPageContainsText() {
        ModelMap model = new ModelMap();
        assertEquals("login", loginController.printWelcome(model));
        String message = (String) model.get("message");
        assertEquals(message, "Enter Username and password");
    }
    
    
    
    @Test
    public void testLoginPageBadUserPassword() {
        ModelMap model = new ModelMap();
        ModelAndView result = loginController.checkLogin("", "",  model);
        assertEquals("login", result.getViewName());
        String errorMessage = (String) model.get("errorMessage");
        assertEquals(errorMessage, "Invalid Username or password, please try again");
        String displayMessage = (String) model.get("displayError");
        assertEquals(displayMessage, "block");
    }
    
    
    @Test
    public void testLoginPageGoodUserPassword() {
        ModelMap model = new ModelMap();
        ModelAndView result = loginController.checkLogin("sravan", "", model);
        assertEquals("redirect:appselector", result.getViewName());
    }

}
