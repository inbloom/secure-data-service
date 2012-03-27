package org.slc.sli.util.logging;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomPatternLogTest {

	private Logger LOG = LoggerFactory.getLogger( CustomPatternLogTest.class );
	
	@Test
	public void testSomething() {
		LOG.warn( "Something fishy" );
		
	}
	
}
