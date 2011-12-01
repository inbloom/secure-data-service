package org.slc.sli.controller.unit;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;

import org.slc.sli.controller.LoginController;

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
        assertEquals(message, "Enter Username or password");
    }
    
    
    
    @Test
    public void testLoginPageBadUserPassword() {
        ModelMap model = new ModelMap();
        
        assertEquals("login", loginController.checkLogin("", "",  model));
        String message = (String) model.get("message");
        assertEquals(message, "Enter valid Username");
    }
    
    
    @Test
    public void testLoginPageGoodUserPassword() {
        ModelMap model = new ModelMap();
        assertEquals("redirect:appselector", loginController.checkLogin("sravan", "", model));
    }

}
