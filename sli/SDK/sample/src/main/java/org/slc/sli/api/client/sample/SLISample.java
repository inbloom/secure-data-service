package org.slc.sli.api.client.sample;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.impl.BasicClient;

/**
 * Basic example of using the SLI SDK to connect to an SLI API service instance and
 * performing CRUD operations.
 */
public class SLISample extends HttpServlet {
    
    private static final String CLIENT_SECRET_PARAM = "clientSecret";
    
    private static final String CLIENT_ID_PARAM = "clientId";
    
    private static final String API_SERVER_URL_PARAM = "apiServerURL";
    
    private static final String CALLBACK_URL_PARAM = "callbackURL";
    
    private static final String SLI_SAMPLE_LOG_NAME = "SLISample";
    
    private static final long serialVersionUID = 3801043536064373956L;
    
    static private Logger logger = Logger.getLogger(SLI_SAMPLE_LOG_NAME);
    
    private SLIClient client = null;
    private String authCode = null;
    private String callbackURLString = null;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        callbackURLString = getInitParameter(CALLBACK_URL_PARAM);
        try {
            client = new BasicClient(new URL(getInitParameter(API_SERVER_URL_PARAM)),
                    getInitParameter(CLIENT_ID_PARAM), getInitParameter(CLIENT_SECRET_PARAM),
                    new URL(callbackURLString));
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "Failed to initialize SLISample servlet: {}", e.toString());
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        
        if (request.getRequestURI().equals(callbackURLString)) {
            logger.log(Level.INFO, "Got callback from OAUTH: {})", request.toString());
            
        } else if (authCode == null) {
            response.sendRedirect(client.getLoginURL().toString());
            
        }
    }
}
