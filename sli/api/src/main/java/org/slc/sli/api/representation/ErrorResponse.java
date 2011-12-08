package org.slc.sli.api.representation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement(name = "error")
public class ErrorResponse {
    @XmlElement(name = "status-code")
    @JsonProperty("status-code")
    int statusCode;
    @XmlElement(name = "message")
    @JsonProperty("message")
    String message;
    
    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
    
    public ErrorResponse() {
    }
    
}
