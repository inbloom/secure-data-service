/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests for CustomRoleResource
 * @author jnanney
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class CustomRoleResourceTest {

    @Autowired
    private SecurityContextInjector injector;

    @Before
    public void setUp() {
        injector.setRealmAdminContext();
    }

    @Test
    public void testValidCreate() {
        
    }

    @Test
    public void testValidUpdate() {
        
    }
    
    @Test
    public void testReadAll() {
        
    }
    
    @Test
    public void testReadAccessible() {
    }
    
    @Test
    public void testReadInaccessible() {
        
    }
    
    @Test
    public void testUpdateWithDuplicateRoles() {
        
    }
    
    @Test
    public void testUpdateWithInvalidRight() {
        
    }
    
    @Test
    public void testUpdateWithInvalidRealmId() {
        
    }
    
    @Test
    public void testUpdateRealmId() {
        
    }
    
    @Test
    public void testCreateWithDuplicateRoles() {
        
    }
    
    @Test
    public void testCreateWithInvalidRight() {
        
    }
    
    @Test
    public void testCreateWithInvalidRealmId() {
        
    }
    
    @Test
    public void testCreateDuplicate() {
        
    }

}
