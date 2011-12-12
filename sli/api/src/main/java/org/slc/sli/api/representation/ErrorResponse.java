package org.slc.sli.api.representation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Representation of an error message in response to issues encountered servicing API requests.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * 
 */
@XmlRootElement(name = "error")
public class ErrorResponse {
    @XmlElement(name = "code")
    @JsonProperty("code")
    int statusCode;
    
    @XmlElement(name = "type")
    @JsonProperty("type")
    String type;
    
    @XmlElement(name = "message")
    @JsonProperty("message")
    String message;
    
    public ErrorResponse(int statusCode, String type, String message) {
        this.statusCode = statusCode;
        this.type = type;
        this.message = message;
    }
    
    public ErrorResponse() {
    }
    
}
