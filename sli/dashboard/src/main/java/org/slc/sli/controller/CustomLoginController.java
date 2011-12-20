package org.slc.sli.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.slc.sli.security.SecurityResponse;


/**
 * 
 * TODO: Write Javadoc
 *
 */
@Controller
@RequestMapping("/customlogin")
public class CustomLoginController {

//    @RequestMapping(method = RequestMethod.GET)
//    public ModelAndView returnApps(String username, ModelMap model) {
//        model.addAttribute("message", "Select an application");
//        HashMap<String, String> appToUrlMap = new HashMap<String, String>();
//
//        //TODO: Retrieve the applications from a service
//        appToUrlMap.put("Dashboard", "studentlist");
//        appToUrlMap.put("FakeApp", "/fakeapp");
//        model.addAttribute("appToUrl", appToUrlMap);
//        return new ModelAndView("SelectApp").addObject("username", username);
//    }
    
	private String urlProtocol = "http";
	private String serverAddress = "devapi1.slidev.org";
	private int serverPort = 8080;
	private String apiCallPrefix = "/api";
	private String loginPageUrl;
	private String callBackPageUrlPrefix;
	private String dashboardLandingPage = "http://localhost:8080/dashboard/login";

	private void getServerDetails() throws IOException {
		// TODO: Write something to search all bundle files, according to current env
		Properties mainProp = new Properties();
		try {
			FileInputStream propsFile = new FileInputStream("main.properties");
			mainProp.load(propsFile);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
//		ResourceBundle propFile = ResourceBundle.getBundle("filter-dev");
//		ResourceBundle mainPropFile = ResourceBundle.getBundle("main");
		urlProtocol = mainProp.getProperty("security.protocol", "http");
		serverAddress = mainProp.getProperty("security.server.address", "devapi1.slidev.org");
		serverPort = Integer.valueOf(mainProp.getProperty("security.port", "8080")).intValue();
		callBackPageUrlPrefix = mainProp.getProperty("security.login.page.url", "?return=");
		apiCallPrefix = mainProp.getProperty("api.call.prefix", "/api");
		dashboardLandingPage = mainProp.getProperty("dashboard.landing.page", "http://localhost:8080/dashboard/login");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	private String forwardToAM(ModelMap model, HttpServletRequest request) throws IOException {
		String returnValue = "";
		
		// Get server links and address from external file
		getServerDetails();
		
		// Check if the user is currently auth or not
		if (!isUserAuthenticated()) {
			// Get the original URL call - so we can return to that page after auth
			String callBackURL = request.getHeader("referer");
			
			String securityLoginUrl = loginPageUrl + callBackPageUrlPrefix + 
					((callBackURL == null) ? dashboardLandingPage : callBackURL);
			returnValue = "redirect:" + securityLoginUrl;
		}
		else {
			// If the user already authenticated - transfer to list of students
			// TODO: take this from external file
			// TODO: do we need to add jetty/tomcat session id here?
			returnValue = "studentlist";
		}
		return returnValue;
	}

	private boolean isUserAuthenticated() throws IOException {
		URL url = new URL(urlProtocol, serverAddress, serverPort, apiCallPrefix	+ "/rest/system/session/check");
		URLConnection connection = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		Gson gson = new Gson();
		SecurityResponse resp = gson.fromJson(response.toString(), SecurityResponse.class);
		loginPageUrl = resp.getRedirect_user();
		System.out.println("Is user auth? " + resp.isAuthenticated() + ", redirect user to: " + resp.getRedirect_user() );
		
		return resp.isAuthenticated();
	}
}
