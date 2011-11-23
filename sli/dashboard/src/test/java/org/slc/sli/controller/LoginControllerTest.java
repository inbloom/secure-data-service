package org.slc.sli.controller;


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
        assertEquals(message, "SLI Dashboard Hello World!");
    }

}
