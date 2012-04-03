package org.slc.sli.util;

import org.junit.Test;

/**
 * JUnit test class for DashboardException
 * 
 * @author Takashi Osako
 * 
 */
public class DashboardExceptionTest {
    
    /**
     * intentionally throw exception
     */
    @Test(expected = DashboardException.class)
    public void test() {
        throw new DashboardException("JUnit test");
    }
    
}
