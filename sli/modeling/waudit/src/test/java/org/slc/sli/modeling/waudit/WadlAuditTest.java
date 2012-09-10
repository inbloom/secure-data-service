package org.slc.sli.modeling.waudit;


import java.io.File;

import org.junit.Test;

public class WadlAuditTest {

	@Test
	public void test() {
		String[] args = new String[] {
				"--outFolder",
				".",
				"--wadlFile",
				"src/test/resources/SLI.wadl",
				"--xmiFile",
				"src/test/resources/SLI.xmi",
				"--documentFile",
				"src/test/resources/documents.xml",
				"--outFile",
				"ebase_wadl.wadl",
		};
		
		WadlAudit.main(args);
		
		File file = new File("ebase_wadl.wadl");
		file.delete();
	}


}
