package org.slc.sli.aspect;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class ApiMigrationAspectTest {

    @Before
    public void setup()throws Exception {
    }

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