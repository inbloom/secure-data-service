package org.slc.sli.modeling.tools.wadl2Doc;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class Wadl2DocTest {

	@Test (expected = UnsupportedOperationException.class)
	public void testNonInstantiable() {
		new Wadl2Doc();
	}
	
	@Test
	public void testMain() {
		String inputFilename = "src/test/resources/scaffold_eapplication.wadl";
    	String outputFilename = "cleaned_domain_SLI.wadl";
    	
    	String[] args = new String[]{inputFilename, outputFilename};
    	
    	Wadl2Doc.main(args);
    	
    	File file = new File(outputFilename);
    	assertTrue(file.exists());
    	file.delete();
	}
}
