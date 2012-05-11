package org.slc.sli.ingestion.landingzone.validation;

import junitx.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests ChecksumValidator.
 * @author srichards
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/validation-context.xml" })
public class ChecksumValidatorTest {
    
    @Autowired
    ChecksumValidator checksumValidator;
    
    @Test
    public void compareIgnoresCase() {
        Assert.assertTrue("Validation fails when case doesn't match.", 
                checksumValidator.checksumsMatch("639e31df6485554cf2f4194f40a157a9", 
                        "639E31DF6485554CF2F4194F40A157A9"));
    }
}
