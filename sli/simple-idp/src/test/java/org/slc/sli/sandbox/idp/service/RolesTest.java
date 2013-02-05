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


package org.slc.sli.sandbox.idp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.sandbox.idp.service.RoleService.Role;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class RolesTest {
    
    RoleService roleService = new RoleService();
    
    @Test
    public void testGetAvailableRoles() {
        List<Role> roles = roleService.getAvailableRoles();
        assertNotNull(roles);
        assertEquals(4, roles.size());
        
        Set<String> values = new HashSet<String>();
        for (Role role : roles) {
            values.add(role.getName());
            values.add(role.getId());
        }
        assertTrue(values.contains("IT Administrator"));
        assertTrue(values.contains("Educator"));
        assertTrue(values.contains("Aggregate Viewer"));
        assertTrue(values.contains("Leader"));
    }
}
