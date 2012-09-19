package org.slc.sli.modeling.docgen;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class DocGenTest {

	private final static String DOMAIN_FILENAME = "src/test/resources/domains.xml";
	private final static String XMI_FILENAME = "src/test/resources/SLI.xmi";
	private final static String OUTPUT_FILENAME = "DocGenTestOutput.txt";
	private final static String OUTPUT_DIRECTORY = "./";
	

	private final static String[] DOC_GEN_TEST_MAIN_ARGS = new String[] {
		"--outFolder",
		OUTPUT_DIRECTORY,
		"--outFile",
		OUTPUT_FILENAME,
		"--xmiFile",
		XMI_FILENAME,
		"--domainFile",
		DOMAIN_FILENAME
	};

	private final static String[] DOC_GEN_TEST_HELP_ARGS_1 = new String[] {
		"-h"
	};

	private final static String[] DOC_GEN_TEST_HELP_ARGS_2 = new String[] {
		"-?"
	};

	@Test
	public void testHelp() {
		this.testHelp(DOC_GEN_TEST_HELP_ARGS_1);
		this.testHelp(DOC_GEN_TEST_HELP_ARGS_2);
	}
	
	private void testHelp(String[] helpArgs) {
		final StringBuffer stringBuffer = new StringBuffer();
		PrintStream stdOut = System.out;
		PrintStream myOut = new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				stringBuffer.append((char) b);
			}
		});
		
		System.setOut(myOut);
		DocGen.main(helpArgs);
		System.setOut(stdOut);
		
		assertFalse(stringBuffer.toString().isEmpty());
	}
	
	@Test (expected = UnsupportedOperationException.class)
	public void testNonInstantiable() {
		new DocGen();
	}
	
	@Test
	public void testMain() {
		File file = new File(OUTPUT_DIRECTORY + OUTPUT_FILENAME);
		if (file.exists()) {
			file.delete();
		}
		DocGen.main(DOC_GEN_TEST_MAIN_ARGS); // should create the file
		assertTrue(file.exists());
		file.delete();
	}
}
