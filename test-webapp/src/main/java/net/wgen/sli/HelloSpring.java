package net.wgen.sli;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


public class HelloSpring implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Security check (this is unnecessary if Spring Security is performing the authorization)
//        if (request.getUserPrincipal() == null) {
//            throw new AuthenticationCredentialsNotFoundException("You must login to view the account list (Spring Security message)"); // only for Spring Security managed authentication
//        }

        // Actual business logic
        ModelAndView mav = new ModelAndView("helloSpring");
        mav.addObject("accounts", "Hello, Spring World.");
        return mav;
    }

}
