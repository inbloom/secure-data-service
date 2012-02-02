package org.slc.sli.api.security;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import org.slc.sli.domain.enums.Right;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.api.util.SecurityUtil.SecurityTask;

/**
 * Tests sudo runs
 * Validates that while running the process has elevated privilege
 * and that once done, original privileges are restored
 * 
 * @author dkornishev
 * 
 */
public class SecurityUtilTest {
    
    @Test
    public void testSudo() {
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        SecurityUtil.sudoRun(new SecurityTask<String>() {
            @Override
            public String execute() {
                Assert.assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(Right.FULL_ACCESS));
                return "";
            }
        });
        
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
