package org.slc.sli.modeling.tools.wadl2Doc;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class WadlViewerTest {

	@Test (expected = UnsupportedOperationException.class)
	public void testNonInstantiable() {
		new WadlViewer();
	}
	
	@Test
	public void testMain() {
		final StringBuffer stringBuffer = new StringBuffer();
		PrintStream stdOut = System.out;
		PrintStream myOut = new PrintStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				stringBuffer.append((char) b);
			}
			
		});
		
		System.setOut(myOut);
		WadlViewer.main(new String[]{"src/test/resources/domain_SLI.wadl"});
		System.setOut(stdOut);
		
		assertFalse(stringBuffer.toString().equals(""));
	}
}
