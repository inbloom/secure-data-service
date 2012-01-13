package org.slc.sli.api.security.resolve;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.security.resolve.impl.DefaultSliAdminValidator;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * 
 * @author pwolf
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultSliAdminValidatorTest {
    
    @Autowired
    DefaultSliAdminValidator testValidator = null;
    
    @Test
    public void testSpringWiring() {
        Assert.assertTrue(testValidator.isSliAdminRealm("test"));
    }
    
    @Test
    public void testRealmPatternMatch() {
        SliAdminValidator validator = new DefaultSliAdminValidator("foo");
        Assert.assertTrue(validator.isSliAdminRealm("foo"));
    }
    
    @Test
    public void testEmptyPattern() {
        SliAdminValidator validator = new DefaultSliAdminValidator("");
        Assert.assertFalse(validator.isSliAdminRealm("foo"));
    }
    
    @Test
    public void testRealmPatternNoMatch() {
        SliAdminValidator validator = new DefaultSliAdminValidator("foo");
        Assert.assertFalse(validator.isSliAdminRealm("bar"));
    }
    
    @Test
    public void testRealmWildcardPatternMatch() {
        SliAdminValidator validator = new DefaultSliAdminValidator(".*dc=sli.*,dc=org.*");
        Assert.assertTrue(validator.isSliAdminRealm("adc=slidev,dc=org"));
    }
    
    @Test
    public void testNullRealm() {
        SliAdminValidator validator = new DefaultSliAdminValidator(".*");
        Assert.assertFalse("Tolerate a null realm, but never allow it to match", 
                validator.isSliAdminRealm(null));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNoPattern() {
        SliAdminValidator validator = new DefaultSliAdminValidator(null);
        validator.isSliAdminRealm("dc=slidev,dc=org");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBadRegex() {
        SliAdminValidator validator = new DefaultSliAdminValidator("(foo");
    }

}
