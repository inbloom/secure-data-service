package net.wgen.sli;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.security.Principal;

import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


public class HelloSpring implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
         Principal p = request.getUserPrincipal();
         if (p == null) {
             throw new Exception("You must login to view the account list (Spring Security message)"); // only for Spring Security managed authentication
         }
        String param = "unset";

        HttpSession session = request.getSession();
 		if (session != null) {
 			Object o = session.getAttribute("authDirectory");
 			param = (String)o;
 		}

        // Actual business logic
        ModelAndView mav = new ModelAndView("helloSpring");
        mav.addObject("princ_name", p.getName());
        mav.addObject("directory", param);
        return mav;
    }

}
