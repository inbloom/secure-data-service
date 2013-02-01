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
package org.slc.sli.aspect;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.aspect.ApiMigrationAspect.MigratePostedEntity;
import org.slc.sli.aspect.ApiMigrationAspect.MigrateResponse;

/**
 * Test for ApiMigrationAspect
 *
 * @author ecole
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class ApiMigrationAspectTest {

    @Test
    public void testDownTransform() {
        Resource testResource = new Resource("v1", "students");
        ArrayList<EntityBody> bodyList = new ArrayList<EntityBody>();
        EntityBody body = new EntityBody();
        body.put("entityType", "student");
        bodyList.add(body);
        ServiceResponse testResponse = doMigrateResponse(testResource, bodyList);
        assertTrue(testResponse.getEntityBodyList().get(0).get("downFoo").equals("downBar"));

    }

    @Test
    public void testUpTransform() {
        EntityBody body = new EntityBody();
        Resource testResource = new Resource("v1", "students");
        body.put("entityType", "student");
        body = doMigratePostedEntity(testResource, body);
        assertTrue(body.get("upFoo").equals("upBar"));
    }

    @MigrateResponse
    private ServiceResponse doMigrateResponse(Resource resource, List<EntityBody> bodyList) {
        return new ServiceResponse(bodyList, bodyList.size());
    }

    @MigratePostedEntity
    private EntityBody doMigratePostedEntity(Resource resource, EntityBody body) {
        return body;
    }
}
