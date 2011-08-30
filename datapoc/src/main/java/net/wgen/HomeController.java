package net.wgen;

import java.util.List;

import net.wgen.domain.Student;
import net.wgen.service.StudentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Sample controller for going to the home page with a message
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory
			.getLogger(HomeController.class);
	
	@Autowired
	private StudentService studentService;

	/**
	 * Selects the home page and populates the model with a message
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		logger.info("Welcome home!");
		model.addAttribute("controllerMessage",
				"This is the message from the controller!");
		return "home";
	}

	@RequestMapping(value = "/list/students", method = RequestMethod.GET)

	public String list(Model model) {
		List<Student> students = studentService.findAll();
		logger.info( "Listing students: " + students );
		
		model.addAttribute("students", students );
		return "list";
	}

}
