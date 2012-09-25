package org.slc.sli.modeling.tools.edfisli.cmdline;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class WhereUsedCmdLineTest {

	@Test (expected = UnsupportedOperationException.class)
	public void testNonInstantiable() {
		new WhereUsedCmdLine();
	}
	
	@Test
	public void testMain() {
		String inputFilename = "src/test/resources/psm_sli.xmi";
		String name = "assessmentTitle";
		String[] args = new String[]{inputFilename, name};
		
		final StringBuffer stringBuffer = new StringBuffer();
		
		PrintStream stdOut = System.out;
		PrintStream myOut = new PrintStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				stringBuffer.append((char) b);
			}
			
		});
		
		System.setOut(myOut);
		WhereUsedCmdLine.main(args);
		System.setOut(stdOut);
		assertFalse(stringBuffer.toString().equals(""));
	}
}
