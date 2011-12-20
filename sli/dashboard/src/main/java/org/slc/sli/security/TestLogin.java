package org.slc.sli.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ResourceBundle;

import com.google.gson.Gson;

public class TestLogin {
	public static String urlProtocol = "http";
	public static String serverAddress = "devapi1.slidev.org";
	public static int serverPort = 8080;
	public static String apiCallPrefix = "/api/rest/";

	private void getServerDetails() {
//		ResourceBundle propFile = ResourceBundle.getBundle("filter-dev");
//		String temp = propFile.get
	}
	private void testLink() throws IOException {
		URL url = new URL(urlProtocol, serverAddress, serverPort, apiCallPrefix	+ "system/session/check");
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
		System.out.println("Is user auth? " + resp.isAuthenticated() + ", redirect user to: " + resp.getRedirect_user() );
		
		if (!resp.isAuthenticated()) {
			String temp = "http://devapi1.slidev.org:8080/api/pub/realm.jsp?return=http://localhost:8080/dashboard/login";
			
		}
	}

	public static void main(String[] args) throws IOException {
		TestLogin test = new TestLogin();
		test.testLink();
	}
}