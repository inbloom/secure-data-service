package org.slc.sli.controller;

import static junit.framework.Assert.*;

import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Provides a test for School Menu get, post, and delete request handling 
 * and check return view name
 * 
 * @author Dong Liu
 * 
 */

public class SchoolMenuControllerTest {
@Test
public void testDisplayMenu(){
SchoolMenuController controller = new SchoolMenuController();
Model model = new ExtendedModelMap();
assertEquals("schoolmenu", controller.displayMenu(model));
}
	
}
