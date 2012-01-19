package org.slc.sli.api.security;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.domain.Entity;

/**
 * Tests for context based permission
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@DirtiesContext
public class ContextBasedPermissionsTest {
    
    @Autowired
    private ContextResolverStore resolverStore;
    
    @Ignore
    @Test
    public void testTeacherCanAccessAnotherTeacherAtSameSchool() {
        Entity principalEntity = null; // TODO: is teacher
        Entity requestEntity = null; // TODO: is another teacher at same school
        Assert.assertTrue(resolverStore.hasPermission(principalEntity, requestEntity));
    }
}
