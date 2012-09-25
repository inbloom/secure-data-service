package org.slc.sli.modeling.tools.xmi2Psm.cmdline;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class PsmCreatorTest {
	
	@Test
	public void testMain() {
		String inputFilename = "src/test/resources/psm_sli.xmi";
		String outputFilename = "PsmCreatorOutput.xml";
		
		PsmCreator.main(new String[]{inputFilename, outputFilename});
		File file = new File(outputFilename);
		assertTrue(file.exists());
		file.delete();
	}
	
	@Test (expected = UnsupportedOperationException.class)
	public void testNonInstantiable() {
		new PsmCreator();
	}
}
