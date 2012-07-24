package org.slc.sli.sif.zis;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestHandler;

public class MockZisRequestHandler implements HttpRequestHandler {
    
    @Autowired
    private MockZis mockZis;
    
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        if (request.getMethod().equals("GET")) {
            doGet(request, response);
        } else if (request.getMethod().equals("POST")) {
            doPost(request, response);
        }
    }
    
    private void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        System.out.println("GET MockZis");
        
        resp.setContentType("text/xml");
        
        PrintWriter out = resp.getWriter();
        out.print(mockZis.createAckString());
        out.flush();
        out.close();
    }
    
    /**
     * SIF message POSTS: respond with a hard coded SIF_ACK message (sufficient for openADK agent
     * connect).
     */
    private void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        InputStream xml = req.getInputStream();
        
        StringWriter writer = new StringWriter();
        IOUtils.copy(xml, writer, "UTF-8");
        String xmlString = writer.toString();
        
        //parse string
        
        System.out.println("POST MockZis with MESSAGE: \n" + xmlString);
        //mockZis.parseSIFMessage(xmlString);
        
        resp.setContentType("text/xml");
        
        PrintWriter out = resp.getWriter();
        out.print(mockZis.createAckString());
        out.flush();
        out.close();
        
    }
}
