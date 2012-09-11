package org.slc.sli.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/hello")
public class HelloResource {
    
    // Logging
    private static Logger log = LoggerFactory.getLogger(HelloResource.class);
    
    
    // Static Attributes
    public static String MESSAGE = "Hello Jersey";
    public static String XML_HEADER = "<?xml version=\"1.0\"?><hello>";
    public static String XML_FOOTER = "</hello>";
    public static String HTML_TITLE_HEADER = "<html><title>";
    public static String HTML_TITLE_FOOTER = "</title>";
    public static String HTML_BODY_HEADER = "<body><h1>";
    public static String HTML_BODY_FOOTER = "</h1></body></html>";
    
    
    // Methods
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayPlainTextHello() {
        log.info("Content Type: text/plain");
        
        return MESSAGE;
    }
    
    @GET
    @Produces(MediaType.TEXT_XML)
    public String sayXMLHello() {
        log.info("Content Type: text/xml");
        
        return XML_HEADER + MESSAGE + XML_FOOTER;
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String sayHtmlHello() {
        log.info("Content Type: text/html");
        
        return HTML_TITLE_HEADER + MESSAGE + HTML_TITLE_FOOTER + HTML_BODY_HEADER + MESSAGE + HTML_BODY_FOOTER;
    }
    
}