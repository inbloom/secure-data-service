package org.slc.sli.modeling.docgen;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

public class DocumentationWriterTest {

	private final static String XMI_FILENAME = "src/test/resources/SLI.xmi";
	private final static String DOMAIN_FILENAME = "src/test/resources/domains.xml";
	private final static String OUTPUT_FILENAME1 = "output1.txt";
	private final static String OUTPUT_FILENAME2 = "output2.txt";
	
	
	
	private final static File XMI_FILE = new File(XMI_FILENAME);
	private final static File DOMAIN_FILE = new File(DOMAIN_FILENAME);
	
	

	
	@Test
	public void test() throws FileNotFoundException {
		ModelIndex modelIndex = new DefaultModelIndex(XmiReader.readModel(XMI_FILE));
		Documentation<Type> documentation = DocumentationReader.readDocumentation(DOMAIN_FILE, modelIndex);

		DocumentationWriter.writeDocument(documentation, modelIndex, OUTPUT_FILENAME1);
		DocumentationWriter.writeDocument(documentation, modelIndex, new File(OUTPUT_FILENAME2));

		File outputFile1 = new File(OUTPUT_FILENAME1);
		File outputFile2 = new File(OUTPUT_FILENAME2);

		assertTrue(outputFile1.exists());
		if (!outputFile2.exists()) {
			outputFile1.delete();
			fail();
		}

		long outputFile1Length = outputFile1.length();
		long outputFile2Length = outputFile2.length();

		outputFile1.delete();
		outputFile2.delete();
		
		assertTrue(outputFile1Length == outputFile2Length);
		
	}
	
	
	@Test (expected = UnsupportedOperationException.class)
	public void testNonInstantiable() {
		new DocumentationWriter();
	}
}
