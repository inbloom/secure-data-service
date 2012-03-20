package org.slc.sli.sample.oauth;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.sample.oauth.model.Students;

/**
 * Sample servlet that returns student data.
 */
public class StudentServlet extends HttpServlet {
    
    /**
     * 
     */
    private static final long serialVersionUID = 818013212675276117L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicClient client = (BasicClient) req.getSession().getAttribute("client");
        List<String> names = Students.getNames(client);
        
        Map<String, Integer> grades = new HashMap<String, Integer>();
        for (String name : names) {
            grades.put(name, Students.getGrade(client, name));
        }
        
        req.setAttribute("grades", grades);
        req.getRequestDispatcher("WEB-INF/students.jsp").forward(req, resp);
    }
    
}
