package org.slc.sli.controller;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Provides a test for Student Menu get, post, and delete request handling
 * and check return view name
 * 
 * @author Dong Liu
 * 
 */

public class StudentMenuControllerTest {
    
    @Test
    public void testDisplayMenu() {
        StudentMenuController controller = new StudentMenuController();
        Model model = new ExtendedModelMap();
        assertEquals("studentmenu", controller.displayMenu(model));
    }
}