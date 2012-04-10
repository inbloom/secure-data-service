package org.slc.sli.sandbox.idp.service;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.sandbox.idp.saml.XmlSignatureHelper;
import org.slc.sli.sandbox.idp.service.AuthRequests.Request;
import org.slc.sli.sandbox.idp.service.Users.User;
import org.w3c.dom.Document;

@RunWith(MockitoJUnitRunner.class)
public class LoginServiceTest {
    
    @Mock
    XmlSignatureHelper signer;
    @InjectMocks
    LoginService login = new LoginService();
    
    @Test
    public void testLogin() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyException,
            TransformerException, MarshalException, XMLSignatureException {
        Document signedDoc = Mockito.mock(Document.class);
        Mockito.when(signer.signSamlAssertion(Mockito.any(Document.class))).thenReturn(signedDoc);
        
        Request request = Mockito.mock(AuthRequests.Request.class);
        Mockito.when(request.getRequestId()).thenReturn("request_id");
        
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn("unique_id");
        Mockito.when(user.getUserName()).thenReturn("Test User");
        
        login.login(user, Arrays.asList("role1", "role2"), request);
    }
}
