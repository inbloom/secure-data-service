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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;

/**
 * Unit tests for SessionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class SecuritySessionResourceTest {

    @Autowired
    SecurityContextInjector injector;

    @Autowired
    SecuritySessionResource resource;

    @Test
    public void testSessionEmails() throws Exception {
        buildWithEmailType(Arrays.asList("Work"));
        Map<String, Object> response = (Map<String, Object>) resource.sessionCheck();
        assertEquals("Work@Work.com", response.get("email"));

        buildWithEmailType(Arrays.asList("Organization"));
        response = (Map<String, Object>) resource.sessionCheck();
        assertEquals("Organization@Organization.com", response.get("email"));

        buildWithEmailType(Arrays.asList("Organization", "Work", "Other"));
        response = (Map<String, Object>) resource.sessionCheck();
        assertEquals("Work@Work.com", response.get("email"));

        buildWithEmailType(Arrays.asList("Organization", "Other"));
        response = (Map<String, Object>) resource.sessionCheck();
        assertEquals("Organization@Organization.com", response.get("email"));

        EntityBody body = new EntityBody();
        body.put("name", new ArrayList<String>());
        Entity e = Mockito.mock(Entity.class);
        Mockito.when(e.getBody()).thenReturn(body);
        injector.setCustomContext("MerpTest", "Merp Test", "IL", Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR), e,
                "merpmerpmerp");
        response = (Map<String, Object>) resource.sessionCheck();
        assertNull(response.get("email"));

    }

    private void buildWithEmailType(List<String> types) {
        EntityBody body = new EntityBody();
        Entity e = Mockito.mock(Entity.class);

        List<Object> emails = new ArrayList<Object>();
        for (String type : types) {
            Map<String, String> email = new HashMap<String, String>();
            email.put("emailAddressType", type);
            email.put("emailAddress", type + "@" + type + ".com");
            emails.add(email);
        }
        body.put("electronicMail", emails);
        Mockito.when(e.getBody()).thenReturn(body);
        injector.setCustomContext("MerpTest", "Merp Test", "IL",
                Arrays.asList(SecureRoleRightAccessImpl.EDUCATOR), e, "merpmerpmerp");
    }
}
