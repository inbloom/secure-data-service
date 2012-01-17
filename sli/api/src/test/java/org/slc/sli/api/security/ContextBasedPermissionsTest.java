package org.slc.sli.api.security;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.domain.Entity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
public class ContextBasedPermissionsTest {
    @Test
    public void testNothing() {

    }

    @Test
    public void testTeacherCanAccessAnotherTeacherAtSameSchool() {
        Entity principalEntity = null; //TODO: is teacher
        Entity requestEntity = null; //TODO: is another teacher at same school
        Assert.assertTrue(SecurityContextResolver.hasPermission(principalEntity, requestEntity));
    }
}
