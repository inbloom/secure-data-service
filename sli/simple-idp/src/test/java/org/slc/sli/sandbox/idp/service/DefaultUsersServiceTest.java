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

package org.slc.sli.sandbox.idp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import org.slc.sli.sandbox.idp.service.DefaultUsersService.Dataset;
import org.slc.sli.sandbox.idp.service.DefaultUsersService.DefaultUser;

/**
 * Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultUsersServiceTest {

    DefaultUsersService service = new DefaultUsersService();

    @Test
    public void testGetAvailableDatasets() {
        service.setDatasetList("one,List One,two,List Two");
        service.initDatasets();

        List<Dataset> result = service.getAvailableDatasets();
        assertEquals(2, result.size());

        assertEquals("List One",result.get(0).getDisplayName());
        assertEquals("one",result.get(0).getKey());
        assertEquals("List Two",result.get(1).getDisplayName());
        assertEquals("two",result.get(1).getKey());
    }

    @Test
    public void testGetSmallDatasetUsers() throws IOException, JSONException {
        service.setDatasetList("TestDataset,The Test Dataset");
        service.initDatasets();
        service.initUserLists();
        List<DefaultUser> users = service.getUsers("TestDataset");
        assertNotNull(users);
        assertEquals(2, users.size());

        DefaultUser lindaKim = users.get(0);
        DefaultUser rrogers = users.get(1);
        assertEquals("linda.kim", lindaKim.getUserId());
        assertEquals("Linda Kim", lindaKim.getName());
        assertEquals("Educator", lindaKim.getRole());
        assertEquals("school", lindaKim.getAssociation());
        assertEquals("Teacher", lindaKim.getType());

        assertEquals("rrogers", rrogers.getUserId());
        assertEquals("Rick Rogers", rrogers.getName());
        assertEquals("IT Administrator", rrogers.getRole());
        assertEquals("sea", rrogers.getAssociation());
        assertEquals("Staff", rrogers.getType());
    }

    @Test
    public void testGetUser() {
        service.setDatasetList("TestDataset,The Test Dataset");
        service.initDatasets();
        service.initUserLists();
        DefaultUser user = service.getUser("TestDataset", "linda.kim");
        assertEquals("linda.kim", user.getUserId());
    }
}
