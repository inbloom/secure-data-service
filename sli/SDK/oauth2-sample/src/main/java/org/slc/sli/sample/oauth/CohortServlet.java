package org.slc.sli.sample.oauth;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.sample.oauth.model.Cohorts;

/**
 * Sample servlet that returns student data.
 */
public class CohortServlet extends HttpServlet {
    
    /**
     * 
     */
    private static final long serialVersionUID = 818013212675276117L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicClient client = (BasicClient) req.getSession().getAttribute("client");
        Map<String, String> idDesc = Cohorts.getIdDesc(client);
        
        req.setAttribute("idDesc", idDesc);
        req.getRequestDispatcher("WEB-INF/cohorts.jsp").forward(req, resp);
    }
    
}
