package org.slc.sli.modeling.tools.edfisli.cmdline;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class EdFiSLICmdLineTest {
	
	@Test (expected = UnsupportedOperationException.class)
	public void testNonInstantiable() {
		new EdFiSLICmdLine();
	}
	
	@Test
	public void testMain() {

		String inputSliXmiFilename = "src/test/resources/SLI.xmi";
		String inputEdfiXmiFilename = "src/test/resources/Ed-Fi-Core.xmi";
		String[] args = new String[]{inputSliXmiFilename, inputEdfiXmiFilename};

		final StringBuffer stringBuffer = new StringBuffer();
		
		PrintStream stdOut = System.out;
		PrintStream myOut = new PrintStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				stringBuffer.append((char) b);
			}
			
		});
		
		System.setOut(myOut);
		EdFiSLICmdLine.main(args);
		System.setOut(stdOut);
		
		assertFalse(stringBuffer.toString().equals(""));
	}
}
