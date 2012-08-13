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

package org.slc.sli.sif.slcinterface;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer; 
import org.mockito.invocation.InvocationOnMock; 
import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.Link;
import org.slc.sli.api.client.SLIClientException;
import org.slc.sli.api.client.util.Query;

/**
 * SifIdResolverImplFile unit tests.
 *
 * @author syau
 *
 */
public class SifIdResolverImplFileTest {

    @InjectMocks
    SifIdResolverImplFile resolver = new SifIdResolverImplFile();

    @Mock
    SlcInterface slcInterface;

    Entity dummyStudentEntity;
    Entity dummySchoolEntity;
    Map<String, Object> dummyStudentData;
    
    @Before
    public void beforeTests() throws URISyntaxException, IOException, SLIClientException {
        MockitoAnnotations.initMocks(this);

        dummyStudentData = new HashMap<String, Object>();
        dummyStudentData.put("studentUniqueStateId", "bootstrapturner");
        
        dummyStudentEntity = new Entity() {
            public Map<String, Object> getData() { return dummyStudentData; }
            public String getEntityType() { return "student"; }
            public String getId() { return "id-of-dummy-student"; }
            public List<Link> getLinks() { return new ArrayList<Link>(); }
        };
        dummySchoolEntity = new Entity() {
            public Map<String, Object> getData() { return null; }
            public String getEntityType() { return "educationOrganization"; }
            public String getId() { return "id-of-dummy-school"; }
            public List<Link> getLinks() { return new ArrayList<Link>(); }
        };
        
        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                List<Entity> l = (List<Entity>) args[0];
                String type = (String) args[1];
                if (type.equals("student")) {
                    l.add(dummyStudentEntity);
                }
                if (type.equals("educationOrganization")) {
                    l.add(dummySchoolEntity);
                }
                return null;
            }}).when(slcInterface).read(Mockito.any(List.class), 
                                        Mockito.anyString(), 
                                        Mockito.any(Query.class));

        resolver.idmap = "default-idmap.csv";
        resolver.zonemap = "default-zonemap.csv";
        resolver.init();
    }

    @Test
    public void testGetSliGuid() {
        String sliGuid = resolver.getSliGuid("201208089D75101A8C3D00AA001A1652"); // flying dutchman school. 
        Assert.assertEquals(sliGuid, "id-of-dummy-school");
    }

    @Test
    public void testGetSliEntity() {
        Entity entity = resolver.getSliEntity("20120808934983498C3D00AA00495948"); // bootstrap turner student 
        Assert.assertNotNull(entity);
        Map<String, Object> data = entity.getData();
        Assert.assertNotNull(data);
        Assert.assertTrue(data.containsKey("studentUniqueStateId"));
        Assert.assertEquals("bootstrapturner", data.get("studentUniqueStateId"));
    }

    @Test
    public void testGetNonExistent() {
        Entity entity = resolver.getSliEntity("XYZ");  
        Assert.assertNull(entity);
    }

}
