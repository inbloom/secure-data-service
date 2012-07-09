package zis;

import java.io.IOException;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
/**
 * Mock ZIS POC
 * 
 * @author jtully
 *
 */
public class MockZisServlet extends HttpServlet {
     
    private static final long serialVersionUID = 1031422249396784970L;

    private static final String ACK_MESSAGE ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<SIF_Message Version=\"2.3\" xmlns=\"http://www.sifinfo.org/infrastructure/2.x\">" +
            "<SIF_Ack><SIF_Header><SIF_MsgId>8BA61F8370EC0B8907047D80413A12D0</SIF_MsgId><SIF_Timestamp>2012-07-09T09:43:04</SIF_Timestamp><SIF_SourceId>TestZone</SIF_SourceId></SIF_Header><SIF_OriginalSourceId>test.agent</SIF_OriginalSourceId><SIF_OriginalMsgId>A23509D6B5434CC5BB6D78BAF5F08BBE</SIF_OriginalMsgId><SIF_Status><SIF_Code>0</SIF_Code></SIF_Status>" +
            "</SIF_Ack>" +
            "</SIF_Message>";
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
         
    	System.out.println("GET MockZis");
    	
        resp.setContentType("text/xml");
         
        PrintWriter out = resp.getWriter();
        out.print(ACK_MESSAGE);
        out.flush();
        out.close();
    }
    
    /**
     * SIF message POSTS: respond with a hard coded SIF_ACK message (sufficient for openADK agent connect).
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        InputStream xml = req.getInputStream();
        
        StringWriter writer = new StringWriter();
        IOUtils.copy(xml, writer, "UTF-8");
        String xmlString = writer.toString();
        
        System.out.println("POST MockZis with MESSAGE: \n" + xmlString);
    	
        resp.setContentType("text/xml");
         
        PrintWriter out = resp.getWriter();
        out.print(ACK_MESSAGE);
        out.flush();
        out.close();
        
    }
    
}
    