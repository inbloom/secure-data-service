package net.wgen.wgenhq;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

@WebService(targetNamespace = "https://dcoleman-wks2.wgenhq.net:8443/sftest/", name = "WGenSimpleAdAuthSoap")
@XmlSeeAlso({com.sforce.soap.authentication.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface WGenSimpleAdAuthSoap {

    @WebResult(name = "AuthenticateResult", targetNamespace = "urn:authentication.soap.sforce.com", partName = "parameters")
    @WebMethod(operationName = "Authenticate")
    public com.sforce.soap.authentication.AuthenticateResult authenticate(
        @WebParam(partName = "parameters", name = "Authenticate", targetNamespace = "urn:authentication.soap.sforce.com")
        com.sforce.soap.authentication.Authenticate parameters
    );
}