/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.resources.security;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Content;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.DOMBuilder;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.jdom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.slc.sli.api.security.SecurityEventBuilder;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.util.logging.SecurityEvent;

/**
 * Unit tests for the Saml Federation Resource class.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class SamlFederationResourceTest {

    @Autowired
    SamlFederationResource resource;

    public static SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");

    @Test
    public void getMetadataTest() {
        Response response = resource.getMetadata();
        Assert.assertNotNull(response);
        Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
        Assert.assertNotNull(response.getEntity());

        Exception exception = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader((String) response.getEntity()));
            org.w3c.dom.Document doc = db.parse(is);
            DOMBuilder builder = new DOMBuilder();
            org.jdom.Document jdomDocument = builder.build(doc);
            Iterator<Element> itr = jdomDocument.getDescendants(new ElementFilter());

            while (itr.hasNext()) {
                Element el = itr.next();
                if(el.getName().equals("X509Certificate")) {
                    Assert.assertNotNull(el.getText());
                }
            }
        } catch (ParserConfigurationException e) {
            exception = e;
        } catch (SAXException e) {
            exception = e;
        } catch (IOException e) {
            exception = e;
        }
        Assert.assertNull(exception);

    }

    @Test (expected = AccessDeniedException.class)
    public void consumeBadSAMLDataTest() {
        String postData = "badSAMLData";

        Exception exception = null;
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        SecurityEventBuilder securityEventBuilder = Mockito.mock(SecurityEventBuilder.class);
        resource.setSecurityEventBuilder(securityEventBuilder);
        SecurityEvent event = new SecurityEvent();
        Mockito.when(securityEventBuilder.createSecurityEvent(any(String.class), any(URI.class), any(String.class), anyBoolean())).thenReturn(event);
        resource.consume(postData, uriInfo);
        Mockito.verify(securityEventBuilder, times(1)).createSecurityEvent(any(String.class), any(URI.class), any(String.class), anyBoolean());
    }

}
