package net.wgen.sli;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

import java.security.Principal;
import java.io.*;

import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class dispatcher implements Controller {


    private ModelAndView returnWSDL(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ServletContext servletContext = request.getSession().getServletContext();

        String file = "resource/slitest.simple.asmx.wsdl";
        File uFile = new File(servletContext.getRealPath("/"), file);
        int fSize = (int) uFile.length();

        if (fSize > 0) {

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(uFile));
        String mimetype = servletContext.getMimeType(file);

        response.setBufferSize(fSize);
        response.setContentType(mimetype);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file + "\"");
        response.setContentLength(fSize);

        FileCopyUtils.copy(in, response.getOutputStream());
        in.close();
        response.getOutputStream().flush();
        response.getOutputStream().close();
        } else {
        response.setContentType("text/html");
        PrintWriter printwriter = response.getWriter();
        printwriter.println("<html>");
        printwriter.println("<br><br><br><h2>Could not get file name:<br>" + file + "</h2>");
        printwriter.println("<br><br><br><center><h3><a href='javascript: history.go(-1)'>Back</a></h3></center>");
        printwriter.println("<br><br><br>&copy; webAccess");
        printwriter.println("</html>");
        printwriter.flush();
        printwriter.close();
        }

        return null;        
    }

    private boolean checkToken(String username, String ipAddress, String token) {
        if(username.equals("jdoe")) {
            return true;
        } 
        return false;
    }

    private ModelAndView soapRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Logger log = Logger.getLogger(dispatcher.class.getName());
		
		// get soap message
		String data=null;
		InputStream is = req.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int letti;
		while ((letti = is.read(buf)) > 0)
		{
			baos.write(buf, 0, letti);	
		}
		data = new String(baos.toByteArray()); 

        log.info("Request payload <" + data + ">");
		
		String username = "";
		String token = "";
		String sourceIp = "";
		Pattern pattern;
		Matcher matcher;
		
		// find google username
		pattern = Pattern.compile("<ns0:username>([^<]*)</ns0:username>");
		matcher = pattern.matcher(data);
		
		// should only return one
		while (matcher.find())
		{
			username = data.substring((matcher.start()+14), (matcher.end()-15));
			log.info("Using username <" + username + ">");
		}

		pattern = Pattern.compile("<ns0:password>([^<]*)</ns0:password>");
		matcher = pattern.matcher(data);
		
		// should only return one
		while (matcher.find()) {
			token = data.substring((matcher.start()+14), (matcher.end()-15));
//			log.info("Using token <" + token + ">");
		}

		pattern = Pattern.compile("<sourceIp>([^<]*)</sourceIp>");
		matcher = pattern.matcher(data);
		
		// should only return one
		while (matcher.find()) {
			sourceIp = data.substring((matcher.start()+10), (matcher.end()-11));
			log.info("Using sourceIp <" + sourceIp + ">");
		}

	    resp.setContentType("text/xml");
	    
	    String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> ";
	    response = response + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"; 
	    response = response + "<soapenv:Body>";
	    response = response + " <AuthenticateResponse ";
	    response = response + "xmlns=\"urn:authentication.soap.sforce.com\"> ";
	    
	    // is token valid
		if (checkToken(username, sourceIp, token) == true)
		{
			response = response + "  <Authenticated>true</Authenticated> ";			
		}
		else
		{
			response = response + "  <Authenticated>false</Authenticated> ";			
		}
		response = response + " </AuthenticateResponse> ";
		response = response + "</soapenv:Body> ";
		response = response + "</soapenv:Envelope>";

		log.info("Response <" + response + ">");

        PrintWriter printwriter = resp.getWriter();
		printwriter.println(response);
        printwriter.flush();
        printwriter.close();

        return null;
    }

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String qs = request.getQueryString();
        
        if(qs != null && qs.toLowerCase().equals("wsdl")) {
            return returnWSDL(request, response);
        } else {
            return soapRequest(request, response);
        }
    }

}
