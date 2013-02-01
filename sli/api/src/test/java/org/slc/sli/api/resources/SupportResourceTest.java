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


package org.slc.sli.api.resources;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.enums.Right;

/**
 * Test for support email
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class SupportResourceTest {
    
    @Autowired
    private SupportResource resource;
    
    @Autowired
    private SecurityContextInjector injector;
    
    @Value("${sli.support.email}")
    private String email;
    
    @Test
    public void testGetEmailFailure() throws Exception {
        assertNotNull(resource);
        AnonymousAuthenticationToken anon = new AnonymousAuthenticationToken("anon", "anon", Arrays.<GrantedAuthority>asList(Right.ANONYMOUS_ACCESS));
        anon.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(anon);
        try {
            resource.getEmail();
            assertFalse(true);
        } catch (InsufficientAuthenticationException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testGetEmailPass() throws Exception {
        injector.setEducatorContext();
        Map<String, String> returned = (Map<String, String>) resource.getEmail();
        assertTrue(returned.get("email").equals(email));
    }
    
    public void setResource(SupportResource resource) {
        this.resource = resource;
    }
}
