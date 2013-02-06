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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

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
import org.springframework.beans.factory.annotation.Autowired;
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
public class ApprovedApplicationResourceTest {
    
    @Autowired
    private ApprovedApplicationResource resource;
    
    @Autowired
    private PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    private ApplicationAuthorizationResource appAuth;
    
    Entity app1 = null;
    
    @Autowired
    SecurityContextInjector injector;
    
    @Autowired
    ValidatorTestHelper helper;
    
    @Before
    public void setup() {
        Entity lea = helper.generateEdorgWithParent(null);
        lea.getBody().put("organizationCategories", Arrays.asList("Local Education Agency"));
        injector.setStaffContext();
        helper.generateStaffEdorg(SecurityUtil.getSLIPrincipal().getEntity().getEntityId(), lea.getEntityId(), false);
        SecurityUtil.getSLIPrincipal().setEdOrgId(lea.getEntityId());
        
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("authorized_ed_orgs", Arrays.asList(SecurityUtil.getEdOrgId()));
        body.put("installed", false);
        body.put("name", "MyApp");
        app1 = repo.create("application", body);
        
        injector.setAdminContextWithElevatedRights();
        SecurityUtil.getSLIPrincipal().setEdOrgId(lea.getEntityId());
        EntityBody auth = new EntityBody();
        auth.put("appId", app1.getEntityId());
        auth.put("authorized", true);
        appAuth.updateAuthorization(app1.getEntityId(), auth, null);
        
        injector.setStaffContext();
        SecurityUtil.getSLIPrincipal().setEdOrgId(lea.getEntityId());
    }
    
    @Test
    public void testGetApps() {


        ResponseImpl resp = (ResponseImpl) resource.getApplications("");
        List<Map> list = (List<Map>) resp.getEntity();
        List<String> names = new ArrayList<String>();
        for (Map map : list) {
            String name = (String) map.get("name");
            names.add(name);
        }
        Assert.assertTrue(names.contains("MyApp"));
    }


}
