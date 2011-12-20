package org.slc.sli.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


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
    
	public String urlProtocol = "http";
	public String serverAddress = "devapi1.slidev.org";
	public int serverPort = 8080;
	public String apiCallPrefix = "/api/rest/";
	private String loginPageUrl;

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
		loginPageUrl = mainProp.getProperty("security.login.page.url", "/pub/realm.jsp?return=");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	private String forwardToAM(ModelMap model) throws IOException {
		getServerDetails();
//		URL url = new URL(urlProtocol, serverAddress, serverPort, apiCallPrefix	+ "system/session/check");
		System.out.println("model keyset = " + model.keySet());
		
		String securityLoginUrl = urlProtocol + "://" + serverAddress + ":" + serverPort + "/api" + loginPageUrl + "http://localhost:8080/dashboard/login";
//		return "redirect:http://devapi1.slidev.org:8080/api/pub/realm.jsp?return=http://localhost:8080/dashboard/login";
		return "redirect:" + securityLoginUrl;
	}
}
