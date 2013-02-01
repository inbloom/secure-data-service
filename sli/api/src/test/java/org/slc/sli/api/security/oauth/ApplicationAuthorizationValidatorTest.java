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


package org.slc.sli.api.security.oauth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Tests authorized applications.
 *
 *
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class ApplicationAuthorizationValidatorTest {

    @Autowired
    ApplicationAuthorizationValidator validator;

    @Autowired
    private Repository<Entity> repo;

    Entity lea1 = null;
    Entity sea1 = null;
    Entity staff1 = null;
    Entity adminApp = null;
    Entity autoApp = null;
    Entity approvedApp = null;
    Entity noAuthApp = null;
    Entity nonApprovedApp = null;
    Entity notAuthorizedApp = null;
    Entity leaRealm = null;
    Entity sliRealm = null;

    @Before
    public void setup() {

        HashMap<String, Object> body = null;

        //Create edorgs
        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("State Education Agency"));
        sea1 = repo.create("educationOrganization", body);

        body = new HashMap<String, Object>();
        body.put("organizationCategories", Arrays.asList("Local Education Agency"));
        body.put("parentEducationAgencyReference", sea1.getEntityId());
        lea1 = repo.create("educationOrganization", body);

        //Create a staff associated with the LEA
        body.put("staffUniqueStateId", "staff1");
        staff1 = repo.create("staff", body);

        body = new HashMap<String, Object>();
        body.put("educationOrganizationReference", lea1.getEntityId());
        body.put("staffReference", staff1.getEntityId());
        repo.create("staffEducationOrganizationAssociation", body);

        //Create an app admin app - admin_visible = true
        body = new HashMap<String, Object>();
        body.put("name", "Admin App");
        body.put("authorized_for_all_edorgs", false);
        body.put("allowed_for_all_edorgs", false);
        body.put("admin_visible", true);
        adminApp = repo.create("application", body);

        //Create an auto allowed/authorized app
        body = new HashMap<String, Object>();
        body.put("name", "Auto App");
        body.put("authorized_for_all_edorgs", true);
        body.put("allowed_for_all_edorgs", true);
        body.put("admin_visible", false);
        autoApp = repo.create("application", body);

        //Create a normal app that's approved and authorized
        body = new HashMap<String, Object>();
        body.put("name", "Approved App");
        body.put("authorized_ed_orgs", Arrays.asList(lea1.getEntityId()));
        approvedApp = repo.create("application", body);

        //Create a normal app that's authorized by the edorg but not approved by developer
        body = new HashMap<String, Object>();
        body.put("name", "App No EdOrgs");
        body.put("authorized_ed_orgs", new ArrayList());
        nonApprovedApp = repo.create("application", body);

        //Create a normal app that's approved by the developer but not authorized by edorg
        body = new HashMap<String, Object>();
        body.put("name", "App No EdOrgs");
        body.put("authorized_ed_orgs", Arrays.asList(lea1.getEntityId()));
        notAuthorizedApp = repo.create("application", body);

        //Create a normal app that's not authorized for any edorgs and not authorized by any edorgs
        body = new HashMap<String, Object>();
        body.put("name", "App No Auth");
        body.put("authorized_ed_orgs", new ArrayList());
        noAuthApp = repo.create("application", body);

        body = new HashMap<String, Object>();
        body.put("authId", lea1.getEntityId());
        body.put("authType", "EDUCATION_ORGANIZATION");
        body.put("appIds", Arrays.asList(approvedApp.getEntityId(), nonApprovedApp.getEntityId()));

        repo.create("applicationAuthorization", body);

        body = new HashMap<String, Object>();
        leaRealm = repo.create("realm", body);

        body = new HashMap<String, Object>();
        body.put("admin", true);
        sliRealm = repo.create("realm", body);
    }

    @Test
    public void testStaffUser() throws InterruptedException {
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEntity(staff1);
        principal.setEdOrg(lea1.getEntityId());
        principal.setRealm(leaRealm.getEntityId());
        List<String> ids = validator.getAuthorizedApps(principal);

        assertTrue("Can see autoApp", ids.contains(autoApp.getEntityId()));
        assertTrue("Can see approvedApp", ids.contains(approvedApp.getEntityId()));
        assertFalse("Cannot see adminApp", ids.contains(adminApp.getEntityId()));
        assertFalse("Cannot see noAuthApp", ids.contains(noAuthApp.getEntityId()));
        assertFalse("Cannot see nonApprovedApp", ids.contains(nonApprovedApp.getEntityId()));
        assertFalse("Cannot see notAuthorizedApp", ids.contains(notAuthorizedApp.getEntityId()));
    }

    @Test
    public void testAdminUser() throws InterruptedException {
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEntity(null);
        principal.setEdOrg("SOMETHING");
        principal.setRealm(sliRealm.getEntityId());
        principal.setAdminRealmAuthenticated(true);
        List<String> ids = validator.getAuthorizedApps(principal);

        assertFalse("Cannot see autoApp", ids.contains(autoApp.getEntityId()));
        assertFalse("Cannot see approvedApp", ids.contains(approvedApp.getEntityId()));
        assertTrue("Can see adminApp", ids.contains(adminApp.getEntityId()));
        assertFalse("Cannot see noAuthApp", ids.contains(noAuthApp.getEntityId()));
        assertFalse("Cannot see nonApprovedApp", ids.contains(nonApprovedApp.getEntityId()));
        assertFalse("Cannot see notAuthorizedApp", ids.contains(notAuthorizedApp.getEntityId()));
    }
}
