package org.slc.sli.util.logging;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple test to help debug the common pattern.
 * 
 * @author smelody
 * 
 */
public class CommonPatternLogTest {
    
    private Logger LOG = LoggerFactory.getLogger(CommonPatternLogTest.class);
    
    @Test
    public void testSomething() {
        LOG.warn("Something fishy");
        
    }
    
}
