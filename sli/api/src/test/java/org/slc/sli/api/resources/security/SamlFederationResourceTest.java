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

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Repository;

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

    public final static String EDORG_REF = "educationOrganizationReference";
    private final static String testStaffId = "staff1";

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
            db.parse(is);
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
        resource.consume(postData, uriInfo);
    }

    @Test
    public void matchRoleTest() throws ParseException {
        Set<String> samlRoles = new HashSet<String>();
        samlRoles.add("teacher");
        samlRoles.add("admin");
        samlRoles.add("principal");
        samlRoles.add("terminator");
        samlRoles.add("nobody");

        Date testDate = ft.parse("08/04/2012 11:49:00 AM");

        Entity staff = setupSEOAs();

        Set<String> matchedRoles = resource.matchRoles(staff.getEntityId(), samlRoles, testDate);
        Set<String> expectedRoles = new HashSet<String>();
        expectedRoles.add("teacher");
        expectedRoles.add("principal");
        expectedRoles.add("terminator");

        Assert.assertTrue(expectedRoles.equals(matchedRoles));
    }

    Map<String, Object> createSEOA(String classification, String edorg, String staff, String endDate) {
        Map<String, Object> seoa = new HashMap<String, Object>();
        seoa.put("staffClassification", classification);
        seoa.put(EDORG_REF, "edorg1");
        seoa.put(ParameterConstants.STAFF_REFERENCE, staff);
        if(endDate != null)
            seoa.put(ParameterConstants.STAFF_EDORG_ASSOC_END_DATE, endDate);

        return seoa;
    }

    private Entity setupSEOAs() {
        Repository repo = resource.getRepository();
        Map<String, Object> staff = new HashMap<String, Object>();
        staff.put("staffUniqueStateId", testStaffId);
        repo.create(EntityNames.STAFF, staff);

        NeutralQuery staffQuery = new NeutralQuery();
        staffQuery.addCriteria(new NeutralCriteria(ParameterConstants.STAFF_UNIQUE_STATE_ID, NeutralCriteria.OPERATOR_EQUAL, testStaffId));

        Entity staffEntity = (Entity)repo.findOne("staff", staffQuery);
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, createSEOA("teacher", "edorg1", staffEntity.getEntityId(), "2012-08-04"));
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, createSEOA("admin", "edorg1", staffEntity.getEntityId(), "2011-08-04"));
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, createSEOA("teacher", "edorg1", staffEntity.getEntityId(), "2012-08-05"));
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, createSEOA("principal", "edorg2", staffEntity.getEntityId(), "2013-08-04"));
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, createSEOA("prophet", "edorg2", staffEntity.getEntityId(), "2013-08-04"));
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, createSEOA("nobody", "edorg2", staffEntity.getEntityId(), "201308-04"));
        repo.create(EntityNames.STAFF_ED_ORG_ASSOCIATION, createSEOA("terminator", "edorg2", staffEntity.getEntityId(), null));

        return staffEntity;
    }

}
