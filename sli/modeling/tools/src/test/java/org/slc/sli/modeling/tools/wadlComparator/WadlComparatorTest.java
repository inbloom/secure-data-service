package org.slc.sli.modeling.tools.wadlComparator;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class WadlComparatorTest {

	@Test (expected = UnsupportedOperationException.class)
	public void testNonInstantiable() {
		new WadlComparator();
	}
	@Test
	public void testMain() throws IOException {

        String pathToGoldenWadl = "src/test/resources/domain_SLI.wadl";
        String pathToGeneratedWadl = "src/test/resources/scaffold_eapplication.wadl";
        String pathToReportFile = "WadlComparatorOutput.txt";
        
        WadlComparator.main(new String[]{pathToGoldenWadl, pathToGeneratedWadl, pathToReportFile});
        File file = new File(pathToReportFile);
        assertTrue(file.exists());
        file.delete();

	}
}
