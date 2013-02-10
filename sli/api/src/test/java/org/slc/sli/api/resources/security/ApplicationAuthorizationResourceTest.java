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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.context.validator.ValidatorTestHelper;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.sun.jersey.core.spi.factory.ResponseImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class ApplicationAuthorizationResourceTest {

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    private ValidatorTestHelper helper;
    
    @Autowired
    private ApplicationAuthorizationResource res;
    
    @Autowired
    private SecurityContextInjector injector;
    
    Entity sea, lea1, lea2, school11, school12, school21, school22;

    @Before
    public void setup() {
    	repo.deleteAll("application", null);
        injector.setAdminContextWithElevatedRights();
        sea = helper.generateEdorgWithParent(null);
        lea1 = helper.generateEdorgWithParent(sea.getEntityId());
        lea2 = helper.generateEdorgWithParent(sea.getEntityId());
        school11 = helper.generateEdorgWithParent(lea1.getEntityId());
        school12 = helper.generateEdorgWithParent(lea1.getEntityId());
        school21 = helper.generateEdorgWithParent(lea2.getEntityId());
        school22 = helper.generateEdorgWithParent(lea2.getEntityId());
        SecurityUtil.getSLIPrincipal().setEdOrgId(lea1.getEntityId());
        
    }
    
    @After
    public void cleanup() throws Exception {
        helper.resetRepo();
        repo.deleteAll("applicationAuthorization", new NeutralQuery());
    }
    
    @Test
    public void testGetAuthForNonExistingAppAndNonExistingAuth() {
        ResponseImpl resp = (ResponseImpl) res.getAuthorization("someAppId", null);
        Assert.assertEquals(404, resp.getStatus());
    }

    @Test
    //Authorized for some other edorg
    public void testGetAuthForNonExistingAppAndExistingAuth() {
        Map<String, Object> auth = new HashMap<String, Object>();
        auth.put("applicationId", "someAppId");
        auth.put("edorgs", Arrays.asList("someOtherEdorg"));
        repo.create("applicationAuthorization", auth);
        ResponseImpl resp = (ResponseImpl) res.getAuthorization("someAppId", null);
        Assert.assertEquals(200, resp.getStatus());
        Map ent = (Map) resp.getEntity();
        Assert.assertFalse((Boolean) ent.get("authorized"));
    }
    
    @Test
    //Authorized for my edorg
    public void testGetAuthForNonExistingAppAndExistingAuth2() {
        Map<String, Object> auth = new HashMap<String, Object>();
        auth.put("applicationId", "someAppId");
        auth.put("edorgs", Arrays.asList(SecurityUtil.getEdOrgId()));
        repo.create("applicationAuthorization", auth);
        ResponseImpl resp = (ResponseImpl) res.getAuthorization("someAppId", null);
        Assert.assertEquals(200, resp.getStatus());
        Map ent = (Map) resp.getEntity();
        Assert.assertTrue((Boolean) ent.get("authorized"));
    }
    
    @Test
    public void testGetAuthForExistingAppAndNonExistingAuth() {
        Map<String, Object> appBody = new HashMap<String, Object>();
        Entity app = repo.create("application", appBody);
        ResponseImpl resp = (ResponseImpl) res.getAuthorization(app.getEntityId(), null);
        Assert.assertEquals(200, resp.getStatus());
        Map ent = (Map) resp.getEntity();
        Assert.assertFalse((Boolean) ent.get("authorized"));
        
    }
    
    @Test
    //Authorized for my edorg
    public void testGetAuthForExistingAppAndExistingAuth() {
        Map<String, Object> appBody = new HashMap<String, Object>();
        Entity app = repo.create("application", appBody);
        Map<String, Object> auth = new HashMap<String, Object>();
        auth.put("applicationId", app.getEntityId());
        auth.put("edorgs", Arrays.asList(SecurityUtil.getEdOrgId()));
        repo.create("applicationAuthorization", auth);
        ResponseImpl resp = (ResponseImpl) res.getAuthorization(app.getEntityId(), null);
        Assert.assertEquals(200, resp.getStatus());
        Map ent = (Map) resp.getEntity();
        Assert.assertTrue((Boolean) ent.get("authorized"));
    }
    
    @Test
    //Authorized for other edorg
    public void testGetAuthForExistingAppAndExistingAuth2() {
        Map<String, Object> appBody = new HashMap<String, Object>();
        Entity app = repo.create("application", appBody);
        Map<String, Object> auth = new HashMap<String, Object>();
        auth.put("applicationId", app.getEntityId());
        auth.put("edorgs", Arrays.asList("someOtherEdorg"));
        repo.create("applicationAuthorization", auth);
        ResponseImpl resp = (ResponseImpl) res.getAuthorization(app.getEntityId(), null);
        Assert.assertEquals(200, resp.getStatus());
        Map ent = (Map) resp.getEntity();
        Assert.assertFalse((Boolean) ent.get("authorized"));
    }
    
    @Test
    //Authorized for other edorg
    public void testGetAuthForExistingAppAndExistingAuthAndEdorg() {
        Map<String, Object> appBody = new HashMap<String, Object>();
        Entity app = repo.create("application", appBody);
        Map<String, Object> auth = new HashMap<String, Object>();
        auth.put("applicationId", app.getEntityId());
        auth.put("edorgs", Arrays.asList("someOtherEdorg"));
        repo.create("applicationAuthorization", auth);
        ResponseImpl resp = (ResponseImpl) res.getAuthorization(app.getEntityId(), SecurityUtil.getEdOrgId());
        Assert.assertEquals(200, resp.getStatus());
        Map ent = (Map) resp.getEntity();
        Assert.assertFalse((Boolean) ent.get("authorized"));
    }
    
    @Test
    //Authorized for my edorg
    public void testUpdate() {
        //create app
        Map<String, Object> appBody = new HashMap<String, Object>();
        Entity app = repo.create("application", appBody);
        
        //create app auth
        Map<String, Object> auth = new HashMap<String, Object>();
        auth.put("applicationId", app.getEntityId());
        auth.put("edorgs", Arrays.asList(SecurityUtil.getEdOrgId()));
        repo.create("applicationAuthorization", auth);
        
        //query app auth
        ResponseImpl resp = (ResponseImpl) res.getAuthorization(app.getEntityId(), null);
        Assert.assertEquals(200, resp.getStatus());
        Map ent = (Map) resp.getEntity();
        Assert.assertTrue((Boolean) ent.get("authorized"));

        //Set authorized to false
        EntityBody body = new EntityBody();
        body.put("authorized", false);
        body.put("applicationId", app.getEntityId());
        res.updateAuthorization(app.getEntityId(),body, null);
        
        //Re-query auth
        resp = (ResponseImpl) res.getAuthorization(app.getEntityId(), null);
        Assert.assertEquals(200, resp.getStatus());
        ent = (Map) resp.getEntity();
        Assert.assertFalse((Boolean) ent.get("authorized"));
    }
    
    @Test
    public void testGetAuths() {
        Map<String, Object> appBody = new HashMap<String, Object>();
        Entity app = repo.create("application", appBody);
        Map<String, Object> auth = new HashMap<String, Object>();
        auth.put("applicationId", app.getEntityId());
        auth.put("edorgs", Arrays.asList(SecurityUtil.getEdOrgId()));
        repo.create("applicationAuthorization", auth);
        ResponseImpl resp = (ResponseImpl) res.getAuthorizations(null);
        Assert.assertEquals(200, resp.getStatus());
        List ents = (List) resp.getEntity();
        Assert.assertEquals(1, ents.size());
    }
    
    @Test(expected=AccessDeniedException.class)
    public void testGetAuthForBadEdorg() {
        ResponseImpl resp = (ResponseImpl) res.getAuthorization("someAppId", "badEdorgId");
    }
    
}
